# PRD: Performance Instrumentation for Constraint Processing

## Document Information

| Field | Value |
|-------|-------|
| **PRD ID** | TIMING-001 |
| **Status** | Draft |
| **Created** | 2026-02-07 |
| **Last Updated** | 2026-02-10 |
| **Issue** | [#314](https://github.com/metaschema-framework/metaschema-java/issues/314) |

---

## 1. Overview

### 1.1 Problem Statement

Developers working with Metaschema-enabled software have no way to measure the performance of individual constraint evaluations or overall constraint processing. When validation is slow, there is no built-in mechanism to identify which constraints or let-statement evaluations are hotspots. This makes performance optimization guesswork rather than data-driven.

### 1.2 Goals

1. Add event-based instrumentation to the constraint validation pipeline
2. Collect per-constraint, per-let-statement, per-phase, and overall validation timing data
3. Expose timing data through SARIF 2.1.0 output using both native timestamp fields and custom properties
4. Provide a CLI flag (`--sarif-timing`) to opt into detailed timing collection
5. Always include overall run timing in SARIF output regardless of `--sarif-timing`
6. Attach per-evaluation timing (constraint duration and let-statement breakdown) to individual SARIF results
7. Ensure zero overhead when timing is disabled

### 1.3 Non-Goals

- OpenTelemetry or external APM integration (future work)
- Console-readable timing output (future work, can reuse `TimingCollector`)
- Profiling of Metapath expression evaluation outside of let-statements
- Historical performance tracking or benchmarking infrastructure

### 1.4 Success Metrics

| Metric | Current | Target |
|--------|---------|--------|
| Per-constraint timing available | No | Yes, via SARIF rule properties |
| Per-result evaluation timing | No | Yes, via SARIF result properties |
| Per-result let-statement timing | No | Yes, via SARIF result properties |
| Let-statement timing available | No | Yes, via SARIF notifications |
| Phase-level timing available | No | Yes, via SARIF invocation |
| Overall run timing in SARIF | No | Yes, always (no flag needed) |
| Overhead when disabled | N/A | Zero (no-op listener) |
| Overhead when enabled | N/A | < 5% wall-clock increase |

---

## 2. Background

### 2.1 Current State

The metaschema-java framework includes:

- **SARIF 2.1.0 output** via `SarifValidationHandler` in `databind-modules`, supporting constraint findings, schema validation findings, and rule descriptors
- **Constraint validation pipeline** with `DefaultConstraintValidator`, the `IConstraintValidationHandler` callback pattern, and parallel execution via `ParallelValidationConfig`
- **CLI integration** with `--threads` for parallel validation and `-o` for SARIF output
- **No timing or instrumentation** of any kind in the validation pipeline

### 2.2 Technical Context

**Key classes involved:**

| Class | Module | Role |
|-------|--------|------|
| `DefaultConstraintValidator` | core | Main constraint evaluation engine |
| `IConstraintValidator` | core | Validator interface |
| `IConstraintValidationHandler` | core | Callback interface for findings |
| `FindingCollectingConstraintValidationHandler` | core | Collects findings for result aggregation |
| `ParallelValidationConfig` | core | Thread pool configuration |
| `DynamicContext` | core | Metapath evaluation context (let bindings) |
| `SarifValidationHandler` | databind-modules | SARIF document generation |
| `AbstractValidateContentCommand` | metaschema-cli | CLI validation commands |

**SARIF 2.1.0 built-in timing fields:**

| Object | Property | Type |
|--------|----------|------|
| `invocation` | `startTimeUtc` / `endTimeUtc` | datetime |
| `notification` | `timeUtc` | datetime |

SARIF has no built-in duration fields. Duration metrics must use `properties` bags per the specification's extensibility model.

**Thread safety:** Constraint validation supports parallel execution. All timing collection must be thread-safe.

---

## 3. Requirements

### 3.1 Functional Requirements

#### FR-1: Event-Based Instrumentation

A `ValidationEventListener` interface defines callbacks at four granularity levels:

- **Overall:** `beforeValidation(URI)` / `afterValidation(URI)`
- **Phase:** `beforePhase(ValidationPhase)` / `afterPhase(ValidationPhase)` where `ValidationPhase` is an enum: `SCHEMA_VALIDATION`, `CONSTRAINT_VALIDATION`, `FINALIZATION`
- **Constraint:** `beforeConstraintEvaluation(IConstraint, INodeItem)` / `afterConstraintEvaluation(IConstraint, INodeItem)`
- **Let-statement:** `beforeLetEvaluation(ILet)` / `afterLetEvaluation(ILet)`

A `NoOpValidationEventListener` provides empty implementations for all methods.

#### FR-2: Validation Configuration

Rename `ParallelValidationConfig` to `ValidationConfig`. Retain existing `withThreads()` and `withExecutor()` builder methods. Add `addListener(ValidationEventListener)` method (supports multiple listeners via composite pattern). Default listener is the no-op implementation. When multiple listeners are registered, a `CompositeValidationEventListener` delegates to all of them in registration order.

#### FR-3: Timing Collection

A `TimingCollector` class implements `ValidationEventListener` and records:

- **`TimingRecord`** for each entry: `totalTimeNs`, `count`, `minTimeNs`, `maxTimeNs`, `startTimestampUtc`, `endTimestampUtc`
- **Hierarchical storage:** separate maps for phase timings (`Map<ValidationPhase, TimingRecord>`), constraint timings (`Map<String, TimingRecord>`), and let timings (`Map<String, TimingRecord>`), plus an overall `validationTiming` record
- **Thread safety:** Thread-local deques for start times (handles nested events); concurrent maps for accumulation
- **Nanosecond precision** via `System.nanoTime()` for durations; `Instant.now()` for UTC timestamps

#### FR-4: Identifier Strategy

Both `IConstraint` and `ILet` expose a `getInternalIdentifier()` method using lazy compute + cache:

- **Constraints:** Author-defined `id` if present; deterministic fallback `{module-short-name}-{definition-name}-{constraint-type}-{index}`
- **Let statements:** Deterministic `{module-short-name}-{definition-name}-let-{name}`

#### FR-5: SARIF Aggregate Timing Enrichment

When a `TimingCollector` is available, `SarifValidationHandler` produces enriched SARIF:

- **Invocation level:** Populate phase timing as `toolExecutionNotifications[]` with per-phase `properties.timing`
- **Rule level:** Add `properties.timing` to each rule in `tool.driver.rules[]` with `totalMs`, `count`, `minMs`, `maxMs`
- **Notifications level:** Add `toolExecutionNotifications[]` entries for each let evaluation with `timeUtc`, `descriptor.id`, `message.text`, and `properties.timing` containing `totalMs`, `count`, `minMs`, `maxMs`

When no `TimingCollector` is provided, aggregate timing enrichment is omitted.

#### FR-6: CLI Integration

New CLI option `--sarif-timing` on validation commands:

- Requires `-o` (SARIF output file); error if used without it
- When present, creates a `TimingCollector` and wires it into `ValidationConfig`
- Also registers `SarifValidationHandler` as a `ValidationEventListener` for per-result timing
- Timing data appears in the SARIF output file

#### FR-7: Always-On Run Timing

The SARIF output must always include an `invocation` object with overall run timing, regardless of whether `--sarif-timing` is used:

- `invocation.startTimeUtc`: recorded when `SarifValidationHandler` is constructed
- `invocation.endTimeUtc`: recorded when `generateSarif()` is called
- `invocation.executionSuccessful`: always set to `true`

This provides basic performance visibility without requiring any CLI flag.

#### FR-8: Per-Result Evaluation Timing

When `--sarif-timing` is enabled, each SARIF `result` object includes the duration of the specific constraint evaluation that produced it:

- Timing is attached via `result.properties` (propertyBag), per SARIF 2.1.0 section 3.8
- Uses the existing `TimingData` structure within the property bag

```json
{
  "ruleId": "require-title",
  "message": { "text": "..." },
  "properties": {
    "timing": {
      "totalMs": 0.307,
      "count": 1,
      "minMs": 0.307,
      "maxMs": 0.307
    }
  }
}
```

Implementation approach: `SarifValidationHandler` implements `ValidationEventListener` and uses thread-local state to track the current constraint evaluation start time. When a finding callback is received during evaluation, the handler associates the finding with the current evaluation. In `afterConstraintEvaluation()`, the handler computes the evaluation duration and records it on all findings from that evaluation.

#### FR-9: Per-Result Let-Statement Timing

When `--sarif-timing` is enabled, each SARIF `result` also includes timing data for let-statement evaluations that occurred during the constraint evaluation that produced it:

- Let timings are attached in `result.properties.letTimings` as an array of entries
- Each entry includes the let-statement name and its `TimingData`

```json
{
  "properties": {
    "timing": { "totalMs": 0.307, "count": 1, "minMs": 0.307, "maxMs": 0.307 },
    "letTimings": [
      {
        "name": "target-items",
        "timing": { "totalMs": 0.25, "count": 1, "minMs": 0.25, "maxMs": 0.25 }
      }
    ]
  }
}
```

The SARIF 2.1.0 propertyBag specification (`additionalProperties: true`) permits arbitrary key-value pairs, making this standards-compliant without schema modification.

#### FR-10: Multi-Listener Event Delivery

The validation event infrastructure must support delivering events to multiple listeners simultaneously. When `--sarif-timing` is used, both the `TimingCollector` (for aggregate timing on rules/phases/notifications) and the `SarifValidationHandler` (for per-result timing) receive events:

- `CompositeValidationEventListener` delegates to a list of listeners
- `ValidationConfig.addListener()` registers additional listeners
- Events are delivered in registration order to all listeners

### 3.2 Non-Functional Requirements

#### NFR-1: Zero Overhead When Disabled

The no-op listener must introduce no measurable overhead. No object allocation, no map lookups, no `System.nanoTime()` calls when timing is disabled.

#### NFR-2: Thread Safety

All timing collection must be safe under parallel constraint validation. Use `ConcurrentHashMap`, thread-local deques, and atomic operations as needed.

#### NFR-3: Backward Compatibility

The rename of `ParallelValidationConfig` to `ValidationConfig` is a breaking change. All existing callers must be updated. The existing builder API (`withThreads()`, `withExecutor()`, `SEQUENTIAL`) must continue to function identically.

---

## 4. Implementation Phases

### Phase 1: Core Infrastructure

Rename `ParallelValidationConfig` to `ValidationConfig`. Define `ValidationEventListener` interface, no-op implementation, and `ValidationPhase` enum. Add `withListener()` to `ValidationConfig`. Add `getInternalIdentifier()` to `IConstraint` and `ILet` with lazy caching.

### Phase 2: Timing Collection

Implement `TimingCollector` with hierarchical storage, thread-safe accumulation, and `TimingRecord`. Wire event firing into `DefaultConstraintValidator` and let-evaluation code paths.

### Phase 3: SARIF Integration & CLI

Extend `SarifValidationHandler` to consume `TimingCollector` data. Add `--sarif-timing` CLI option. End-to-end integration.

### Phase 4: Per-Result Timing & Always-On Run Timing

Add always-on invocation timing (FR-7). Implement `SarifValidationHandler` as a `ValidationEventListener` for per-result evaluation timing (FR-8) and per-result let-statement timing (FR-9). Add `CompositeValidationEventListener` for multi-listener support (FR-10). Extend the SARIF module to support `propertyBag` on result objects. Update CLI wiring to register both `TimingCollector` and `SarifValidationHandler` as event listeners.

---

## 5. Testing Strategy

### 5.1 Test Approach

All code changes follow TDD. Tests are written before implementation at each phase.

### 5.2 Unit Tests

| Component | Test Focus |
|-----------|-----------|
| `TimingCollector` | Accumulates timing correctly; thread-safety under concurrent events; min/max/count tracking; nested deque handling |
| `NoOpValidationEventListener` | All methods callable without error |
| `ValidationConfig` | Builder produces correct config; default listener is no-op; existing `withThreads()` API unchanged |
| Identifier generation | Explicit IDs used when present; deterministic fallback computed correctly; lazy caching returns same instance |
| `SarifValidationHandler` | With collector: timing properties at all three levels; without collector: output unchanged; millisecond conversion correct; UTC timestamps populated |
| `SarifValidationHandler` (per-result) | Per-result evaluation timing attached to `result.properties.timing`; per-result let timings in `result.properties.letTimings`; SARIF schema compliance |
| `SarifValidationHandler` (always-on) | Invocation with `startTimeUtc`/`endTimeUtc` always present in SARIF output even without `--sarif-timing` |
| `CompositeValidationEventListener` | Events delivered to all registered listeners in order |
| CLI `--sarif-timing` | Accepted with `-o`; error without `-o`; timing data in output file |

### 5.3 Integration Test

End-to-end test that validates a document with known constraints and let statements, captures SARIF output with `--sarif-timing`, and verifies:

- Timing values are positive and plausible
- All constraint identifiers appear in rule timing
- Let evaluation notifications present
- Phase timing covers all phases
- `startTimeUtc` < `endTimeUtc`
- Each result has `properties.timing` with per-evaluation duration
- Results from constraints with let statements include `properties.letTimings`
- SARIF output without `--sarif-timing` still includes invocation with timestamps

### 5.4 Verification Checklist

- [ ] All existing tests pass (no regressions from rename)
- [ ] New unit tests for all components
- [ ] Integration test passes
- [ ] `mvn clean install -PCI -Prelease` succeeds
- [ ] SARIF output validates against SARIF 2.1.0 schema

---

## 6. Risks and Mitigations

| Risk | Impact | Likelihood | Mitigation |
|------|--------|------------|------------|
| Rename breaks downstream consumers | Medium | Medium | Update all references in metaschema-java; document in release notes |
| Thread-local deque leaks on exception | Low | Low | Use try-finally in event firing; clear thread-locals in `afterValidation` |
| `System.nanoTime()` overhead in hot path | Low | Low | Only called when timing enabled; no-op listener is default |
| Let-evaluation instrumentation requires changes to Metapath context | Medium | Medium | Careful scoping of changes to let-binding evaluation only |

---

## 7. Open Questions

None — all design decisions resolved during brainstorming.

---

## 8. Related Documents

- [Implementation Plan](./implementation-plan.md)
- [Issue #314](https://github.com/metaschema-framework/metaschema-java/issues/314)
- [SARIF 2.1.0 Specification](https://docs.oasis-open.org/sarif/sarif/v2.1.0/sarif-v2.1.0.html)
