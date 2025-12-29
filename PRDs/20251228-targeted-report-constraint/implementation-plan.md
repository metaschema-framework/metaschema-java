# Implementation Plan: TargetedReportConstraint Support

This document details the implementation for adding constraint processing support for `TargetedReportConstraint`.

---

## Prerequisites

- Build the project to ensure all generated sources exist: `mvn install -DskipTests`
- Understand the difference between report and expect semantics (see PRD)

---

## Test-Driven Development Requirement

**All functional code changes must follow TDD:**

1. Write or update tests first to capture expected behavior
2. Verify tests fail with existing implementation
3. Make the code changes
4. Verify tests pass after changes

---

## Phase 1: Complete TargetedReportConstraint Support

### PR 1: Add IReportConstraint Interface and Implementation

| Attribute | Value |
|-----------|-------|
| **Files Changed** | 25 |
| **Risk Level** | Medium |
| **Dependencies** | None |
| **Target Branch** | develop |
| **Status** | Complete |
| **Pull Request** | [#598](https://github.com/metaschema-framework/metaschema-java/pull/598) |

#### Files to Create

| File | Purpose |
|------|---------|
| `core/src/main/java/gov/nist/secauto/metaschema/core/model/constraint/IReportConstraint.java` | Report constraint interface with `getTest()` method |
| `core/src/main/java/gov/nist/secauto/metaschema/core/model/constraint/impl/DefaultReportConstraint.java` | Default implementation with builder |
| `core/src/test/java/gov/nist/secauto/metaschema/core/model/constraint/ReportConstraintTest.java` | Unit tests for report constraint |

#### Files to Modify

| File | Changes |
|------|---------|
| `core/src/main/java/gov/nist/secauto/metaschema/core/model/constraint/IConstraintVisitor.java` | Add `visitReportConstraint()` method |
| `core/src/main/java/gov/nist/secauto/metaschema/core/model/constraint/IValueConstrained.java` | Add `getReportConstraints()` and `addConstraint(IReportConstraint)` methods |
| `core/src/main/java/gov/nist/secauto/metaschema/core/model/constraint/IFeatureValueConstrained.java` | Add delegation for report constraints |
| `core/src/main/java/gov/nist/secauto/metaschema/core/model/constraint/ValueConstraintSet.java` | Add storage and retrieval for report constraints |
| `core/src/main/java/gov/nist/secauto/metaschema/core/model/constraint/AbstractTargetedConstraints.java` | Add `getReportConstraints()` forwarding in `applyTo()` |
| `core/src/main/java/gov/nist/secauto/metaschema/core/model/constraint/DefaultConstraintValidator.java` | Add `validateReport()` method and integrate into validation flow |
| `databind/src/main/java/gov/nist/secauto/metaschema/databind/model/metaschema/impl/ConstraintBindingSupport.java` | Add `TargetedReportConstraint` handling to `parse()` methods and `newReport()` factory |
| `databind/src/test/java/gov/nist/secauto/metaschema/databind/model/metaschema/BindingConstraintLoaderTest.java` | Add test for loading TargetedReportConstraint |
| `.claude/skills/metaschema-constraints-authoring.md` | Add `report` constraint type documentation |
| `.claude/skills/metaschema-java-library.md` | Add `IReportConstraint` interface (if applicable) |

#### Implementation Approach

##### Step 1: Create Core Interface (TDD)

1. **Write test first** - Create `ReportConstraintTest.java` with tests for:
   - Builder creates valid constraint
   - Test expression is retrievable
   - Constraint properties (id, level, message) are accessible
   - Visitor pattern works correctly

2. **Watch tests fail** - Verify compilation errors due to missing classes

3. **Create `IReportConstraint.java`** modeled after `IExpectConstraint`:
   ```java
   public interface IReportConstraint extends IConfigurableMessageConstraint {
     @NonNull
     IMetapathExpression getTest();

     @NonNull
     static Builder builder() {
       return new Builder();
     }

     final class Builder extends AbstractConfigurableMessageConstraintBuilder<Builder, IReportConstraint> {
       // Builder implementation
     }
   }
   ```

4. **Create `DefaultReportConstraint.java`** in `impl/` package

5. **Run tests** - Verify they pass

##### Step 2: Update Constraint Interfaces (TDD)

1. **Write tests** for constraint storage/retrieval in `ValueConstraintSet`

2. **Update `IValueConstrained.java`**:
   - Add `List<? extends IReportConstraint> getReportConstraints()`
   - Add `void addConstraint(@NonNull IReportConstraint constraint)`

3. **Update `IFeatureValueConstrained.java`**:
   - Add delegation methods for report constraints

4. **Update `ValueConstraintSet.java`**:
   - Add `List<IReportConstraint> reportConstraints` field
   - Implement getter and add methods

5. **Update `AbstractTargetedConstraints.java`**:
   - Add `getReportConstraints().forEach(definition::addConstraint)` in `applyTo()`

##### Step 3: Update Visitor Pattern

1. **Update `IConstraintVisitor.java`**:
   ```java
   R visitReportConstraint(@NonNull IReportConstraint constraint, T state);
   ```

2. **Update `IReportConstraint.java`** to implement `accept()`:
   ```java
   @Override
   default <T, R> R accept(IConstraintVisitor<T, R> visitor, T state) {
     return visitor.visitReportConstraint(this, state);
   }
   ```

   **Note:** There are currently no implementations of `IConstraintVisitor` in the codebase. The interface is prepared for future visitor pattern usage. Only the interface itself needs the new method.

##### Step 4: Update Constraint Loader (TDD)

1. **Write test** in `BindingConstraintLoaderTest.java` for loading `TargetedReportConstraint`

2. **Update `ConstraintBindingSupport.java`**:
   - Add import for `TargetedReportConstraint`
   - Add case for `TargetedReportConstraint` in `parse(IValueConstrained, ...)` method
   - Add case for `TargetedReportConstraint` in `parse(IValueTargetedConstraintsBase, ...)` method
   - Add case for `TargetedReportConstraint` in `parse(IModelConstrained, ...)` method
   - Add factory method:
     ```java
     @NonNull
     private static IReportConstraint newReport(
         @NonNull TargetedReportConstraint obj,
         @NonNull ISource source) {
       // Build constraint from binding object
     }
     ```

##### Step 5: Update Validation Pipeline (TDD)

1. **Write test** for report constraint validation in `DefaultConstraintValidatorTest.java`

2. **Update `DefaultConstraintValidator.java`**:
   - Add `validateReport()` method similar to `validateExpect()` but:
     - Generate finding when test is TRUE (opposite of expect)
     - Use constraint's configured level (default: INFORMATIONAL)
     - Kind mapping: ERROR/CRITICAL → FAIL, all others → INFORMATIONAL
   - Add `validateReport()` call to all three validation entry points:
     - `validateFlag()` (line ~190) - add after other constraint validations
     - `validateField()` (line ~217) - add after other constraint validations
     - `validateAssembly()` (line ~243) - add after other constraint validations

##### Step 6: Update Skill Documentation

Update Claude Code skills to document the new constraint type:

1. **Update `.claude/skills/metaschema-constraints-authoring.md`**:
   - Add `report` to Constraint Types Overview table
   - Add new section for `report` constraint with:
     - Purpose: Report/fail when Metapath condition is TRUE (opposite of expect)
     - Attributes: `test`, `target`, `message`, `level` (default: INFORMATIONAL)
     - YAML and XML syntax examples
     - Semantic distinction from `expect`

2. **Update `.claude/skills/metaschema-java-library.md`** (if constraint interfaces are documented):
   - Add `IReportConstraint` interface
   - Document relationship to `IExpectConstraint`

##### Step 7: Final Verification

1. Run full test suite: `mvn test`
2. Run full build with checks: `mvn clean install -PCI -Prelease`
3. Verify all tests pass and no new warnings

#### Acceptance Criteria

- [x] `IReportConstraint` interface created extending `IConfigurableMessageConstraint`
- [x] `IReportConstraint.getTest()` returns the test Metapath expression
- [x] `DefaultReportConstraint` implementation with working builder
- [x] `IConstraintVisitor.visitReportConstraint()` method added
- [x] `IValueConstrained` has `getReportConstraints()` and `addConstraint(IReportConstraint)`
- [x] `ValueConstraintSet` stores and retrieves report constraints
- [x] `AbstractTargetedConstraints.applyTo()` forwards report constraints
- [x] `ConstraintBindingSupport.parse()` handles `TargetedReportConstraint` in all overloads
- [x] `ConstraintBindingSupport.newReport()` factory method creates valid constraints
- [x] `DefaultConstraintValidator.validateReport()` generates findings when test is TRUE
- [x] Report constraints default to INFORMATIONAL level
- [x] Report constraints at ERROR/CRITICAL cause validation failures (Kind.FAIL)
- [x] `metaschema-constraints-authoring.md` skill updated with `report` constraint
- [x] `metaschema-java-library.md` skill updated if applicable
- [x] Unit tests for `IReportConstraint` builder and behavior
- [x] Unit tests for constraint loading
- [x] Unit tests for constraint validation
- [x] All new code has complete Javadoc
- [x] All tests pass: `mvn test`
- [x] Build succeeds: `mvn clean install -PCI -Prelease`

---

## Key Design Decisions

### Report vs Expect Semantics

| Aspect | Expect Constraint | Report Constraint |
|--------|-------------------|-------------------|
| Meaning | "This MUST be true" | "This MUST NOT be true" |
| Generates finding when | Test = FALSE | Test = TRUE |
| Default level | ERROR | INFORMATIONAL |
| Kind at ERROR/CRITICAL | FAIL | FAIL |
| Kind at WARNING and below | Based on level | INFORMATIONAL |

Both expect and report can cause validation failures when configured at ERROR or CRITICAL level.

### Kind Mapping for Report Constraints

| Configured Level | Kind |
|------------------|------|
| INFORMATIONAL (default) | INFORMATIONAL |
| DEBUG | INFORMATIONAL |
| WARNING | INFORMATIONAL |
| ERROR | FAIL |
| CRITICAL | FAIL |

### Interface Hierarchy

```text
IConstraint
└── IConfigurableMessageConstraint
    ├── IExpectConstraint  (existing)
    └── IReportConstraint  (new - follows same pattern)
```

---

## PR Summary Table

| PR | Description | Files | Risk | Dependencies | Status |
|----|-------------|-------|------|--------------|--------|
| [#598](https://github.com/metaschema-framework/metaschema-java/pull/598) | Add IReportConstraint interface and full implementation | 25 | Medium | None | Complete |

**Total PRs**: 1
**Total Files Changed**: 25

---

## Verification Commands

```bash
# Run all tests
mvn test

# Run specific test classes
mvn -pl core test -Dtest=ReportConstraintTest
mvn -pl databind test -Dtest=BindingConstraintLoaderTest

# Full CI build
mvn clean install -PCI -Prelease
```
