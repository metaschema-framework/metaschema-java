# Implementation Plan: Performance Instrumentation for Constraint Processing

This document details each PR for the constraint timing instrumentation initiative.

---

## Prerequisites

- Working build from `develop` branch
- Git worktree created for this feature

---

## Test-Driven Development Requirement

**All functional code changes must follow TDD:**

1. Write or update tests first to capture expected behavior
2. Verify tests fail for the expected reason
3. Make the code changes
4. Verify tests pass after changes

---

## Phase 1: Core Infrastructure

### PR 1: Rename `ParallelValidationConfig` to `ValidationConfig` and Add Event Listener Support

| Attribute | Value |
|-----------|-------|
| **Files Changed** | ~15 |
| **Risk Level** | Medium |
| **Dependencies** | None |
| **Target Branch** | develop |
| **Status** | Complete |
| **Commit** | `45ee1c63d` |

#### Files to Modify

| File | Changes |
|------|---------|
| `core/src/main/java/dev/metaschema/core/model/constraint/ParallelValidationConfig.java` | Rename to `ValidationConfig.java`; add `withListener(ValidationEventListener)` builder method; default to `NoOpValidationEventListener` |
| `core/src/main/java/dev/metaschema/core/model/constraint/ValidationEventListener.java` | **New file.** Interface with `beforeValidation`, `afterValidation`, `beforePhase`, `afterPhase`, `beforeConstraintEvaluation`, `afterConstraintEvaluation`, `beforeLetEvaluation`, `afterLetEvaluation` |
| `core/src/main/java/dev/metaschema/core/model/constraint/NoOpValidationEventListener.java` | **New file.** Empty implementation of all `ValidationEventListener` methods |
| `core/src/main/java/dev/metaschema/core/model/constraint/ValidationPhase.java` | **New file.** Enum: `SCHEMA_VALIDATION`, `CONSTRAINT_VALIDATION`, `FINALIZATION` |
| `core/src/main/java/dev/metaschema/core/model/constraint/DefaultConstraintValidator.java` | Update imports from `ParallelValidationConfig` to `ValidationConfig`; update constructor and field references |
| `core/src/test/java/dev/metaschema/core/model/constraint/ParallelValidationConfigTest.java` | Rename to `ValidationConfigTest.java`; add tests for `withListener()` and default no-op listener |
| `core/src/test/java/dev/metaschema/core/model/constraint/DefaultConstraintValidatorThreadSafetyTest.java` | Update references from `ParallelValidationConfig` to `ValidationConfig` |
| `databind/src/main/java/dev/metaschema/databind/IBindingContext.java` | Update `ParallelValidationConfig` references to `ValidationConfig` |
| Any other files referencing `ParallelValidationConfig` | Update imports and references |

#### Acceptance Criteria

- [x] `ParallelValidationConfig` renamed to `ValidationConfig` across all files
- [x] `ValidationEventListener` interface defined with all 8 event methods
- [x] `NoOpValidationEventListener` provides empty implementations
- [x] `ValidationPhase` enum created with `SCHEMA_VALIDATION`, `CONSTRAINT_VALIDATION`, `FINALIZATION`
- [x] `ValidationConfig.withListener()` builder method works correctly
- [x] Default listener is `NoOpValidationEventListener`
- [x] Existing `withThreads()` and `withExecutor()` API unchanged
- [x] `SEQUENTIAL` static instance continues to work
- [x] All existing tests pass (no regressions)
- [x] New tests for `ValidationConfig.withListener()` and no-op default
- [x] Build succeeds: `mvn clean install -PCI -Prelease`

---

### PR 2: Add Identifiers to Constraints and Let Statements

| Attribute | Value |
|-----------|-------|
| **Files Changed** | ~8 |
| **Risk Level** | Low |
| **Dependencies** | PR 1 |
| **Target Branch** | develop |
| **Status** | Complete |
| **Commit** | `e2c827b52` |

#### Files to Modify

| File | Changes |
|------|---------|
| `core/src/main/java/dev/metaschema/core/model/constraint/IConstraint.java` | Add `getInternalIdentifier()` default method |
| `core/src/main/java/dev/metaschema/core/model/constraint/ILet.java` | Add `getInternalIdentifier()` default method |
| `core/src/main/java/dev/metaschema/core/model/constraint/AbstractConstraint.java` | Implement `getInternalIdentifier()` with lazy compute + cache |
| `core/src/main/java/dev/metaschema/core/model/constraint/DefaultLet.java` | Implement `getInternalIdentifier()` with lazy compute + cache |
| `core/src/test/java/dev/metaschema/core/model/constraint/IConstraintIdentifierTest.java` | **New file.** Tests for identifier generation |
| `core/src/test/java/dev/metaschema/core/model/constraint/ILetIdentifierTest.java` | **New file.** Tests for let identifier generation |

#### Acceptance Criteria

- [x] `IConstraint.getInternalIdentifier()` returns author-defined `id` when present
- [x] `IConstraint.getInternalIdentifier()` returns deterministic fallback when `id` is null
- [x] `ILet.getInternalIdentifier()` returns deterministic scoped identifier
- [x] Identifiers are lazily computed and cached (same instance on repeated calls)
- [x] Unit tests cover explicit ID, fallback, and caching scenarios
- [x] All existing tests pass
- [x] Build succeeds: `mvn clean install -PCI -Prelease`

---

## Phase 2: Timing Collection

### PR 3: Implement `TimingCollector` and Wire Event Firing

| Attribute | Value |
|-----------|-------|
| **Files Changed** | ~10 |
| **Risk Level** | Medium |
| **Dependencies** | PR 1, PR 2 |
| **Target Branch** | develop |
| **Status** | Complete |
| **Commit** | `f82f14a94` |

#### Files to Modify

| File | Changes |
|------|---------|
| `core/src/main/java/dev/metaschema/core/model/constraint/TimingCollector.java` | **New file.** Implements `ValidationEventListener`; hierarchical storage with ConcurrentHashMaps and ThreadLocal deques |
| `core/src/main/java/dev/metaschema/core/model/constraint/TimingRecord.java` | **New file.** Record class with `totalTimeNs`, `count`, `minTimeNs`, `maxTimeNs`, `startTimestampUtc`, `endTimestampUtc` |
| `core/src/main/java/dev/metaschema/core/model/constraint/DefaultConstraintValidator.java` | Fire `beforeConstraintEvaluation`/`afterConstraintEvaluation` wrapping each constraint evaluation; fire `beforeLetEvaluation`/`afterLetEvaluation` in `handleLetStatements()` |
| `core/src/main/java/dev/metaschema/core/model/constraint/ValidationFeature.java` | **New file.** Add `EVENT_LISTENER` feature for passing listener through `IConfiguration` |
| `databind/src/main/java/dev/metaschema/databind/IBindingContext.java` | Read `ValidationFeature.EVENT_LISTENER` and apply to `ValidationConfig` |
| `core/src/test/java/dev/metaschema/core/model/constraint/TimingCollectorTest.java` | **New file.** Tests: timing accumulation, min/max/count tracking, UTC timestamp recording |
| `core/src/test/java/dev/metaschema/core/model/constraint/TimingCollectorThreadSafetyTest.java` | **New file.** Concurrent access tests |

#### Acceptance Criteria

- [x] `TimingCollector` accumulates timing data correctly for all event types
- [x] `TimingRecord` tracks totalTimeNs, count, minTimeNs, maxTimeNs, startTimestampUtc, endTimestampUtc
- [x] Thread-safety verified under concurrent constraint evaluation
- [x] Nested events (constraint containing let evaluations) handled correctly via deque
- [x] Events fired from `DefaultConstraintValidator` for all constraint types
- [x] Events fired for let-statement evaluations in `handleLetStatements()`
- [x] No-op listener introduces no measurable overhead
- [x] All existing tests pass
- [x] Build succeeds: `mvn clean install -PCI -Prelease`

---

## Phase 3: SARIF Integration & CLI

### PR 4: SARIF Timing Output and `--sarif-timing` CLI Option

| Attribute | Value |
|-----------|-------|
| **Files Changed** | ~15 |
| **Risk Level** | Medium |
| **Dependencies** | PR 3 |
| **Target Branch** | develop |
| **Status** | Complete |
| **Commits** | `4562769de`, `bc1b206bc` |

#### Files to Modify

| File | Changes |
|------|---------|
| `databind-modules/modules/sarif/sarif-module.xml` | **Submodule change.** Add `invocation`, `notification`, `reportingDescriptor.properties`, `run.invocations` SARIF types; fix `notification.message` `min-occurs="1"` per SARIF spec |
| `databind-modules/src/main/java/dev/metaschema/modules/sarif/SarifValidationHandler.java` | Accept optional `TimingCollector`; implement `enrichWithTiming()` for invocation, rule, and notification timing |
| `metaschema-cli/src/main/java/dev/metaschema/cli/commands/AbstractValidateContentCommand.java` | Add `SARIF_TIMING_OPTION` (`--sarif-timing`); create `TimingCollector` and wire events |
| `databind-modules/src/test/java/dev/metaschema/modules/sarif/SarifValidationHandlerTimingTest.java` | **New file.** Tests for timing enrichment and SARIF schema compliance |
| `metaschema-cli/src/test/java/dev/metaschema/cli/CLITest.java` | Add `testSarifTimingOutput()` end-to-end integration test with SARIF schema validation |
| `metaschema-cli/src/test/resources/content/timing-test-module.xml` | **New file.** Test metaschema module with inline constraints and let statements |
| `metaschema-cli/src/test/resources/content/timing-test-constraints.xml` | **New file.** External constraints with let statements |
| `metaschema-cli/src/test/resources/content/timing-test-content.json` | **New file.** Test content matching the timing-test-module schema |
| `.claude/rules/sarif-compliance.md` | **New file.** Claude rule requiring SARIF 2.1.0 compliance |
| `pom.xml` | Add `dev.harrel:json-schema` to parent POM dependencyManagement |
| `databind-modules/pom.xml` | Remove hardcoded `json-schema` version (inherits from parent) |
| `metaschema-cli/pom.xml` | Add `json-schema` test dependency (inherits version from parent) |

#### Deviations from Plan

- PR 5 (end-to-end integration test) was merged into PR 4 as `testSarifTimingOutput()` in `CLITest.java` rather than a separate test class, since the test naturally fits with the CLI module tests
- SARIF module changes required a separate submodule PR (metaschema-modules PR #10)
- Added SARIF compliance Claude rule (`.claude/rules/sarif-compliance.md`) not originally planned
- Centralized `dev.harrel:json-schema` dependency version in parent POM

#### Acceptance Criteria

- [x] `SarifValidationHandler` accepts optional `TimingCollector`
- [x] Invocation includes `startTimeUtc`/`endTimeUtc` and phase timing notifications
- [x] Each constraint rule includes `properties.timing` with `totalMs`, `count`, `minMs`, `maxMs`
- [x] Let evaluations appear as `toolExecutionNotifications[]` with timing data
- [x] Without `TimingCollector`, SARIF output is identical to current behavior
- [x] `--sarif-timing` flag accepted when `-o` is present
- [x] `--sarif-timing` without `-o` produces a clear error message
- [x] Nanoseconds correctly converted to milliseconds in SARIF output
- [x] Integration test validates timing data at all SARIF levels
- [x] Integration test exercises inline and external let statements
- [x] SARIF output validates against official SARIF 2.1.0 JSON schema
- [x] All existing tests pass
- [x] Build succeeds: `mvn clean install -PCI -Prelease`

---

## Phase 4: Per-Result Timing & Always-On Run Timing

### PR 5: Always-On Run Timing, Per-Result Evaluation and Let-Statement Timing

| Attribute | Value |
|-----------|-------|
| **Files Changed** | ~15 |
| **Risk Level** | Medium |
| **Dependencies** | PR 4 |
| **Target Branch** | develop |
| **Status** | Complete |

#### Overview

This PR addresses three gaps:

1. **Always-on run timing**: SARIF output always includes `invocation.startTimeUtc`/`endTimeUtc` even without `--sarif-timing`
2. **Per-result evaluation timing**: Each SARIF `result` includes the duration of the specific constraint evaluation that produced it
3. **Per-result let-statement timing**: Each SARIF `result` includes timing data for let-statement evaluations that occurred during its constraint evaluation

#### Architecture

**Multi-listener support**: `SarifValidationHandler` implements `ValidationEventListener` to receive before/after events directly. A `CompositeValidationEventListener` delivers events to both `TimingCollector` (aggregate timing) and `SarifValidationHandler` (per-result timing).

**Per-result timing flow**:

```text
beforeConstraintEvaluation()
  → SarifValidationHandler records thread-local start nanotime
  → clears thread-local let timing map
  → clears thread-local "current evaluation findings" list
    ↓
  [beforeLetEvaluation() / afterLetEvaluation()]
    → SarifValidationHandler accumulates per-let durations in thread-local map
    ↓
  handler.handleXxxViolation()
    → SarifValidationHandler.addConstraintValidationFinding()
    → adds new ConstraintResult to thread-local "current evaluation findings"
    ↓
afterConstraintEvaluation()
  → computes evaluation duration = nanoTime() - start
  → snapshots thread-local let timing map
  → sets evaluation duration + let timings on all ConstraintResults in current list
```

**SARIF compliance**: Per SARIF 2.1.0 section 3.8, `propertyBag` is the standard extensibility mechanism. The `result.properties` uses the same `TimingData` structure already used on rules and notifications. Let-statement timing is added as `properties.letTimings` — valid because `propertyBag` permits `additionalProperties: true`.

#### Files to Modify

| File | Changes |
|------|---------|
| `core/src/main/java/dev/metaschema/core/model/constraint/CompositeValidationEventListener.java` | **New file.** Delegates to multiple `ValidationEventListener` instances |
| `core/src/main/java/dev/metaschema/core/model/constraint/ValidationConfig.java` | Add `addListener()` method; build `CompositeValidationEventListener` when multiple listeners registered |
| `core/src/test/java/dev/metaschema/core/model/constraint/CompositeValidationEventListenerTest.java` | **New file.** Tests multi-listener delivery |
| `databind-modules/modules/sarif/sarif-module.xml` | **Submodule change.** Add `propertyBag` to `result` definition; add `letTimingEntry` assembly; add `letTimings` list to `PropertyBag` |
| `databind-modules/src/main/java/dev/metaschema/modules/sarif/SarifValidationHandler.java` | Implement `ValidationEventListener`; add thread-local state for per-evaluation timing; always create `Invocation` with timestamps; attach per-result timing and let timings during `generateResults()` |
| `databind-modules/src/test/java/dev/metaschema/modules/sarif/SarifValidationHandlerTimingTest.java` | Add tests for per-result timing, per-result let timing, and always-on invocation timing |
| `metaschema-cli/src/main/java/dev/metaschema/cli/commands/AbstractValidateContentCommand.java` | Register `SarifValidationHandler` as second event listener alongside `TimingCollector`; always record run timestamps |
| `metaschema-cli/src/test/java/dev/metaschema/cli/CLITest.java` | Add tests verifying invocation always present in SARIF output; per-result timing in `--sarif-timing` output |

#### SARIF Module Changes (Submodule)

Changes to `sarif-module.xml`:

1. Add `<assembly ref="propertyBag" min-occurs="0">` to the `result` definition (line ~252, inside `<model>`)
2. Add `letTimingEntry` assembly definition:

```xml
<define-assembly name="letTimingEntry">
    <formal-name>Let Statement Timing Entry</formal-name>
    <description>Timing data for a single let-statement evaluation.</description>
    <define-flag name="name" required="yes">
        <formal-name>Let Statement Name</formal-name>
        <description>The name of the let-statement variable.</description>
    </define-flag>
    <model>
        <assembly ref="timingData">
            <formal-name>Timing Data</formal-name>
            <description>Performance timing measurements for this let-statement.</description>
            <use-name>timing</use-name>
        </assembly>
    </model>
</define-assembly>
```

3. Add `letTimings` to `PropertyBag` model:

```xml
<assembly ref="letTimingEntry" min-occurs="0" max-occurs="unbounded">
    <formal-name>Let Statement Timings</formal-name>
    <description>Per-let-statement timing data for the associated constraint evaluation.</description>
    <group-as name="letTimings" in-json="ARRAY"/>
</assembly>
```

After modifying the submodule, regenerate binding classes:

```bash
mvn install -DskipTests
mvn -f databind-modules/pom.xml generate-sources
```

#### Thread-Local State in SarifValidationHandler

```java
// Per-evaluation timing state (thread-local for parallel validation)
private final ThreadLocal<Long> currentEvaluationStartNanos = new ThreadLocal<>();
private final ThreadLocal<Map<String, Long>> currentLetStartNanos = new ThreadLocal<>();
private final ThreadLocal<Map<String, Long>> currentLetDurations
    = ThreadLocal.withInitial(LinkedHashMap::new);
private final ThreadLocal<List<ConstraintResult>> currentEvaluationResults
    = ThreadLocal.withInitial(ArrayList::new);

// Always-on run timing
private final Instant constructionTimestamp = Instant.now();
```

#### Deviations from Plan

- Thread-local let duration tracking uses `Map<ILet, Long>` instead of `Map<String, Long>` for type safety
- Added `ConcurrentHashMap<IConstraint, EvaluationTimingSnapshot>` for deferred timing lookup when findings are added after validation completes (CLI pattern)
- `--sarif-timing` option marked as experimental per user request
- CLI wires `SarifValidationHandler` as second event listener via `CompositeValidationEventListener` for per-result timing delivery

#### Acceptance Criteria

- [x] `CompositeValidationEventListener` delivers events to all registered listeners
- [x] `ValidationConfig.addListener()` supports registering multiple listeners
- [x] SARIF output always includes `invocation` with `startTimeUtc`/`endTimeUtc` even without `--sarif-timing`
- [x] Each SARIF `result` includes `properties.timing` with per-evaluation duration when `--sarif-timing` is used
- [x] Each SARIF `result` includes `properties.letTimings` for let-statements evaluated during its constraint evaluation
- [x] `result.properties` uses the standard SARIF `propertyBag` extensibility mechanism
- [x] Per-result timing is thread-safe under parallel validation
- [x] SARIF output validates against official SARIF 2.1.0 JSON schema
- [x] All existing tests pass
- [x] Build succeeds: `mvn clean install -PCI -Prelease`

---

## PR Summary Table

| PR | Description | Commit | Status |
|----|-------------|--------|--------|
| 1 | Rename `ParallelValidationConfig` to `ValidationConfig`; add event listener support | `45ee1c63d` | Complete |
| 2 | Add identifiers to constraints and let statements | `e2c827b52` | Complete |
| 3 | Implement `TimingCollector` and wire event firing | `f82f14a94` | Complete |
| 4 | SARIF timing output, `--sarif-timing` CLI option, and integration tests | `4562769de`, `bc1b206bc` | Complete |
| 5 | Always-on run timing, per-result evaluation and let-statement timing | — | Complete |

**Pull Request**: https://github.com/metaschema-framework/metaschema-java/pull/658
**Submodule PR**: https://github.com/metaschema-framework/metaschema-modules/pull/10
