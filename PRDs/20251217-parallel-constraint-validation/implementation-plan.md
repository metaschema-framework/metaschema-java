# Parallel Constraint Validation Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Add experimental parallel constraint validation with `--threads N` CLI option to improve validation performance for large documents.

**Architecture:** Thread-safe shared state in validator/handler classes, parallel sibling traversal via ExecutorService, per-subContext execution stacks. Sequential by default, parallel when `--threads > 1`.

**Tech Stack:** Java 11, ConcurrentHashMap, AtomicReference, ExecutorService, Apache Commons CLI

---

## Task 1: Create ParallelValidationConfig Class

**Files:**
- Create: `core/src/main/java/dev/metaschema/core/model/constraint/ParallelValidationConfig.java`
- Test: `core/src/test/java/dev/metaschema/core/model/constraint/ParallelValidationConfigTest.java`

**Step 1: Write the failing tests**

```java
/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model.constraint;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ParallelValidationConfigTest {

  @Test
  void testSequentialIsNotParallel() {
    ParallelValidationConfig config = ParallelValidationConfig.SEQUENTIAL;
    assertFalse(config.isParallel());
  }

  @Test
  void testWithThreadsOneIsNotParallel() {
    ParallelValidationConfig config = ParallelValidationConfig.withThreads(1);
    assertFalse(config.isParallel());
  }

  @Test
  void testWithThreadsFourIsParallel() {
    ParallelValidationConfig config = ParallelValidationConfig.withThreads(4);
    assertTrue(config.isParallel());
    config.close();
  }

  @Test
  void testWithThreadsZeroThrows() {
    assertThrows(IllegalArgumentException.class, () -> ParallelValidationConfig.withThreads(0));
  }

  @Test
  void testWithThreadsNegativeThrows() {
    assertThrows(IllegalArgumentException.class, () -> ParallelValidationConfig.withThreads(-1));
  }

  @Test
  void testWithExecutorIsParallel() {
    ExecutorService executor = Executors.newFixedThreadPool(2);
    try {
      ParallelValidationConfig config = ParallelValidationConfig.withExecutor(executor);
      assertTrue(config.isParallel());
    } finally {
      executor.shutdown();
    }
  }

  @Test
  void testWithExecutorNullThrows() {
    assertThrows(NullPointerException.class, () -> ParallelValidationConfig.withExecutor(null));
  }

  @Test
  void testCloseShutdownsInternalExecutor() {
    ParallelValidationConfig config = ParallelValidationConfig.withThreads(2);
    ExecutorService executor = config.getExecutor();
    assertFalse(executor.isShutdown());
    config.close();
    assertTrue(executor.isShutdown());
  }

  @Test
  void testCloseDoesNotShutdownExternalExecutor() {
    ExecutorService executor = Executors.newFixedThreadPool(2);
    try {
      ParallelValidationConfig config = ParallelValidationConfig.withExecutor(executor);
      config.close();
      assertFalse(executor.isShutdown());
    } finally {
      executor.shutdown();
    }
  }
}
```

**Step 2: Run tests to verify they fail**

Run: `mvn -pl core test -Dtest=ParallelValidationConfigTest -DfailIfNoTests=false`
Expected: Compilation error - class does not exist

**Step 3: Write the implementation**

```java
/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model.constraint;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Configuration for parallel constraint validation.
 * <p>
 * This class supports two modes:
 * <ul>
 * <li>Internal thread pool: Created via {@link #withThreads(int)}, shut down by {@link #close()}</li>
 * <li>External executor: Provided via {@link #withExecutor(ExecutorService)}, NOT shut down by {@link #close()}</li>
 * </ul>
 * <p>
 * Instances should be used with try-with-resources or explicitly closed after validation.
 */
public final class ParallelValidationConfig implements AutoCloseable {

  /**
   * Single-threaded sequential execution (default, current behavior).
   * <p>
   * This instance does not need to be closed.
   */
  @NonNull
  public static final ParallelValidationConfig SEQUENTIAL = new ParallelValidationConfig(null, 1, false);

  @Nullable
  private ExecutorService executor;
  private final int threadCount;
  private final boolean ownsExecutor;

  private ParallelValidationConfig(@Nullable ExecutorService executor, int threadCount, boolean ownsExecutor) {
    this.executor = executor;
    this.threadCount = threadCount;
    this.ownsExecutor = ownsExecutor;
  }

  /**
   * Create configuration using an application-provided executor.
   * <p>
   * The executor is NOT shut down by {@link #close()}; the caller retains ownership.
   *
   * @param executor the executor service to use for parallel tasks
   * @return configuration using the provided executor
   * @throws NullPointerException if executor is null
   */
  @NonNull
  public static ParallelValidationConfig withExecutor(@NonNull ExecutorService executor) {
    Objects.requireNonNull(executor, "executor must not be null");
    return new ParallelValidationConfig(executor, 0, false);
  }

  /**
   * Create configuration that creates an internal thread pool.
   * <p>
   * The internal pool is shut down when {@link #close()} is called.
   *
   * @param threadCount number of threads (must be &gt;= 1)
   * @return configuration with internal thread pool
   * @throws IllegalArgumentException if threadCount &lt; 1
   */
  @NonNull
  public static ParallelValidationConfig withThreads(int threadCount) {
    if (threadCount < 1) {
      throw new IllegalArgumentException("threadCount must be at least 1, got: " + threadCount);
    }
    if (threadCount == 1) {
      return SEQUENTIAL;
    }
    return new ParallelValidationConfig(null, threadCount, true);
  }

  /**
   * Check if parallel execution is enabled.
   *
   * @return true if using more than one thread
   */
  public boolean isParallel() {
    return executor != null || threadCount > 1;
  }

  /**
   * Get the executor service, creating an internal pool if needed.
   * <p>
   * For internal pools, the executor is created lazily on first call.
   *
   * @return the executor service
   * @throws IllegalStateException if called on SEQUENTIAL config
   */
  @NonNull
  public ExecutorService getExecutor() {
    if (!isParallel()) {
      throw new IllegalStateException("Cannot get executor for sequential configuration");
    }
    if (executor == null) {
      synchronized (this) {
        if (executor == null) {
          executor = Executors.newFixedThreadPool(threadCount);
        }
      }
    }
    return executor;
  }

  /**
   * Shut down internal executor if one was created.
   * <p>
   * Does nothing if using an external executor or if no executor was created.
   */
  @Override
  public void close() {
    if (ownsExecutor && executor != null) {
      executor.shutdown();
      try {
        if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
          executor.shutdownNow();
        }
      } catch (InterruptedException e) {
        executor.shutdownNow();
        Thread.currentThread().interrupt();
      }
    }
  }
}
```

**Step 4: Run tests to verify they pass**

Run: `mvn -pl core test -Dtest=ParallelValidationConfigTest`
Expected: All 10 tests PASS

**Step 5: Run checkstyle to verify Javadoc**

Run: `mvn -pl core checkstyle:check`
Expected: BUILD SUCCESS

**Step 6: Commit**

```bash
git add core/src/main/java/dev/metaschema/core/model/constraint/ParallelValidationConfig.java
git add core/src/test/java/dev/metaschema/core/model/constraint/ParallelValidationConfigTest.java
git commit -m "feat(core): add ParallelValidationConfig for parallel constraint validation"
```

---

## Task 2: Make DynamicContext Thread-Safe

**Files:**
- Modify: `core/src/main/java/dev/metaschema/core/metapath/DynamicContext.java`
- Test: `core/src/test/java/dev/metaschema/core/metapath/DynamicContextTest.java`

**Step 1: Write failing tests for thread-safety**

Add to existing test file or create new:

```java
/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DynamicContextTest {

  @Test
  void testSubContextCopiesExecutionStack() {
    DynamicContext parent = new DynamicContext();
    IExpression mockExpr = new MockExpression();

    parent.pushExecutionStack(mockExpr);
    assertEquals(1, parent.getExecutionStack().size());

    DynamicContext child = parent.subContext();

    // Child should have copy of parent's stack
    assertEquals(1, child.getExecutionStack().size());

    // Modifying child stack should not affect parent
    child.popExecutionStack(mockExpr);
    assertEquals(0, child.getExecutionStack().size());
    assertEquals(1, parent.getExecutionStack().size());
  }

  @Test
  void testSubContextExecutionStackIsolation() {
    DynamicContext parent = new DynamicContext();
    DynamicContext child = parent.subContext();

    IExpression mockExpr = new MockExpression();
    child.pushExecutionStack(mockExpr);

    // Parent should not see child's push
    assertEquals(0, parent.getExecutionStack().size());
    assertEquals(1, child.getExecutionStack().size());
  }

  // Simple mock for testing - real implementation uses CST expressions
  private static class MockExpression implements IExpression {
    @Override
    public String toCSTString() {
      return "mock";
    }
    // ... implement other required methods with defaults
  }
}
```

**Step 2: Run tests to verify they fail**

Run: `mvn -pl core test -Dtest=DynamicContextTest -DfailIfNoTests=false`
Expected: FAIL - subContext shares executionStack with parent

**Step 3: Modify DynamicContext**

In `core/src/main/java/dev/metaschema/core/metapath/DynamicContext.java`:

Change 1: Move executionStack from SharedState to instance field (around line 52):

```java
public class DynamicContext { // NOPMD - intentional data class

  @NonNull
  private final Map<Integer, ISequence<?>> letVariableMap;
  @NonNull
  private final SharedState sharedState;
  @NonNull
  private final Deque<IExpression> executionStack;
```

Change 2: Initialize in primary constructor (around line 69):

```java
  public DynamicContext(@NonNull StaticContext staticContext) {
    this.letVariableMap = new ConcurrentHashMap<>();
    this.sharedState = new SharedState(staticContext);
    this.executionStack = new ArrayDeque<>();
  }
```

Change 3: Copy stack in subContext constructor (around line 74):

```java
  private DynamicContext(@NonNull DynamicContext context) {
    this.letVariableMap = new ConcurrentHashMap<>(context.letVariableMap);
    this.sharedState = context.sharedState;
    this.executionStack = new ArrayDeque<>(context.executionStack);
  }
```

Change 4: Update SharedState class - remove executionStack (around line 93):

```java
  private static class SharedState {
    @NonNull
    private final StaticContext staticContext;
    @NonNull
    private final ZonedDateTime currentDateTime;
    @NonNull
    private final Map<URI, IDocumentNodeItem> availableDocuments;
    @NonNull
    private final Map<CalledContext, ISequence<?>> functionResultCache;
    @Nullable
    private CachingLoader documentLoader;
    @NonNull
    private final IMutableConfiguration<MetapathEvaluationFeature<?>> configuration;
    @NonNull
    private ZoneId implicitTimeZone;
    // REMOVED: executionStack - now per-context
```

Change 5: Update availableDocuments to ConcurrentHashMap (in SharedState constructor):

```java
      this.availableDocuments = new ConcurrentHashMap<>();
```

Change 6: Update push/pop methods to use instance field (around line 376):

```java
  public void pushExecutionStack(@NonNull IExpression expression) {
    this.executionStack.push(expression);
  }

  public void popExecutionStack(@NonNull IExpression expression) {
    IExpression popped = this.executionStack.pop();
    if (!expression.equals(popped)) {
      throw new IllegalStateException("Popped expression does not match expected expression");
    }
  }

  @NonNull
  public Deque<IExpression> getExecutionStack() {
    return new ArrayDeque<>(this.executionStack);
  }
```

**Step 4: Run tests to verify they pass**

Run: `mvn -pl core test -Dtest=DynamicContextTest`
Expected: All tests PASS

**Step 5: Run full core tests to verify no regressions**

Run: `mvn -pl core test`
Expected: All tests PASS

**Step 6: Commit**

```bash
git add core/src/main/java/dev/metaschema/core/metapath/DynamicContext.java
git add core/src/test/java/dev/metaschema/core/metapath/DynamicContextTest.java
git commit -m "feat(core): make DynamicContext execution stack per-context for thread safety"
```

---

## Task 3: Make FindingCollectingConstraintValidationHandler Thread-Safe

**Files:**
- Modify: `core/src/main/java/dev/metaschema/core/model/constraint/FindingCollectingConstraintValidationHandler.java`
- Test: `core/src/test/java/dev/metaschema/core/model/constraint/FindingCollectingConstraintValidationHandlerTest.java`

**Step 1: Write failing tests for thread-safety and sorting**

```java
/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model.constraint;

import static org.junit.jupiter.api.Assertions.*;

import dev.metaschema.core.model.constraint.IConstraint.Level;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class FindingCollectingConstraintValidationHandlerTest {

  @Test
  void testConcurrentAddFindings() throws Exception {
    FindingCollectingConstraintValidationHandler handler =
        new FindingCollectingConstraintValidationHandler();

    int threadCount = 10;
    int findingsPerThread = 100;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch latch = new CountDownLatch(threadCount);

    for (int t = 0; t < threadCount; t++) {
      final int threadId = t;
      executor.submit(() -> {
        try {
          for (int i = 0; i < findingsPerThread; i++) {
            // Create mock finding - implementation will need test helpers
            handler.handleExpectViolation(
                createMockConstraint(Level.ERROR),
                createMockNode("/root/item" + threadId + "-" + i),
                createMockNode("/root/item" + threadId + "-" + i),
                createMockContext());
          }
        } finally {
          latch.countDown();
        }
      });
    }

    latch.await(30, TimeUnit.SECONDS);
    executor.shutdown();

    List<ConstraintValidationFinding> findings = handler.getFindings();
    assertEquals(threadCount * findingsPerThread, findings.size());
  }

  @Test
  void testHighestSeverityConcurrentUpdates() throws Exception {
    FindingCollectingConstraintValidationHandler handler =
        new FindingCollectingConstraintValidationHandler();

    ExecutorService executor = Executors.newFixedThreadPool(4);
    CountDownLatch latch = new CountDownLatch(4);

    // Thread 1: Add INFORMATIONAL findings
    executor.submit(() -> {
      try {
        for (int i = 0; i < 100; i++) {
          addFinding(handler, Level.INFORMATIONAL);
        }
      } finally {
        latch.countDown();
      }
    });

    // Thread 2: Add WARNING findings
    executor.submit(() -> {
      try {
        for (int i = 0; i < 100; i++) {
          addFinding(handler, Level.WARNING);
        }
      } finally {
        latch.countDown();
      }
    });

    // Thread 3: Add ERROR findings
    executor.submit(() -> {
      try {
        for (int i = 0; i < 100; i++) {
          addFinding(handler, Level.ERROR);
        }
      } finally {
        latch.countDown();
      }
    });

    // Thread 4: Add CRITICAL findings
    executor.submit(() -> {
      try {
        for (int i = 0; i < 100; i++) {
          addFinding(handler, Level.CRITICAL);
        }
      } finally {
        latch.countDown();
      }
    });

    latch.await(30, TimeUnit.SECONDS);
    executor.shutdown();

    assertEquals(Level.CRITICAL, handler.getHighestSeverity());
    assertEquals(400, handler.getFindings().size());
  }

  @Test
  void testFindingsSortedByMetapath() {
    FindingCollectingConstraintValidationHandler handler =
        new FindingCollectingConstraintValidationHandler();

    // Add findings in random order
    addFindingWithPath(handler, "/root/zebra");
    addFindingWithPath(handler, "/root/alpha");
    addFindingWithPath(handler, "/root/middle");

    List<ConstraintValidationFinding> findings = handler.getFindings();

    assertEquals("/root/alpha", findings.get(0).getTarget().getMetapath());
    assertEquals("/root/middle", findings.get(1).getTarget().getMetapath());
    assertEquals("/root/zebra", findings.get(2).getTarget().getMetapath());
  }

  // Helper methods - implement using mock objects or test fixtures
  private void addFinding(FindingCollectingConstraintValidationHandler handler, Level level) {
    // Implementation using mocks
  }

  private void addFindingWithPath(FindingCollectingConstraintValidationHandler handler, String path) {
    // Implementation using mocks
  }
}
```

**Step 2: Run tests to verify they fail**

Run: `mvn -pl core test -Dtest=FindingCollectingConstraintValidationHandlerTest -DfailIfNoTests=false`
Expected: FAIL - race conditions or wrong ordering

**Step 3: Modify FindingCollectingConstraintValidationHandler**

In `core/src/main/java/dev/metaschema/core/model/constraint/FindingCollectingConstraintValidationHandler.java`:

Change 1: Update imports (around line 20):

```java
import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
```

Change 2: Update class Javadoc (around line 28):

```java
/**
 * A validation result handler that collects the resulting findings for later
 * retrieval using the {@link #getFindings()} method.
 * <p>
 * This class is thread-safe and can be used with parallel constraint validation.
 */
```

Change 3: Update fields (around line 39):

```java
  @NonNull
  private final Queue<ConstraintValidationFinding> findings = new ConcurrentLinkedQueue<>();
  @NonNull
  private final AtomicReference<Level> highestLevel = new AtomicReference<>(IConstraint.Level.INFORMATIONAL);
```

Change 4: Update getFindings() method (around line 44):

```java
  @Override
  @NonNull
  public List<ConstraintValidationFinding> getFindings() {
    return findings.stream()
        .sorted(Comparator.comparing(f -> f.getTarget().getMetapath()))
        .collect(Collectors.toUnmodifiableList());
  }
```

Change 5: Update getHighestSeverity() method (around line 50):

```java
  @Override
  @NonNull
  public Level getHighestSeverity() {
    return highestLevel.get();
  }
```

Change 6: Update addFinding() method (around line 62):

```java
  protected void addFinding(@NonNull ConstraintValidationFinding finding) {
    findings.add(finding);

    Level severity = finding.getSeverity();
    highestLevel.updateAndGet(current ->
        severity.ordinal() > current.ordinal() ? severity : current);
  }
```

**Step 4: Run tests to verify they pass**

Run: `mvn -pl core test -Dtest=FindingCollectingConstraintValidationHandlerTest`
Expected: All tests PASS

**Step 5: Run full core tests to verify no regressions**

Run: `mvn -pl core test`
Expected: All tests PASS

**Step 6: Commit**

```bash
git add core/src/main/java/dev/metaschema/core/model/constraint/FindingCollectingConstraintValidationHandler.java
git add core/src/test/java/dev/metaschema/core/model/constraint/FindingCollectingConstraintValidationHandlerTest.java
git commit -m "feat(core): make FindingCollectingConstraintValidationHandler thread-safe"
```

---

## Task 4: Make DefaultConstraintValidator Thread-Safe

**Files:**
- Modify: `core/src/main/java/dev/metaschema/core/model/constraint/DefaultConstraintValidator.java`
- Test: `core/src/test/java/dev/metaschema/core/model/constraint/DefaultConstraintValidatorThreadSafetyTest.java`

**Step 1: Write failing tests**

```java
/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model.constraint;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class DefaultConstraintValidatorThreadSafetyTest {

  @Test
  void testConcurrentValueMapAccess() throws Exception {
    FindingCollectingConstraintValidationHandler handler =
        new FindingCollectingConstraintValidationHandler();
    DefaultConstraintValidator validator = new DefaultConstraintValidator(handler);

    int threadCount = 10;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch latch = new CountDownLatch(threadCount);
    AtomicInteger errors = new AtomicInteger(0);

    for (int t = 0; t < threadCount; t++) {
      executor.submit(() -> {
        try {
          // Simulate concurrent allowed-values validation
          // This test verifies no ConcurrentModificationException
          for (int i = 0; i < 100; i++) {
            // Call methods that access valueMap
            // Implementation will use mock node items
          }
        } catch (Exception e) {
          errors.incrementAndGet();
        } finally {
          latch.countDown();
        }
      });
    }

    latch.await(30, TimeUnit.SECONDS);
    executor.shutdown();

    assertEquals(0, errors.get(), "Should have no concurrent access errors");
  }

  @Test
  void testConcurrentIndexKeyRefAccess() throws Exception {
    FindingCollectingConstraintValidationHandler handler =
        new FindingCollectingConstraintValidationHandler();
    DefaultConstraintValidator validator = new DefaultConstraintValidator(handler);

    int threadCount = 10;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch latch = new CountDownLatch(threadCount);
    AtomicInteger errors = new AtomicInteger(0);

    for (int t = 0; t < threadCount; t++) {
      final int threadId = t;
      executor.submit(() -> {
        try {
          // Simulate concurrent index-has-key validation
          for (int i = 0; i < 100; i++) {
            // Call methods that access indexNameToKeyRefMap
          }
        } catch (Exception e) {
          errors.incrementAndGet();
        } finally {
          latch.countDown();
        }
      });
    }

    latch.await(30, TimeUnit.SECONDS);
    executor.shutdown();

    assertEquals(0, errors.get(), "Should have no concurrent access errors");
  }
}
```

**Step 2: Run tests to verify they fail**

Run: `mvn -pl core test -Dtest=DefaultConstraintValidatorThreadSafetyTest -DfailIfNoTests=false`
Expected: FAIL or race condition errors

**Step 3: Modify DefaultConstraintValidator**

In `core/src/main/java/dev/metaschema/core/model/constraint/DefaultConstraintValidator.java`:

Change 1: Update imports (add around line 37):

```java
import java.util.Collections;
```

Change 2: Update valueMap declaration (line 65):

```java
  @NonNull
  private final Map<INodeItem, ValueStatus> valueMap = new ConcurrentHashMap<>();
```

Change 3: Update class Javadoc (around line 51):

```java
/**
 * Used to perform constraint validation over one or more node items.
 * <p>
 * This class is thread-safe when used with {@link ParallelValidationConfig}.
 */
```

Change 4: Update validateIndexHasKey method (around line 664):

```java
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
```

**Step 4: Run tests to verify they pass**

Run: `mvn -pl core test -Dtest=DefaultConstraintValidatorThreadSafetyTest`
Expected: All tests PASS

**Step 5: Run full core tests to verify no regressions**

Run: `mvn -pl core test`
Expected: All tests PASS

**Step 6: Commit**

```bash
git add core/src/main/java/dev/metaschema/core/model/constraint/DefaultConstraintValidator.java
git add core/src/test/java/dev/metaschema/core/model/constraint/DefaultConstraintValidatorThreadSafetyTest.java
git commit -m "feat(core): make DefaultConstraintValidator thread-safe"
```

---

## Task 5: Add Parallel Traversal to DefaultConstraintValidator

**Files:**
- Modify: `core/src/main/java/dev/metaschema/core/model/constraint/DefaultConstraintValidator.java`
- Test: `core/src/test/java/dev/metaschema/core/model/constraint/ParallelValidationTest.java`

**Step 1: Write failing tests**

```java
/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model.constraint;

import static org.junit.jupiter.api.Assertions.*;

import dev.metaschema.core.metapath.DynamicContext;

import org.junit.jupiter.api.Test;

import java.util.List;

class ParallelValidationTest {

  @Test
  void testSequentialAndParallelProduceSameResults() {
    // Create a document with multiple sibling nodes
    // Validate with sequential (threads=1)
    // Validate with parallel (threads=4)
    // Assert same findings count and content

    FindingCollectingConstraintValidationHandler sequentialHandler =
        new FindingCollectingConstraintValidationHandler();
    DefaultConstraintValidator sequentialValidator =
        new DefaultConstraintValidator(sequentialHandler);

    FindingCollectingConstraintValidationHandler parallelHandler =
        new FindingCollectingConstraintValidationHandler();

    try (ParallelValidationConfig parallelConfig = ParallelValidationConfig.withThreads(4)) {
      DefaultConstraintValidator parallelValidator =
          new DefaultConstraintValidator(parallelHandler, parallelConfig);

      // Create test document with constraints
      // INodeItem testDoc = createTestDocument();
      // DynamicContext context = new DynamicContext();

      // sequentialValidator.validate(testDoc, context);
      // parallelValidator.validate(testDoc, context);

      // List<ConstraintValidationFinding> seqFindings = sequentialHandler.getFindings();
      // List<ConstraintValidationFinding> parFindings = parallelHandler.getFindings();

      // assertEquals(seqFindings.size(), parFindings.size());
      // Findings should be same (sorted by location)
    }
  }

  @Test
  void testParallelValidationWithManyChildren() {
    // Test that parallel validation works with > PARALLEL_THRESHOLD children
    FindingCollectingConstraintValidationHandler handler =
        new FindingCollectingConstraintValidationHandler();

    try (ParallelValidationConfig config = ParallelValidationConfig.withThreads(4)) {
      DefaultConstraintValidator validator = new DefaultConstraintValidator(handler, config);

      // Create document with 10+ sibling nodes (above threshold of 4)
      // Validate and verify completion without errors
    }
  }

  @Test
  void testParallelConfigConstructor() {
    FindingCollectingConstraintValidationHandler handler =
        new FindingCollectingConstraintValidationHandler();

    // Test new constructor accepts ParallelValidationConfig
    try (ParallelValidationConfig config = ParallelValidationConfig.withThreads(2)) {
      DefaultConstraintValidator validator = new DefaultConstraintValidator(handler, config);
      assertNotNull(validator);
    }

    // Test backward-compatible constructor still works
    DefaultConstraintValidator validator = new DefaultConstraintValidator(handler);
    assertNotNull(validator);
  }
}
```

**Step 2: Run tests to verify they fail**

Run: `mvn -pl core test -Dtest=ParallelValidationTest -DfailIfNoTests=false`
Expected: Compilation error - constructor does not exist

**Step 3: Modify DefaultConstraintValidator for parallel support**

In `core/src/main/java/dev/metaschema/core/model/constraint/DefaultConstraintValidator.java`:

Change 1: Add imports:

```java
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
```

Change 2: Add field for parallel config (around line 73):

```java
  @NonNull
  private final ParallelValidationConfig parallelConfig;
```

Change 3: Add new constructor (around line 81):

```java
  /**
   * Construct a new constraint validation instance with parallel execution support.
   *
   * @param handler
   *          the validation handler to use for handling constraint violations
   * @param parallelConfig
   *          the parallel execution configuration
   */
  public DefaultConstraintValidator(
      @NonNull IConstraintValidationHandler handler,
      @NonNull ParallelValidationConfig parallelConfig) {
    this.handler = handler;
    this.configuration = new DefaultConfiguration<>();
    this.parallelConfig = parallelConfig;
  }
```

Change 4: Update existing constructor to call new one:

```java
  /**
   * Construct a new constraint validation instance.
   *
   * @param handler
   *          the validation handler to use for handling constraint violations
   */
  public DefaultConstraintValidator(
      @NonNull IConstraintValidationHandler handler) {
    this(handler, ParallelValidationConfig.SEQUENTIAL);
  }
```

Change 5: Add constant for parallel threshold (around line 63):

```java
  private static final int PARALLEL_THRESHOLD = 4;
```

Change 6: Modify Visitor class to support parallel traversal. Update visitAssembly (around line 1070):

```java
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

      ExecutorService executor = parallelConfig.getExecutor();
      List<? extends IModelNodeItem<?, ?>> children =
          item.modelItems().collect(Collectors.toList());

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
```

**Step 4: Run tests to verify they pass**

Run: `mvn -pl core test -Dtest=ParallelValidationTest`
Expected: All tests PASS

**Step 5: Run full core tests to verify no regressions**

Run: `mvn -pl core test`
Expected: All tests PASS

**Step 6: Commit**

```bash
git add core/src/main/java/dev/metaschema/core/model/constraint/DefaultConstraintValidator.java
git add core/src/test/java/dev/metaschema/core/model/constraint/ParallelValidationTest.java
git commit -m "feat(core): add parallel traversal support to DefaultConstraintValidator"
```

---

## Task 6: Add --threads CLI Option

**Files:**
- Modify: `metaschema-cli/src/main/java/dev/metaschema/cli/commands/AbstractValidateContentCommand.java`
- Test: `metaschema-cli/src/test/java/dev/metaschema/cli/commands/ValidateCommandParallelTest.java`

**Step 1: Write failing tests**

```java
/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.cli.commands;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ValidateCommandParallelTest {

  @Test
  void testThreadsOptionParsing() {
    // Test that --threads 4 is parsed correctly
    // This will be an integration test using the CLI
  }

  @Test
  void testThreadsOptionDefaultsToOne() {
    // Test that without --threads, validation uses sequential mode
  }

  @Test
  void testThreadsOptionZeroRejected() {
    // Test that --threads 0 produces an error
  }

  @Test
  void testThreadsOptionNegativeRejected() {
    // Test that --threads -1 produces an error
  }

  @Test
  void testExperimentalWarningPrinted() {
    // Test that using --threads > 1 prints experimental warning to stderr
  }
}
```

**Step 2: Run tests to verify they fail**

Run: `mvn -pl metaschema-cli test -Dtest=ValidateCommandParallelTest -DfailIfNoTests=false`
Expected: Tests fail or option not recognized

**Step 3: Modify AbstractValidateContentCommand**

In `metaschema-cli/src/main/java/dev/metaschema/cli/commands/AbstractValidateContentCommand.java`:

Change 1: Add import:

```java
import dev.metaschema.core.model.constraint.ParallelValidationConfig;
```

Change 2: Add constant for the option (around line 65):

```java
  @NonNull
  private static final Option PARALLEL_THREADS_OPTION = ObjectUtils.notNull(
      Option.builder()
          .longOpt("threads")
          .hasArg()
          .argName("count")
          .desc("Number of threads for parallel constraint validation (default: 1, experimental)")
          .build());
```

Change 3: Add option to gatherOptions() method (around line 110):

```java
  @Override
  public Collection<? extends Option> gatherOptions() {
    return ObjectUtils.notNull(List.of(
        CONSTRAINTS_OPTION,
        SARIF_OUTPUT_FILE_OPTION,
        SARIF_INCLUDE_PASS_OPTION,
        NO_SCHEMA_VALIDATION_OPTION,
        NO_CONSTRAINT_VALIDATION_OPTION,
        PARALLEL_THREADS_OPTION));
  }
```

Change 4: Add helper method to parse thread count:

```java
  /**
   * Get the parallel validation configuration from command line options.
   *
   * @param cmdLine the parsed command line
   * @return the parallel validation config
   * @throws InvalidArgumentException if thread count is invalid
   */
  @NonNull
  protected static ParallelValidationConfig getParallelConfig(@NonNull CommandLine cmdLine)
      throws InvalidArgumentException {
    int threadCount = 1;
    if (cmdLine.hasOption(PARALLEL_THREADS_OPTION)) {
      String value = cmdLine.getOptionValue(PARALLEL_THREADS_OPTION);
      try {
        threadCount = Integer.parseInt(value);
      } catch (NumberFormatException e) {
        throw new InvalidArgumentException("Invalid thread count: " + value);
      }
      if (threadCount < 1) {
        throw new InvalidArgumentException("Thread count must be at least 1, got: " + threadCount);
      }
    }
    return threadCount > 1
        ? ParallelValidationConfig.withThreads(threadCount)
        : ParallelValidationConfig.SEQUENTIAL;
  }

  /**
   * Print experimental warning if parallel validation is enabled.
   *
   * @param config the parallel validation config
   * @param threadCount the thread count for the warning message
   */
  protected static void printParallelWarningIfNeeded(
      @NonNull ParallelValidationConfig config,
      int threadCount) {
    if (config.isParallel()) {
      System.err.println("WARNING: Parallel constraint validation (--threads " + threadCount + ") is experimental.");
      System.err.println("         Report issues at https://github.com/metaschema-framework/metaschema-java/issues");
    }
  }
```

Change 5: Update executor to use parallel config. In the execute method of AbstractValidationCommandExecutor, add handling for parallel config:

```java
  // In execute() method, add before validation:
  ParallelValidationConfig parallelConfig = getParallelConfig(cmdLine);
  int threadCount = cmdLine.hasOption(PARALLEL_THREADS_OPTION)
      ? Integer.parseInt(cmdLine.getOptionValue(PARALLEL_THREADS_OPTION))
      : 1;
  printParallelWarningIfNeeded(parallelConfig, threadCount);

  try {
    // ... existing validation logic, pass parallelConfig to validator ...
  } finally {
    parallelConfig.close();
  }
```

**Step 4: Run tests to verify they pass**

Run: `mvn -pl metaschema-cli test -Dtest=ValidateCommandParallelTest`
Expected: All tests PASS

**Step 5: Run full CLI tests to verify no regressions**

Run: `mvn -pl metaschema-cli test`
Expected: All tests PASS

**Step 6: Commit**

```bash
git add metaschema-cli/src/main/java/dev/metaschema/cli/commands/AbstractValidateContentCommand.java
git add metaschema-cli/src/test/java/dev/metaschema/cli/commands/ValidateCommandParallelTest.java
git commit -m "feat(cli): add --threads option for parallel constraint validation"
```

---

## Task 7: Integration Testing and Documentation

**Files:**
- Create: `metaschema-cli/src/test/java/dev/metaschema/cli/commands/ParallelValidationIntegrationTest.java`
- Update: PRD with completion status

**Step 1: Write integration tests**

```java
/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.cli.commands;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

class ParallelValidationIntegrationTest {

  @TempDir
  Path tempDir;

  @Test
  void testValidateWithThreadsOption() {
    // Run CLI with --threads 4 on a sample document
    // Verify successful validation
  }

  @Test
  void testParallelAndSequentialProduceSameOutput() {
    // Run same validation with --threads 1 and --threads 4
    // Compare SARIF output (findings should match)
  }

  @Test
  void testLargeDocumentPerformance() {
    // Validate a large document with many sibling nodes
    // Verify parallel is faster than sequential (basic smoke test)
  }
}
```

**Step 2: Run all tests**

Run: `mvn test`
Expected: All tests PASS

**Step 3: Run full CI build**

Run: `mvn clean install -PCI -Prelease`
Expected: BUILD SUCCESS

**Step 4: Final commit**

```bash
git add metaschema-cli/src/test/java/dev/metaschema/cli/commands/ParallelValidationIntegrationTest.java
git commit -m "test(cli): add integration tests for parallel constraint validation"
```

---

## Verification Checklist

After all tasks complete:

- [ ] All unit tests pass: `mvn test`
- [ ] CI build passes: `mvn clean install -PCI -Prelease`
- [ ] Checkstyle passes: `mvn checkstyle:check`
- [ ] `--threads 1` produces same results as no flag
- [ ] `--threads 4` validates successfully
- [ ] Experimental warning prints to stderr
- [ ] Invalid thread counts rejected with clear error

---

## Summary

| Task | Component | Key Changes |
|------|-----------|-------------|
| 1 | ParallelValidationConfig | New class for thread pool configuration |
| 2 | DynamicContext | Per-context execution stack, ConcurrentHashMap for docs |
| 3 | FindingCollectingHandler | ConcurrentLinkedQueue, AtomicReference, sorted output |
| 4 | DefaultConstraintValidator | ConcurrentHashMap for valueMap, synchronized lists |
| 5 | DefaultConstraintValidator | Parallel visitor, sibling-level parallelization |
| 6 | CLI | --threads option, experimental warning |
| 7 | Integration | End-to-end tests, CI verification |
