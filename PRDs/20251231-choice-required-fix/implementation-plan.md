# Implementation Plan: Fix isRequired() for Choice Block Properties

## Overview

This PR fixes issue #604 where `isRequired()` incorrectly returns `true` for properties inside choice blocks.

## PR Scope

Single PR containing:
- Fix to `isRequired()` logic
- Javadoc updates
- Unit tests
- Regenerated bootstrap binding classes

## Tasks

### Task 1: Write Unit Tests (TDD - RED phase)

Create tests that verify `isRequired()` behavior before implementing the fix.

**File:** `databind/src/test/java/gov/nist/secauto/metaschema/databind/codegen/typeinfo/AbstractNamedModelInstanceTypeInfoTest.java`

**Test cases:**
- [x] Property outside choice with minOccurs=1, maxOccurs=1 → returns `true`
- [x] Property outside choice with minOccurs=0 → returns `false`
- [x] Property inside choice with minOccurs=1, maxOccurs=1 → returns `false`
- [x] Property inside choice with minOccurs=0 → returns `false`
- [x] Collection property (maxOccurs > 1) → returns `false`

### Task 2: Implement Fix (TDD - GREEN phase)

Update `isRequired()` to check for choice membership.

**File:** `databind/src/main/java/gov/nist/secauto/metaschema/databind/codegen/typeinfo/AbstractNamedModelInstanceTypeInfo.java`

**Change:**
```java
@Override
public boolean isRequired() {
  // Properties inside choice blocks are never required because
  // the requirement is conditional on the choice branch being taken
  if (getChoiceId() != null) {
    return false;
  }

  INSTANCE instance = getInstance();
  return instance.getMinOccurs() >= 1 && instance.getMaxOccurs() == 1;
}
```

### Task 3: Update Javadoc

Document the choice block behavior and enhance setter Javadoc.

**Files:**
- `databind/src/main/java/gov/nist/secauto/metaschema/databind/codegen/typeinfo/IPropertyTypeInfo.java`
- `databind/src/main/java/gov/nist/secauto/metaschema/databind/codegen/typeinfo/INamedModelInstanceTypeInfo.java`

**Changes:**
1. Update `isRequired()` Javadoc to document choice block behavior
2. Enhance `buildSetterJavadoc()` to document nullability for optional parameters:
   - Required: "the value to set"
   - Optional: "the value to set, or {@code null} to clear"

### Task 4: Regenerate Bootstrap Binding Classes

Regenerate all bootstrap binding classes to pick up annotation changes.

```bash
mvn install -DskipTests
mvn -f databind/pom-bootstrap-model.xml generate-sources
mvn -f databind/pom-bootstrap-config.xml generate-sources
mvn -f metaschema-testing/pom-bootstrap.xml generate-sources
```

### Task 5: Verify Build

Run full CI build to ensure all tests pass.

```bash
mvn clean install -PCI -Prelease
```

## Acceptance Criteria

### Core Fix
- [x] All unit tests pass
- [x] New tests verify `isRequired()` returns `false` for choice properties
- [x] `IPropertyTypeInfo.isRequired()` Javadoc documents the choice block behavior

### Generated Code Quality
- [x] All generated getters have correct null-safety annotations:
  - `@NonNull` for required properties (minOccurs >= 1, not in choice)
  - `@NonNull` for collection properties (lazy initialized)
  - `@Nullable` for optional properties (minOccurs = 0 or in choice)
- [x] All generated setters have matching null-safety annotations on parameters
- [x] Generated getter/setter Javadocs document nullability behavior

### Bootstrap Bindings
- [x] Bootstrap binding classes regenerated with correct annotations
- [x] Verify choice properties in regenerated classes have `@Nullable` annotations

### Build Verification
- [x] Build passes: `mvn clean install -PCI -Prelease`

## Files Changed Summary

| File | Change Type |
|------|-------------|
| `databind/.../typeinfo/AbstractNamedModelInstanceTypeInfo.java` | Modified |
| `databind/.../typeinfo/IPropertyTypeInfo.java` | Modified |
| `databind/.../typeinfo/INamedModelInstanceTypeInfo.java` | Modified |
| `databind/.../typeinfo/INamedInstanceTypeInfo.java` | Modified |
| `databind/.../typeinfo/AbstractNamedModelInstanceTypeInfoTest.java` | New |
| `.claude/rules/development-workflow.md` | Modified |
| `databind/.../model/metaschema/binding/*.java` | Regenerated |
| `databind/.../config/binding/*.java` | Regenerated |
| `metaschema-testing/.../testsuite/*.java` | Regenerated |
