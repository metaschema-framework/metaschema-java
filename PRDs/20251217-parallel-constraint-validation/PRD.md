# Parallel Constraint Validation

## Overview

Add experimental support for executing constraint validations in multiple threads to improve validation performance for large documents.

## Goals

1. **Performance**: Reduce validation time for large documents by parallelizing constraint evaluation across multiple CPU cores
2. **Flexibility**: Support both CLI usage (specify thread count) and service integration (provide managed ExecutorService)
3. **Backward Compatibility**: Default to single-threaded execution; existing behavior unchanged
4. **Correctness**: Maintain identical validation results regardless of thread count

## Non-Goals

- Automatic thread count selection (user must specify)
- Parallelizing schema validation (only constraint validation)
- Distributed validation across multiple machines

## Requirements

### Functional Requirements

1. **CLI Argument**: Add `--threads <count>` option to validation commands
   - Default: 1 (single-threaded, current behavior)
   - Values > 1 enable parallel validation

2. **Service API**: Allow applications to provide a managed `ExecutorService`
   - Enables integration with application thread pools
   - Shared pool can be reused across multiple validations

3. **Parallelization Strategy**:
   - Document-level: Validate multiple documents concurrently
   - Node-level: Validate sibling subtrees within a document concurrently
   - Constraint evaluation on each node remains sequential

4. **Finding Order**: Sort findings by document location before returning (for consistent CLI output)

### Non-Functional Requirements

1. **Thread Safety**: All shared state must be thread-safe
2. **No Deadlocks**: Parallel execution must not introduce deadlock scenarios
3. **Graceful Degradation**: If parallelization fails, fall back to sequential execution

## Technical Design

### Thread Contention Analysis

The following shared state requires thread-safe handling:

| Component | Current State | Issue | Solution |
|-----------|--------------|-------|----------|
| `DefaultConstraintValidator.valueMap` | `LinkedHashMap` | Not thread-safe | `ConcurrentHashMap` |
| `DefaultConstraintValidator.indexNameToKeyRefMap` | `ConcurrentHashMap` with `LinkedList` values | List operations not thread-safe | `computeIfAbsent` + `Collections.synchronizedList` |
| `FindingCollectingConstraintValidationHandler.findings` | `LinkedList` | Not thread-safe | `ConcurrentLinkedQueue` |
| `FindingCollectingConstraintValidationHandler.highestLevel` | `Level` field | Non-atomic updates | `AtomicReference<Level>` |
| `DynamicContext.SharedState.executionStack` | `ArrayDeque` (shared) | Concurrent push/pop corrupts state | Move to per-context (copied from parent) |
| `DynamicContext.SharedState.availableDocuments` | `HashMap` | Race condition in caching | `ConcurrentHashMap` |

### Constraint Dependencies

Some constraints have ordering dependencies:

1. **Index constraints** must complete before **index-has-key** validation in `finalizeValidation()`
2. **Allowed-values** constraints are registered during traversal, validated at end of node visit

The parallel traversal respects these by:
- Running `finalizeValidation()` after all parallel traversal completes
- Keeping per-node constraint evaluation sequential

### API Design

#### ParallelValidationConfig

```java
/**
 * Configuration for parallel constraint validation.
 * <p>
 * This class is thread-safe and immutable.
 */
public final class ParallelValidationConfig {

  /** Single-threaded execution (default, current behavior). */
  public static final ParallelValidationConfig SEQUENTIAL =
      new ParallelValidationConfig(null, 1);

  private final ExecutorService executor;
  private final int threadCount;

  /**
   * Create configuration using an application-provided executor.
   * <p>
   * The executor is NOT shut down by the validator; the caller retains ownership.
   *
   * @param executor the executor service to use for parallel tasks
   * @return configuration using the provided executor
   */
  public static ParallelValidationConfig withExecutor(@NonNull ExecutorService executor);

  /**
   * Create configuration that creates an internal thread pool.
   * <p>
   * The internal pool is shut down after validation completes.
   *
   * @param threadCount number of threads (must be &gt;= 1)
   * @return configuration with internal thread pool
   * @throws IllegalArgumentException if threadCount &lt; 1
   */
  public static ParallelValidationConfig withThreads(int threadCount);

  /**
   * Check if parallel execution is enabled.
   *
   * @return true if using more than one thread
   */
  public boolean isParallel();

  /**
   * Get the executor, creating an internal pool if needed.
   *
   * @return the executor service
   */
  @NonNull
  ExecutorService getOrCreateExecutor();

  /**
   * Shut down internal executor if one was created.
   * Does nothing if using an external executor.
   */
  void shutdownInternalExecutor();
}
```

#### Validator Integration

```java
public class DefaultConstraintValidator {

  /**
   * Construct a validator with parallel execution support.
   *
   * @param handler the validation handler for findings
   * @param parallelConfig parallel execution configuration
   */
  public DefaultConstraintValidator(
      @NonNull IConstraintValidationHandler handler,
      @NonNull ParallelValidationConfig parallelConfig);

  /**
   * Construct a validator with sequential execution (backward compatible).
   *
   * @param handler the validation handler for findings
   */
  public DefaultConstraintValidator(@NonNull IConstraintValidationHandler handler) {
    this(handler, ParallelValidationConfig.SEQUENTIAL);
  }
}
```

### Thread-Safe Shared State

#### DefaultConstraintValidator Changes

```java
public class DefaultConstraintValidator {

  // CHANGED: LinkedHashMap → ConcurrentHashMap
  @NonNull
  private final Map<INodeItem, ValueStatus> valueMap = new ConcurrentHashMap<>();

  // EXISTING: Already ConcurrentHashMap, but list operations need synchronization
  @NonNull
  private final Map<String, List<KeyRef>> indexNameToKeyRefMap = new ConcurrentHashMap<>();

  // CHANGED: Thread-safe collection of KeyRefs
  private void validateIndexHasKey(
      @NonNull IIndexHasKeyConstraint constraint,
      @NonNull IDefinitionNodeItem<?, ?> node,
      @NonNull ISequence<? extends INodeItem> targets) {
    String indexName = constraint.getIndexName();

    List<KeyRef> keyRefItems = indexNameToKeyRefMap.computeIfAbsent(
        indexName,
        k -> Collections.synchronizedList(new ArrayList<>()));

    keyRefItems.add(new KeyRef(constraint, node, new ArrayList<>(targets)));
  }
}
```

#### FindingCollectingConstraintValidationHandler Changes

```java
public class FindingCollectingConstraintValidationHandler {

  // CHANGED: LinkedList → ConcurrentLinkedQueue
  @NonNull
  private final Queue<ConstraintValidationFinding> findings = new ConcurrentLinkedQueue<>();

  // CHANGED: Level → AtomicReference<Level>
  @NonNull
  private final AtomicReference<Level> highestLevel =
      new AtomicReference<>(IConstraint.Level.INFORMATIONAL);

  protected void addFinding(@NonNull ConstraintValidationFinding finding) {
    findings.add(finding);

    Level severity = finding.getSeverity();
    highestLevel.updateAndGet(current ->
        severity.ordinal() > current.ordinal() ? severity : current);
  }

  @Override
  @NonNull
  public Level getHighestSeverity() {
    return highestLevel.get();
  }

  @Override
  @NonNull
  public List<ConstraintValidationFinding> getFindings() {
    // Sort by document location for consistent CLI output
    return findings.stream()
        .sorted(Comparator.comparing(f -> f.getTarget().getMetapath()))
        .collect(Collectors.toUnmodifiableList());
  }
}
```

#### DynamicContext Changes

```java
public class DynamicContext {

  @NonNull
  private final Map<Integer, ISequence<?>> letVariableMap;
  @NonNull
  private final SharedState sharedState;
  // CHANGED: Moved from SharedState to per-context
  @NonNull
  private final Deque<IExpression> executionStack;

  public DynamicContext(@NonNull StaticContext staticContext) {
    this.letVariableMap = new ConcurrentHashMap<>();
    this.sharedState = new SharedState(staticContext);
    this.executionStack = new ArrayDeque<>();
  }

  private DynamicContext(@NonNull DynamicContext context) {
    this.letVariableMap = new ConcurrentHashMap<>(context.letVariableMap);
    this.sharedState = context.sharedState;
    // Copy parent's stack so error traces show full call chain
    this.executionStack = new ArrayDeque<>(context.executionStack);
  }

  private static class SharedState {
    // ... other fields unchanged ...

    // CHANGED: HashMap → ConcurrentHashMap
    @NonNull
    private final Map<URI, IDocumentNodeItem> availableDocuments = new ConcurrentHashMap<>();

    // REMOVED: executionStack (moved to per-context)
  }
}
```

### Parallel Traversal Mechanism

#### Parallel Visitor

```java
class Visitor extends AbstractNodeItemVisitor<DynamicContext, Void> {

  private static final int PARALLEL_THRESHOLD = 4;  // Min children to parallelize

  private final ParallelValidationConfig parallelConfig;

  @Override
  public Void visitAssembly(@NonNull IAssemblyNodeItem item, DynamicContext context) {
    assert context != null;

    IAssemblyDefinition definition = item.getDefinition();
    DynamicContext effectiveContext = handleLetStatements(item, definition.getLetExpressions(), context);

    try {
      validateAssembly(item, effectiveContext);
    } catch (ConstraintValidationException ex) {
      throw ExceptionUtils.wrap(ex);
    }

    // Parallel or sequential child traversal
    if (parallelConfig.isParallel() && shouldParallelize(item)) {
      visitChildrenParallel(item, effectiveContext);
    } else {
      super.visitAssembly(item, effectiveContext);
    }

    return null;
  }

  private boolean shouldParallelize(@NonNull IAssemblyNodeItem item) {
    return item.modelItems().count() >= PARALLEL_THRESHOLD;
  }

  private void visitChildrenParallel(
      @NonNull IAssemblyNodeItem item,
      @NonNull DynamicContext context) {

    ExecutorService executor = parallelConfig.getOrCreateExecutor();
    List<? extends IModelNodeItem<?, ?>> children = item.modelItems().collect(Collectors.toList());

    List<Future<?>> futures = new ArrayList<>(children.size());
    for (IModelNodeItem<?, ?> child : children) {
      futures.add(executor.submit(() -> {
        DynamicContext childContext = context.subContext();
        child.accept(this, childContext);
        return null;
      }));
    }

    // Wait for all children and propagate exceptions
    try {
      for (Future<?> future : futures) {
        future.get();
      }
    } catch (ExecutionException e) {
      cancelRemainingFutures(futures);
      Throwable cause = e.getCause();
      if (cause instanceof RuntimeException) {
        throw (RuntimeException) cause;
      }
      throw new ConstraintValidationException(cause);
    } catch (InterruptedException e) {
      cancelRemainingFutures(futures);
      Thread.currentThread().interrupt();
      throw new ConstraintValidationException("Validation interrupted", e);
    }
  }

  private void cancelRemainingFutures(List<Future<?>> futures) {
    for (Future<?> future : futures) {
      if (!future.isDone()) {
        future.cancel(true);
      }
    }
  }
}
```

### CLI Integration

#### New Option

```java
// In AbstractValidateContentCommand
private static final Option PARALLEL_THREADS_OPTION = Option.builder()
    .longOpt("threads")
    .hasArg()
    .argName("count")
    .type(Number.class)
    .desc("Number of threads for parallel constraint validation (default: 1, experimental)")
    .build();
```

#### Usage in Executor

```java
// In AbstractValidationCommandExecutor
int threadCount = 1;
if (cmdLine.hasOption("threads")) {
  threadCount = ((Number) cmdLine.getParsedOptionValue("threads")).intValue();
  if (threadCount < 1) {
    throw new InvalidArgumentException("Thread count must be at least 1");
  }
}

ParallelValidationConfig parallelConfig = threadCount > 1
    ? ParallelValidationConfig.withThreads(threadCount)
    : ParallelValidationConfig.SEQUENTIAL;

try {
  // ... validation logic using parallelConfig ...
} finally {
  parallelConfig.shutdownInternalExecutor();
}
```

### Execution Flow Diagram

```text
CLI: metaschema-cli validate --threads 4 document.xml

                    ┌─────────────────┐
                    │  Root Assembly  │  (main thread validates root)
                    └────────┬────────┘
                             │
         ┌───────────────────┼───────────────────┐
         ▼                   ▼                   ▼
    ┌─────────┐         ┌─────────┐         ┌─────────┐
    │ Child 1 │         │ Child 2 │         │ Child 3 │   (parallel via thread pool)
    │ subtree │         │ subtree │         │ subtree │
    └─────────┘         └─────────┘         └─────────┘
         │                   │                   │
         └───────────────────┴───────────────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │finalizeValidation│  (main thread, after all traversal)
                    │ (index-has-key) │
                    └─────────────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │  Sort findings  │  (by document location)
                    │  Return results │
                    └─────────────────┘
```

## Testing Strategy

### Unit Tests

1. **Thread Safety Tests**
   - Concurrent access to `valueMap`
   - Concurrent adds to `findings`
   - Concurrent updates to `highestLevel`

2. **Behavioral Equivalence Tests**
   - Same document produces identical findings with 1 vs N threads
   - Finding count matches between sequential and parallel

3. **Configuration Tests**
   - `ParallelValidationConfig` factory methods
   - Executor lifecycle (internal pool shutdown)

### Integration Tests

1. **CLI Tests**
   - `--threads 1` produces same results as no flag
   - `--threads 4` completes successfully
   - Invalid thread count rejected

2. **Large Document Tests**
   - Performance improvement with multiple threads
   - No missing or duplicate findings

## Success Metrics

1. **Correctness**: 100% of existing constraint validation tests pass
2. **Performance**: 2x+ speedup on large documents with 4 threads
3. **Stability**: No deadlocks or race conditions in stress tests

## Risks and Mitigations

| Risk | Likelihood | Impact | Mitigation |
|------|------------|--------|------------|
| Race conditions in shared state | Medium | High | Comprehensive thread-safety review; stress testing |
| Performance regression for small documents | Low | Medium | Parallelize only when children >= threshold |
| Complex debugging | Medium | Medium | Per-context execution stacks; clear error messages |
| Executor resource leaks | Low | Medium | try-finally shutdown pattern; clear ownership semantics |

## Design Decisions

1. **PARALLEL_THRESHOLD**: Fixed at 4 (not configurable). This avoids complexity and provides a reasonable default. Can be made configurable later if users request it.

2. **CLI**: Only `--threads N` option. No `--parallel` shortcut. Explicit thread count is clearer for an experimental feature.

3. **Experimental warning**: Print warning to stderr when `--threads > 1`:
   ```text
   WARNING: Parallel constraint validation (--threads N) is experimental.
            Report issues at https://github.com/metaschema-framework/metaschema-java/issues
   ```
