# Implementation Plan: External Constraint Set Builder

## Overview

This plan implements a fluent builder API for constructing `IConstraintSet` instances programmatically, enabling tests to avoid XMLBeans-based constraint loading.

## PR Strategy

Two PRs to keep changes focused and reviewable:

1. **PR #1: Core Builder Infrastructure** (~30 files)
   - Builder interfaces and implementations
   - Integration with IModuleMockFactory

2. **PR #2: Test Migration** (~5 files)
   - Migrate ExternalConstraintsModulePostProcessorTest
   - Evaluate MetaConstraintLoaderTest (may remain XML-based)

---

## PR #1: Core Builder Infrastructure

### Commit 1: Create constraint builder interfaces

**Files to create:**
- `core/src/test/java/dev/metaschema/core/testsupport/builder/IConstraintSetBuilder.java`
- `core/src/test/java/dev/metaschema/core/testsupport/builder/IContextBuilder.java`
- `core/src/test/java/dev/metaschema/core/testsupport/builder/IAllowedValuesBuilder.java`
- `core/src/test/java/dev/metaschema/core/testsupport/builder/IMatchesBuilder.java`

**Acceptance Criteria:**
- [ ] `IConstraintSetBuilder` defines fluent API for building constraint sets
- [ ] `IContextBuilder` defines fluent API for building contexts with metapaths
- [ ] `IAllowedValuesBuilder` defines fluent API for allowed-values constraints
- [ ] `IMatchesBuilder` defines fluent API for matches constraints
- [ ] All interfaces follow patterns established by `IModuleBuilder`

### Commit 2: Implement constraint builders

**Files to create:**
- `core/src/test/java/dev/metaschema/core/testsupport/builder/ConstraintSetBuilder.java`
- `core/src/test/java/dev/metaschema/core/testsupport/builder/ContextBuilder.java`
- `core/src/test/java/dev/metaschema/core/testsupport/builder/AllowedValuesBuilder.java`
- `core/src/test/java/dev/metaschema/core/testsupport/builder/MatchesBuilder.java`

**Acceptance Criteria:**
- [ ] `ConstraintSetBuilder` creates `MetaConstraintSet` instances
- [ ] `ContextBuilder` creates `MetaConstraintSet.Context` instances
- [ ] `AllowedValuesBuilder` creates `IAllowedValuesConstraint` instances
- [ ] `MatchesBuilder` creates `IMatchesConstraint` instances
- [ ] Builders properly construct constraint hierarchy

### Commit 3: Integrate with IModuleMockFactory

**Files to modify:**
- `core/src/test/java/dev/metaschema/core/testsupport/builder/IModuleMockFactory.java`
- `core/src/test/java/dev/metaschema/core/testsupport/MockedModelTestSupport.java`

**Acceptance Criteria:**
- [ ] `IModuleMockFactory.constraintSet()` method added
- [ ] `MockedModelTestSupport` provides constraint set builder access
- [ ] Follows same patterns as `module()`, `assembly()`, etc.

### Commit 4: Add unit tests for constraint builders

**Files to create:**
- `core/src/test/java/dev/metaschema/core/testsupport/tests/ConstraintSetBuilderTest.java`

**Acceptance Criteria:**
- [ ] Test basic constraint set creation
- [ ] Test context with metapath expressions
- [ ] Test allowed-values constraint building
- [ ] Test matches constraint building
- [ ] Test nested context hierarchy
- [ ] All tests pass

### Commit 5: Verify build passes

**Acceptance Criteria:**
- [ ] `mvn -pl core test` passes
- [ ] `mvn -pl core clean install -PCI` passes
- [ ] No new warnings introduced

---

## PR #2: Test Migration

### Commit 1: Migrate ExternalConstraintsModulePostProcessorTest

**Files to modify:**
- `core/src/test/java/dev/metaschema/core/model/constraint/ExternalConstraintsModulePostProcessorTest.java`

**Changes:**
- Remove `XmlMetaConstraintLoader` usage
- Remove `IOException` from throws clause
- Use `mocking.constraintSet()` builder instead of XML loading

**Before:**
```java
List<IConstraintSet> constraints
    = new XmlMetaConstraintLoader().load(ObjectUtils.notNull(
        Paths.get("src/test/resources/content/issue184-constraints.xml")));
```

**After:**
```java
IConstraintSet constraints = mocking.constraintSet()
    .context(ctx -> ctx
        .metapath("//*")
        .allowedValues(av -> av
            .target("@value")
            .allowedValue("value1", "Value #1")))
    .build();
```

**Acceptance Criteria:**
- [ ] No XMLBeans/XmlMetaConstraintLoader imports
- [ ] Test still verifies constraint is applied to assembly definition
- [ ] Test passes with same assertions

### Commit 2: Evaluate and potentially migrate MetaConstraintLoaderTest

**Analysis needed:**
This test uses both `XmlMetaConstraintLoader` AND `ModuleLoader` to load a module from XML. Options:

1. **Keep XML-based**: This test specifically tests the XML constraint loading + module loading integration. It may be appropriate to keep it as-is since it tests XMLBeans functionality.

2. **Migrate fully**: Build both module and constraints programmatically. This would change what the test is testing.

3. **Split the test**: Keep XML loading test separate, add new programmatic test.

**Recommended approach:** Option 1 - Keep this test XML-based as it tests the XML loading infrastructure. The goal is to reduce XMLBeans in tests, not eliminate all XML-based tests.

**Files to modify (if migrating):**
- `core/src/test/java/dev/metaschema/core/model/xml/MetaConstraintLoaderTest.java`

**Acceptance Criteria:**
- [ ] Decision documented on whether to migrate
- [ ] If migrated: No XMLBeans imports, tests pass
- [ ] If kept: Add comment explaining why XML-based

### Commit 3: Clean up unused XML test resources (if applicable)

**Files to potentially delete:**
- `core/src/test/resources/content/issue184-constraints.xml` (if no longer used)

**Acceptance Criteria:**
- [ ] Verify no other tests use the XML file before deletion
- [ ] If deleted: Update .gitignore if needed
- [ ] If kept: Document why in a comment

---

## Verification Checklist

After all PRs merged:

- [ ] `mvn clean install -PCI -Prelease` passes from project root
- [ ] No regression in test coverage
- [ ] `ExternalConstraintsModulePostProcessorTest` uses programmatic constraint building
- [ ] Builder API is documented with Javadoc
- [ ] Patterns are consistent with `IModuleBuilder`

---

## File Count Summary

| PR | New Files | Modified Files | Deleted Files | Total |
|----|-----------|----------------|---------------|-------|
| PR #1 | 9 | 2 | 0 | 11 |
| PR #2 | 0 | 1-2 | 0-1 | 1-3 |
| **Total** | 9 | 3-4 | 0-1 | 12-14 |

---

## Future Enhancements (Out of Scope)

If additional constraint types are needed by other tests:
- `IExpectBuilder` for expect constraints
- `IIndexBuilder` for index constraints
- `IUniqueBuilder` for unique constraints
- `ICardinalityBuilder` for cardinality constraints
- `IIndexHasKeyBuilder` for index-has-key constraints
- `ILetBuilder` for let expressions
