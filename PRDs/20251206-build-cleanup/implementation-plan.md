# Implementation Plan: Build Cleanup PRs

This document details each PR in the build cleanup initiative.

---

## Test-Driven Development Requirement

**All functional code changes (PRs 1-5) must follow TDD:**

1. Write or update tests first to capture expected behavior
2. Verify tests pass with existing implementation
3. Make the code changes
4. Verify tests still pass after changes
5. Add additional tests if edge cases are discovered

This ensures behavioral equivalence is verified, not assumed.

---

## Phase 1: Deprecation Fixes

### PR 1: Fix finalize() Deprecation Warnings ✅ COMPLETED

| Attribute | Value |
|-----------|-------|
| **Files Changed** | 2 |
| **Risk Level** | Medium |
| **Dependencies** | None |
| **Target Branch** | develop |
| **Status** | ✅ Completed 2025-12-06 |
| **Pull Request** | [#512](https://github.com/metaschema-framework/metaschema-java/pull/512) |

#### Files Modified

1. `core/src/main/java/gov/nist/secauto/metaschema/core/model/AbstractContainerModelSupport.java`
   - **Removed** empty `finalize()` method - abstract class with non-throwing constructor doesn't need finalizer attack protection
2. `databind/src/main/java/gov/nist/secauto/metaschema/databind/model/metaschema/impl/AbstractAbsoluteModelGenerator.java`
   - **Removed** empty `finalize()` method and unused `@SuppressFBWarnings` import - abstract class with non-throwing constructor doesn't need finalizer attack protection

#### Files NOT Modified (intentionally kept)

1. `databind/src/main/java/gov/nist/secauto/metaschema/databind/DefaultBindingContext.java`
   - **Kept** empty `finalize()` method - this is a non-final concrete class with a constructor that can throw, so the finalize() is required to prevent SEI CERT OBJ-11 finalizer attacks. Updated Javadoc to clarify this.

#### Implementation Findings

The original code used empty `finalize()` methods to prevent finalizer attacks (SEI CERT Rule OBJ-11). Analysis revealed:
- **Abstract classes** don't need this protection because they can't be directly instantiated
- **Concrete non-final classes with throwing constructors** still need this protection
- Java 6+ only provides automatic protection if the exception is thrown *before* the Object constructor finishes

#### Acceptance Criteria

- [x] Tests verified before code changes (TDD) - existing tests cover functionality
- [x] Empty `finalize()` removed from abstract classes where unnecessary (2 files)
- [x] `finalize()` kept in `DefaultBindingContext` for SEI CERT OBJ-11 compliance
- [x] Resource cleanup behavior preserved
- [x] All tests pass
- [x] No memory leaks introduced
- [x] Build passes with no SpotBugs CT_CONSTRUCTOR_THROW errors

---

### PR 2: Fix Maven Plugin @Component Deprecation ✅ COMPLETED

| Attribute | Value |
|-----------|-------|
| **Files Changed** | 1 |
| **Risk Level** | Low |
| **Dependencies** | None |
| **Target Branch** | develop |
| **Status** | ✅ Completed 2025-12-06 |
| **Pull Request** | [#513](https://github.com/metaschema-framework/metaschema-java/pull/513) |

#### Files Modified

1. `metaschema-maven-plugin/src/main/java/gov/nist/secauto/metaschema/maven/plugin/AbstractMetaschemaMojo.java`
   - Replaced deprecated `@Component` annotation with JSR-330 `@Inject` annotation for `BuildContext` injection
   - Updated import from `org.apache.maven.plugins.annotations.Component` to `javax.inject.Inject`

#### Implementation Findings

- The `javax.inject` dependency is already available transitively through `maven-core`, so no new dependencies were required
- Maven 3.x provides native JSR-330 injection support via Sisu/Guice

#### Acceptance Criteria

- [x] Tests verified before code changes (TDD) - integration tests pass
- [x] No `@Component` annotations in maven-plugin module
- [x] Plugin integration tests pass (Passed: 1, Failed: 0)
- [x] Maven goals execute correctly
- [x] Full CI build passes

---

### PR 3: Fix FunctionUtils Deprecation (Math Operations) ✅ COMPLETED

| Attribute | Value |
|-----------|-------|
| **Files Changed** | 11 (7 source + 4 test) |
| **Risk Level** | Medium |
| **Dependencies** | None |
| **Target Branch** | develop |
| **Status** | ✅ Completed 2025-12-06 |
| **Pull Request** | [#514](https://github.com/metaschema-framework/metaschema-java/pull/514) |

#### Focus
Math operations in CST package.

#### Files Modified

| File | Changes |
|------|---------|
| `core/.../function/FunctionUtils.java` | Added non-deprecated `castToNumeric()` wrapper method; deprecated `toNumericOrNull()` |
| `core/.../item/atomic/INumericItem.java` | Fixed `cast()` to handle boolean inputs per XPath 3.1 (true→1, false→0) |
| `core/.../cst/math/AbstractBasicArithmeticExpression.java` | Replaced `toNumeric()` with `castToNumeric()` |
| `core/.../cst/math/Modulo.java` | Replaced sequence `toNumeric()` with explicit atomize/getFirstItem/castToNumeric pattern |
| `core/.../cst/math/IntegerDivision.java` | Replaced sequence `toNumeric()` with explicit atomize/getFirstItem/castToNumeric pattern |
| `core/.../cst/math/Negate.java` | Replaced sequence `toNumeric()` with explicit atomize/getFirstItem/castToNumeric pattern |
| `core/.../cst/math/Multiplication.java` | Replaced `toNumeric()` with `castToNumeric()` |

#### Test Files Added

| File | Tests | Description |
|------|-------|-------------|
| `core/.../cst/math/ModuloTest.java` | 12 | Tests for modulo operation with various numeric types |
| `core/.../cst/math/IntegerDivisionTest.java` | 14 | Tests for integer division with various numeric types |
| `core/.../cst/math/NegateTest.java` | 10 | Tests for unary negation with various numeric types |
| `core/.../item/atomic/NumericCastSymmetryTest.java` | 22 | Tests verifying IDecimalItem.cast() and INumericItem.cast() produce equivalent results |

#### Implementation Findings

1. **Exception preservation**: Created `castToNumeric()` wrapper that catches `InvalidValueForCastFunctionException` and wraps it in `InvalidTypeMetapathException` to maintain behavioral compatibility
2. **XPath 3.1 compliance**: Fixed `INumericItem.cast()` to handle boolean inputs (true→1, false→0) per XPath 3.1 specification
3. **Type preservation**: `INumericItem.cast()` preserves original type (integers stay integers), while `IDecimalItem.cast()` always converts to decimal

#### Acceptance Criteria

- [x] Tests written/updated before code changes (TDD) - 58 new tests added
- [x] No deprecated `FunctionUtils.toNumeric()` calls in cst/math operations
- [x] `castToNumeric()` wrapper preserves exception behavior
- [x] `INumericItem.cast()` handles boolean per XPath 3.1
- [x] All math operation tests pass (159 numeric-related tests)
- [x] Cast symmetry verified between IDecimalItem and INumericItem

#### Known Issues

- Environmental test timeouts on Windows affect unrelated tests (module loading, XML parsing) - tracked in [#515](https://github.com/metaschema-framework/metaschema-java/issues/515)

---

### PR 4: Fix FunctionUtils Deprecation (Function Library) ✅ COMPLETED

| Attribute | Value |
|-----------|-------|
| **Files Changed** | 5 (3 source + 1 test + 1 interface) |
| **Risk Level** | Medium |
| **Dependencies** | PR 3 |
| **Target Branch** | develop |
| **Status** | ✅ Completed 2025-12-07 |
| **Pull Request** | [#520](https://github.com/metaschema-framework/metaschema-java/pull/520) |

#### Focus
Function library classes that use deprecated FunctionUtils methods.

#### Files Modified

| File | Changes |
|------|---------|
| `core/.../item/ISequence.java` | Added `countTypes()` and `getItemTypes()` methods; optimized `ofCollection()` to return ISequence unchanged |
| `core/.../function/library/FnAvg.java` | Added private `countTypes()` helper using ISequence methods |
| `core/.../function/library/FnMinMax.java` | Replaced `FunctionUtils.countTypes()` and `getTypes()` with ISequence methods |
| `core/.../function/library/FnSum.java` | Replaced `FunctionUtils.countTypes()` with ISequence method |

#### Test Files Added/Modified

| File | Tests | Description |
|------|-------|-------------|
| `core/.../metapath/ISequenceTest.java` | 9 | Tests for `countTypes()`, `getItemTypes()`, and `ofCollection()` passthrough behavior |

#### Implementation Findings

1. **ISequence enhancements**: Added type utility methods directly to ISequence interface for reuse across function library
2. **Performance optimization**: `ofCollection()` now returns ISequence unchanged when input is already a sequence, avoiding unnecessary wrapping
3. **Documentation**: Added comprehensive Javadoc documenting defensive copy behavior (or lack thereof)

#### Acceptance Criteria

- [x] Tests written/updated before code changes (TDD) - 9 new tests for ISequence methods
- [x] No deprecated `FunctionUtils.countTypes()` calls
- [x] No deprecated `FunctionUtils.getTypes()` calls
- [x] All Metapath function tests pass
- [x] New ISequence methods documented with Javadoc

---

### PR 5: Fix FunctionUtils Deprecation (Type Requirements)

| Attribute | Value |
|-----------|-------|
| **Files Changed** | ~6 |
| **Risk Level** | Medium |
| **Dependencies** | PR 4 (recommended, not required) |
| **Target Branch** | develop |

#### Focus
Type requirement methods and remaining usages.

#### Files to Modify

| File | Line(s) | Method |
|------|---------|--------|
| `core/.../IMetapathExpression.java` | 38 | `toNumeric()` |
| `core/.../function/library/FnBaseUri.java` | 76 | `requireTypeOrNull()` |
| `core/.../function/library/FnData.java` | 70 | `requireTypeOrNull()` |
| `core/.../function/library/FnDocumentUri.java` | 72 | `requireTypeOrNull()` |
| `core/.../function/library/FnPath.java` | 76 | `requireTypeOrNull()` |
| `core/src/test/.../FunctionTestBase.java` | 73-74 | `getTypes()` |
| `core/.../function/library/MpRecurseDepth.java` | 83 | `requireType()` |

#### Implementation Approach

1. Replace `requireTypeOrNull()` with recommended alternative
2. Replace `requireType()` with recommended alternative
3. Replace `toNumeric()` in IMetapathExpression with castToNumeric()

#### Acceptance Criteria

- [ ] Tests written/updated before code changes (TDD)
- [ ] No deprecated `FunctionUtils.requireType*()` calls
- [ ] No deprecated `FunctionUtils.toNumeric()` calls in expression evaluation
- [ ] All Metapath function tests pass

---

### PR 6: Fix INcNameItem and Interface Deprecation

| Attribute | Value |
|-----------|-------|
| **Files Changed** | ~5 |
| **Risk Level** | Medium |
| **Dependencies** | None |
| **Target Branch** | develop |

#### Files to Modify

| File | Line(s) | Issue |
|------|---------|-------|
| `core/.../datatype/adapter/NcNameAdapter.java` | 26, 47, 65, 67 | `INcNameItem` usage |
| `core/.../item/atomic/INcNameItem.java` | 47 | Deprecated interface |
| `core/.../item/atomic/impl/NcNameItemImpl.java` | 21 | `INcNameItem` implementation |
| `core/.../metapath/StaticContext.java` | 73, 90 | `WellKnown` methods |
| `core/.../metapath/impl/AbstractSequence.java` | 50 | `getValue()` |

#### Implementation Approach

1. Identify replacement types for `INcNameItem`
2. Update `NcNameAdapter` to use new types
3. Replace `WellKnown` method calls with alternatives
4. Replace `ISequence.getValue()` with alternative

#### Acceptance Criteria

- [ ] Tests written/updated before code changes (TDD)
- [ ] No deprecated interface usages
- [ ] All adapter tests pass
- [ ] All sequence tests pass

---

## Phase 2: Javadoc Additions

### PR 7: Javadoc - Core Module (Model Interfaces)

| Attribute | Value |
|-----------|-------|
| **Files Changed** | ~40-50 |
| **Risk Level** | Low |
| **Dependencies** | None |
| **Target Branch** | develop |

#### Scope
`core/src/main/java/gov/nist/secauto/metaschema/core/model/`

#### Key Files
- Interface definitions for metaschema model elements
- Assembly, field, flag interfaces
- Constraint interfaces

#### Acceptance Criteria

- [ ] All public interfaces have class-level Javadoc
- [ ] All public methods have `@param`, `@return`, `@throws` as appropriate
- [ ] No new Javadoc warnings from modified files

---

### PR 8: Javadoc - Core Module (Metapath)

| Attribute | Value |
|-----------|-------|
| **Files Changed** | ~30-40 |
| **Risk Level** | Low |
| **Dependencies** | None |
| **Target Branch** | develop |

#### Scope
`core/src/main/java/gov/nist/secauto/metaschema/core/metapath/`

#### Key Files
- Expression interfaces
- Function library classes
- Item type interfaces

#### Acceptance Criteria

- [ ] All Metapath expression interfaces documented
- [ ] All function library methods documented
- [ ] No new Javadoc warnings from modified files

---

### PR 9: Javadoc - Core Module (Remaining)

| Attribute | Value |
|-----------|-------|
| **Files Changed** | ~20-30 |
| **Risk Level** | Low |
| **Dependencies** | None |
| **Target Branch** | develop |

#### Scope
- `core/.../datatype/` - Data type adapters
- `core/.../util/` - Utility classes
- `core/.../configuration/` - Configuration interfaces

#### Key Files
- `ExceptionUtils.java` (10 warnings)
- Data type adapter interfaces

#### Acceptance Criteria

- [ ] All utility classes documented
- [ ] All configuration interfaces documented
- [ ] No new Javadoc warnings from modified files

---

### PR 10: Javadoc - Databind Module

| Attribute | Value |
|-----------|-------|
| **Files Changed** | ~50 (split if exceeds limit) |
| **Risk Level** | Low |
| **Dependencies** | None |
| **Target Branch** | develop |

#### Scope
`databind/src/main/java/gov/nist/secauto/metaschema/databind/`

#### Key Files
- `binding/AssemblyModel.java` (72 warnings - highest priority)
- Model and annotation classes
- Binding context classes

#### Acceptance Criteria

- [ ] AssemblyModel fully documented
- [ ] All binding interfaces documented
- [ ] No new Javadoc warnings from modified files

---

### PR 11: Javadoc - Schemagen Module

| Attribute | Value |
|-----------|-------|
| **Files Changed** | ~40-50 |
| **Risk Level** | Low |
| **Dependencies** | None |
| **Target Branch** | develop |

#### Scope
`schemagen/src/main/java/gov/nist/secauto/metaschema/schemagen/`

#### Key Files
- `ModuleIndex.java` (23 warnings - highest priority)
- Schema generation classes
- XML/JSON schema writer classes

#### Acceptance Criteria

- [ ] ModuleIndex fully documented
- [ ] All schema generator interfaces documented
- [ ] No new Javadoc warnings from modified files

---

### PR 12: Javadoc - Remaining Modules

| Attribute | Value |
|-----------|-------|
| **Files Changed** | ~20-30 |
| **Risk Level** | Low |
| **Dependencies** | None |
| **Target Branch** | develop |

#### Scope
- `metaschema-maven-plugin/` (~13 warnings)
- `metaschema-cli/`
- Any remaining modules

#### Acceptance Criteria

- [ ] All Maven plugin goals documented
- [ ] All CLI commands documented
- [ ] No new Javadoc warnings from modified files

---

## PR Summary Table

| PR | Description | Files | Risk | Dependencies | Status |
|----|-------------|-------|------|--------------|--------|
| 1 | Fix finalize() deprecation | 2 | Medium | None | ✅ [#512](https://github.com/metaschema-framework/metaschema-java/pull/512) |
| 2 | Fix @Component deprecation | 1 | Low | None | ✅ [#513](https://github.com/metaschema-framework/metaschema-java/pull/513) |
| 3 | Fix FunctionUtils (math ops) | 11 | Medium | None | ✅ [#514](https://github.com/metaschema-framework/metaschema-java/pull/514) |
| 4 | Fix FunctionUtils (function library) | 5 | Medium | PR 3 | ✅ [#520](https://github.com/metaschema-framework/metaschema-java/pull/520) |
| 5 | Fix FunctionUtils (type requirements) | ~6 | Medium | PR 4 (recommended) | Pending |
| 6 | Fix INcNameItem deprecation | ~5 | Medium | None | Pending |
| 7 | Javadoc: core/model | ~40-50 | Low | None | Pending |
| 8 | Javadoc: core/metapath | ~30-40 | Low | None | Pending |
| 9 | Javadoc: core/remaining | ~20-30 | Low | None | Pending |
| 10 | Javadoc: databind | ~50 | Low | None | Pending |
| 11 | Javadoc: schemagen | ~40-50 | Low | None | Pending |
| 12 | Javadoc: remaining | ~20-30 | Low | None | Pending |

**Total Estimated PRs**: 12
**Completed PRs**: 4
**Total Estimated Files**: ~250-300 (within limits when split across PRs)

## Related Issues

| Issue | Description | Status |
|-------|-------------|--------|
| [#515](https://github.com/metaschema-framework/metaschema-java/issues/515) | Environmental test timeouts on Windows | Open |
