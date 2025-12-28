# PRD: Add Constraint Processing Support for TargetedReportConstraint

## Document Information

| Field | Value |
|-------|-------|
| **PRD ID** | REPORT-592 |
| **Status** | In Review |
| **Author** | David Waltermire |
| **Created** | 2025-12-28 |
| **Last Updated** | 2025-12-28 |
| **GitHub Issue** | [#592](https://github.com/metaschema-framework/metaschema-java/issues/592) |
| **Milestone** | v3.0.0 Milestone 2 |

---

## 1. Overview

### 1.1 Problem Statement

The `TargetedReportConstraint` binding class was added in PR #589 as part of the Metapath-based targeting for binding configurations. However, the constraint processing system has not been updated to handle this new constraint type.

Currently, the constraint loader (`ConstraintBindingSupport`) and validation infrastructure do not:
- Recognize and parse `TargetedReportConstraint` instances
- Evaluate the target Metapath expression to identify target nodes
- Apply the report constraint logic to matched targets

This leaves the `TargetedReportConstraint` binding class orphaned - it exists but cannot be used in constraint validation.

### 1.2 Goals

1. Create a core `IReportConstraint` interface following the existing constraint interface pattern
2. Implement `DefaultReportConstraint` with builder support
3. Update `ConstraintBindingSupport` to parse `TargetedReportConstraint` from binding configuration
4. Integrate report constraints into the validation pipeline
5. Add comprehensive unit tests for the new constraint type

### 1.3 Non-Goals

- Modifying the Metaschema module definition (already complete)
- Changing the generated binding class `TargetedReportConstraint.java` (already generated correctly)
- Adding CLI commands for report constraints
- Adding schema generation support for report constraints

### 1.4 Success Metrics

| Metric | Current | Target |
|--------|---------|--------|
| TargetedReportConstraint recognized by loader | No | Yes |
| Report constraints validated at runtime | No | Yes |
| Unit test coverage for new code | 0% | 80%+ |
| All existing tests pass | Yes | Yes |

---

## 2. Background

### 2.1 Current State

The constraint processing system supports these targeted constraint types:
- `TargetedAllowedValuesConstraint`
- `TargetedExpectConstraint`
- `TargetedMatchesConstraint`
- `TargetedIndexConstraint`
- `TargetedIndexHasKeyConstraint`
- `TargetedIsUniqueConstraint`
- `TargetedHasCardinalityConstraint`

Each targeted constraint type has:
1. A binding class (generated from Metaschema module)
2. A core interface (e.g., `IExpectConstraint`)
3. A default implementation (e.g., `DefaultExpectConstraint`)
4. Loader support in `ConstraintBindingSupport`
5. Validation logic in `DefaultConstraintValidator`
6. Visitor pattern method in `IConstraintVisitor`

The `TargetedReportConstraint` binding class exists but lacks items 2-6.

### 2.2 Technical Context

**Report vs Expect Semantics:**

The report constraint is semantically different from expect:
- **Expect constraint:** Fails validation if the test expression evaluates to FALSE
- **Report constraint:** Generates an informational finding when the test expression evaluates to TRUE

This means report constraints produce findings/reports but do not cause validation failures. They are used for informational purposes, warnings, or advisory messages.

**Key Files:**

| Component | Location |
|-----------|----------|
| Binding class | `databind/.../binding/TargetedReportConstraint.java` |
| Constraint interfaces | `core/.../model/constraint/` |
| Constraint implementations | `core/.../model/constraint/impl/` |
| Constraint loader | `databind/.../impl/ConstraintBindingSupport.java` |
| Constraint validator | `core/.../constraint/DefaultConstraintValidator.java` |
| Visitor interface | `core/.../constraint/IConstraintVisitor.java` |

---

## 3. Requirements

### 3.1 Functional Requirements

#### FR-1: IReportConstraint Interface
Create an `IReportConstraint` interface that:
- Extends `IConfigurableMessageConstraint`
- Provides access to the test expression via `getTest(): IMetapathExpression`
- Follows the same pattern as `IExpectConstraint`

#### FR-2: DefaultReportConstraint Implementation
Create a `DefaultReportConstraint` class that:
- Implements `IReportConstraint`
- Provides a builder via `IReportConstraint.builder()`
- Supports all standard constraint properties (id, level, target, message, remarks)

#### FR-3: Constraint Loading Support
Update `ConstraintBindingSupport` to:
- Handle `TargetedReportConstraint` in all `parse()` method overloads
- Create `IReportConstraint` instances via a new `newReport()` factory method

#### FR-4: Validation Pipeline Integration
Update the validation system to:
- Call report constraint validation at appropriate points
- Generate findings when report test expressions evaluate to TRUE
- Default level is INFORMATIONAL (unlike expect's default of ERROR)
- Map Kind based on severity: ERROR/CRITICAL → FAIL, WARNING → PASS, others → INFORMATIONAL

#### FR-5: Visitor Pattern Support
Update `IConstraintVisitor` to:
- Include `visitReportConstraint()` method
- Update all visitor implementations

#### FR-6: Skill Documentation Updates
Update Claude Code skills to document the new constraint type:
- `.claude/skills/metaschema-constraints-authoring.md` - Add `report` constraint type with syntax and examples
- `.claude/skills/metaschema-java-library.md` - Add `IReportConstraint` interface if constraint interfaces are documented

### 3.2 Non-Functional Requirements

#### NFR-1: API Consistency
The new constraint types must follow established patterns for naming, structure, and builder usage consistent with existing constraints like `IExpectConstraint`.

#### NFR-2: Test Coverage
All new code must have unit tests following TDD practices with minimum 80% coverage.

#### NFR-3: Documentation
All public interfaces and methods must have complete Javadoc per project standards.

---

## 4. Implementation Phases

This is a single-phase implementation as all components are interdependent.

### Phase 1: Complete TargetedReportConstraint Support

Implement all components in a single cohesive PR:
1. Core interface and implementation
2. Constraint loading support
3. Validation integration
4. Comprehensive unit tests

See [Implementation Plan](./implementation-plan.md) for detailed breakdown.

---

## 5. Testing Strategy

### 5.1 Test Approach

All development follows TDD:
1. Write failing tests for each new component
2. Implement the component to pass tests
3. Refactor while maintaining green tests

### 5.2 Verification Checklist

- [ ] `IReportConstraint` interface created with appropriate methods
- [ ] `DefaultReportConstraint` implementation with builder
- [ ] `ConstraintBindingSupport.parse()` handles `TargetedReportConstraint`
- [ ] `newReport()` factory method creates valid constraints
- [ ] Report constraints generate findings when test is TRUE
- [ ] Report constraints default to INFORMATIONAL level
- [ ] Report constraints at ERROR/CRITICAL level cause validation failures (Kind.FAIL)
- [ ] Visitor pattern updated with `visitReportConstraint()`
- [ ] `metaschema-constraints-authoring.md` skill updated with `report` constraint
- [ ] `metaschema-java-library.md` skill updated if applicable
- [ ] All new code has Javadoc
- [ ] All unit tests pass
- [ ] Build succeeds with `mvn clean install -PCI -Prelease`

---

## 6. Risks and Mitigations

| Risk | Impact | Likelihood | Mitigation |
|------|--------|------------|------------|
| Report semantics differ from expect | Medium | Low | Clear documentation, separate validation method |
| Breaking existing constraint handling | High | Low | Comprehensive test coverage, incremental changes |
| Missing visitor implementations | Medium | Medium | Search for all IConstraintVisitor implementations |

---

## 7. Design Decisions

### DD-1: Report Constraint Severity and Kind Mapping

**Decision:** Report constraints support all severity levels with INFORMATIONAL as the default.

| Configured Level | Kind when test=TRUE |
|------------------|---------------------|
| INFORMATIONAL (default) | INFORMATIONAL |
| DEBUG | INFORMATIONAL |
| WARNING | INFORMATIONAL |
| ERROR | FAIL |
| CRITICAL | FAIL |

**Rationale:** This allows authors to use report constraints for both informational messages and hard errors when detecting problematic patterns.

### DD-2: Semantic Distinction from Expect

**Decision:** Report and expect are opposite assertions:

| Constraint | Meaning | Generates finding when... |
|------------|---------|---------------------------|
| Expect | "This MUST be true" | Test = FALSE |
| Report | "This MUST NOT be true" | Test = TRUE |

Both can cause validation failures at ERROR/CRITICAL level.

### DD-3: Finding Differentiation

**Decision:** No special field needed to distinguish report from expect findings.

Consumers can use `instanceof IReportConstraint` vs `instanceof IExpectConstraint` on the finding's constraint object if differentiation is needed. This follows the existing pattern for other constraint types.

---

## 8. Related Documents

- [Implementation Plan](./implementation-plan.md)
- [PR #589 - Metapath-based targeting for binding configurations](https://github.com/metaschema-framework/metaschema-java/pull/589)
- [Issue #592 - Add constraint processing support for TargetedReportConstraint](https://github.com/metaschema-framework/metaschema-java/issues/592)
