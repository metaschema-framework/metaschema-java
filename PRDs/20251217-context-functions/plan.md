# Implementation Plan: Complete Metapath Context Functions

Related PRD: [PRD.md](PRD.md)
GitHub Issue: [#162](https://github.com/metaschema-framework/metaschema-java/issues/162)

## Overview

This plan implements the remaining Metapath context functions: `fn:position`, `fn:last`, and `fn:default-language`.

---

## PR 1: Add FocusContext Infrastructure

**Branch:** `feature/162-focus-context`
**Target:** `develop`
**Estimated Files:** 2

### Tasks

#### 1.1 Create FocusContext Class
- [ ] Create `core/src/main/java/dev/metaschema/core/metapath/FocusContext.java`
  - [ ] Add `sequence` field (ISequence<?>)
  - [ ] Add `position` field (int, 1-based)
  - [ ] Add `size` field (int, lazy computed from sequence)
  - [ ] Add static factory method `of(IItem item, int position, int size)`
  - [ ] Add static factory method `of(ISequence<?> sequence)` for full sequence context
  - [ ] Add `getContextItem()` method
  - [ ] Add `getPosition()` method
  - [ ] Add `getSize()` method
  - [ ] Add comprehensive Javadoc

#### 1.2 Extend DynamicContext
- [ ] Modify `core/src/main/java/dev/metaschema/core/metapath/DynamicContext.java`
  - [ ] Add `@Nullable FocusContext focusContext` as instance field (NOT in SharedState - focus is local to each scope)
  - [ ] Add `getFocusContext()` method returning `@Nullable FocusContext`
  - [ ] Add overloaded `subContext(@NonNull FocusContext)` method that creates a sub-context with new focus
  - [ ] Ensure existing `subContext()` (no args) **preserves** parent's focusContext (required for nested variable bindings like `some`/`every`/`for` that still need access to outer focus)
  - [ ] Update private constructor to copy focusContext from parent
  - [ ] Add Javadoc for new methods

#### 1.3 Write Unit Tests
- [ ] Create `core/src/test/java/dev/metaschema/core/metapath/FocusContextTest.java`
  - [ ] Test factory method with item/position/size
  - [ ] Test factory method with sequence
  - [ ] Test position returns correct value
  - [ ] Test size returns correct value
  - [ ] Test getContextItem returns correct item

#### 1.4 Verification
- [ ] Run `mvn -pl core test` - all tests pass
- [ ] Run `mvn clean install -PCI -Prelease` - full build passes

---

## PR 2: Implement fn:position and fn:last

**Branch:** `feature/162-position-last`
**Target:** `develop`
**Estimated Files:** 6

### Tasks

#### 2.1 Implement FnPosition
- [ ] Create `core/src/main/java/dev/metaschema/core/metapath/function/library/FnPosition.java`
  - [ ] Define SIGNATURE with:
    - name: "position"
    - namespace: MetapathConstants.NS_METAPATH_FUNCTIONS
    - deterministic()
    - contextDependent()
    - focusDependent()
    - returnType: IIntegerItem.type()
    - returnOne()
  - [ ] Implement execute() method:
    - Get FocusContext from DynamicContext
    - Throw ContextAbsentDynamicMetapathException if absent
    - Return position as IIntegerItem
  - [ ] Add comprehensive Javadoc with spec reference

#### 2.2 Implement FnLast
- [ ] Create `core/src/main/java/dev/metaschema/core/metapath/function/library/FnLast.java`
  - [ ] Define SIGNATURE with:
    - name: "last"
    - namespace: MetapathConstants.NS_METAPATH_FUNCTIONS
    - deterministic()
    - contextDependent()
    - focusDependent()
    - returnType: IIntegerItem.type()
    - returnOne()
  - [ ] Implement execute() method:
    - Get FocusContext from DynamicContext
    - Throw ContextAbsentDynamicMetapathException if absent
    - Return size as IIntegerItem
  - [ ] Add comprehensive Javadoc with spec reference

#### 2.3 Register Functions
- [ ] Modify `core/src/main/java/dev/metaschema/core/metapath/function/library/DefaultFunctionLibrary.java`
  - [ ] Add `registerFunction(FnPosition.SIGNATURE);` with spec comment
  - [ ] Add `registerFunction(FnLast.SIGNATURE);` with spec comment
  - [ ] Remove P1/P2 placeholder comments for these functions

#### 2.4 Update PredicateExpression
- [ ] Modify `core/src/main/java/dev/metaschema/core/metapath/cst/logic/PredicateExpression.java`
  - [ ] Compute sequence size once at start of evaluation
  - [ ] For each item, create a sub-context with FocusContext set
  - [ ] Pass sub-context to predicate evaluation
  - [ ] Update imports as needed

#### 2.5 Write FnPosition Tests
- [ ] Create `core/src/test/java/dev/metaschema/core/metapath/function/library/FnPositionTest.java`
  - [ ] Test position() returns 1 for first item
  - [ ] Test position() returns correct value for middle items
  - [ ] Test position() returns size for last item
  - [ ] Test position() in predicate `item[position() = 2]`
  - [ ] Test position() in expression `position() mod 2 = 0`
  - [ ] Test position() throws XPDY0002 when context absent

#### 2.6 Write FnLast Tests
- [ ] Create `core/src/test/java/dev/metaschema/core/metapath/function/library/FnLastTest.java`
  - [ ] Test last() returns correct sequence size
  - [ ] Test last() in predicate `item[position() = last()]`
  - [ ] Test last() in predicate `item[last() - 1]`
  - [ ] Test last() throws XPDY0002 when context absent

#### 2.7 Write Integration Tests
- [ ] Add tests in appropriate test class for combined usage:
  - [ ] Test `(1, 2, 3)[position() = last()]` returns 3
  - [ ] Test `(1, 2, 3, 4)[position() mod 2 = 1]` returns (1, 3)
  - [ ] Test `(a, b, c)[position() > 1 and position() < last()]` returns b

#### 2.8 Verification
- [ ] Run `mvn -pl core test` - all tests pass
- [ ] Run `mvn clean install -PCI -Prelease` - full build passes

---

## PR 3: Implement fn:default-language

**Branch:** `feature/162-default-language`
**Target:** `develop`
**Estimated Files:** 4

### Tasks

#### 3.1 Extend StaticContext
- [ ] Modify `core/src/main/java/dev/metaschema/core/metapath/StaticContext.java`
  - [ ] Add `@Nullable private final String defaultLanguage` field
  - [ ] Add `getDefaultLanguage()` method (returns "en" if null)
  - [ ] Update Builder:
    - Add `private String defaultLanguage = "en"` field
    - Add `defaultLanguage(String language)` method
  - [ ] Update constructor to set defaultLanguage from builder
  - [ ] Update `buildFrom()` to copy defaultLanguage
  - [ ] Add Javadoc for new methods

#### 3.2 Implement FnDefaultLanguage
- [ ] Create `core/src/main/java/dev/metaschema/core/metapath/function/library/FnDefaultLanguage.java`
  - [ ] Define SIGNATURE with:
    - name: "default-language"
    - namespace: MetapathConstants.NS_METAPATH_FUNCTIONS
    - deterministic()
    - contextDependent()
    - focusIndependent()
    - returnType: IStringItem.type()
    - returnOne()
  - [ ] Implement execute() method:
    - Get default language from StaticContext via DynamicContext
    - Return as IStringItem
  - [ ] Add comprehensive Javadoc with spec reference

#### 3.3 Register Function
- [ ] Modify `core/src/main/java/dev/metaschema/core/metapath/function/library/DefaultFunctionLibrary.java`
  - [ ] Add `registerFunction(FnDefaultLanguage.SIGNATURE);` with spec comment

#### 3.4 Write Tests
- [ ] Create `core/src/test/java/dev/metaschema/core/metapath/function/library/FnDefaultLanguageTest.java`
  - [ ] Test default-language() returns "en" with default context
  - [ ] Test default-language() returns configured value
  - [ ] Test default-language() in expression comparison

#### 3.5 Verification
- [ ] Run `mvn -pl core test` - all tests pass
- [ ] Run `mvn clean install -PCI -Prelease` - full build passes

---

## PR 4: Documentation and Issue Closure

**Branch:** `feature/162-docs`
**Target:** `develop`
**Estimated Files:** 1-2

### Tasks

#### 4.1 Documentation Updates
- [ ] Review and update any affected documentation
- [ ] Ensure all new classes have complete Javadoc

#### 4.2 Issue Closure
- [ ] Verify all acceptance criteria from issue #162 are met:
  - [ ] Positive test cases with full coverage for fn:position
  - [ ] Positive test cases with full coverage for fn:last
  - [ ] Positive test cases with full coverage for fn:default-language
  - [ ] Negative test cases covering error conditions
  - [ ] CI-CD build passes
- [ ] Reference issue #162 in final PR description
- [ ] Update issue #162 checklist with completed items

#### 4.3 Final Verification
- [ ] Run `mvn clean install -PCI -Prelease` - full build passes
- [ ] All PRs merged to develop
- [ ] Issue #162 closed

---

## File Summary

### New Files
1. `core/src/main/java/.../metapath/FocusContext.java`
2. `core/src/main/java/.../function/library/FnPosition.java`
3. `core/src/main/java/.../function/library/FnLast.java`
4. `core/src/main/java/.../function/library/FnDefaultLanguage.java`
5. `core/src/test/java/.../metapath/FocusContextTest.java`
6. `core/src/test/java/.../function/library/FnPositionTest.java`
7. `core/src/test/java/.../function/library/FnLastTest.java`
8. `core/src/test/java/.../function/library/FnDefaultLanguageTest.java`

### Modified Files
1. `core/src/main/java/.../metapath/DynamicContext.java`
2. `core/src/main/java/.../metapath/StaticContext.java`
3. `core/src/main/java/.../cst/logic/PredicateExpression.java`
4. `core/src/main/java/.../function/library/DefaultFunctionLibrary.java`

---

## Progress Tracking

| PR | Status | Branch | Merged |
|----|--------|--------|--------|
| PR 1: FocusContext Infrastructure | Not Started | - | - |
| PR 2: fn:position and fn:last | Not Started | - | - |
| PR 3: fn:default-language | Not Started | - | - |
| PR 4: Documentation | Not Started | - | - |

---

## Notes

- The implementation follows TDD principles: tests are written alongside implementation
- Each PR should be self-contained and independently mergeable
- All PRs must pass `mvn clean install -PCI -Prelease` before merge
- Consider performance impact of computing sequence size for `last()` - may need lazy evaluation
