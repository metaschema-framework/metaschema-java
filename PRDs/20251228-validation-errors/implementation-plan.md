# Implementation Plan: Validation Error Message Improvements

## Overview

This plan implements issues #595, #596, and #205 to improve validation error messages during deserialization. The work follows TDD principles - tests are written first for each feature.

## PR Structure

Single PR addressing all three related issues, organized into logical implementation phases.

## Phase 1: Foundation - ValidationContext and IResourceLocation

### Task 1.1: Create ValidationContext class

**File**: `databind/src/main/java/gov/nist/secauto/metaschema/databind/io/ValidationContext.java`

Create a new class to bundle parsing context:

```java
public class ValidationContext {
  private final URI source;
  private final IResourceLocation location;
  private final String path;
  private final Format format;

  // Factory methods, getters, formatting helpers
}
```

**Acceptance Criteria**:
- [ ] Class created with URI, IResourceLocation, path, and Format fields
- [ ] Factory methods for XML and JSON contexts
- [ ] Method to format location string (handles null/unknown gracefully)
- [ ] Method to format path string (handles root level)
- [ ] Javadoc complete

### Task 1.2: Create PathTracker utility

**File**: `databind/src/main/java/gov/nist/secauto/metaschema/databind/io/PathTracker.java`

Lightweight path tracking during parsing:

```java
public class PathTracker {
  private final Deque<String> segments = new ArrayDeque<>();

  public void push(String segment);
  public void pop();
  public String getCurrentPath();
}
```

**Acceptance Criteria**:
- [ ] Push/pop segment methods
- [ ] getCurrentPath() returns "/" for empty, "/a/b/c" format otherwise
- [ ] Thread-safe or documented as not thread-safe
- [ ] Javadoc complete

### Task 1.3: Create IResourceLocation implementation

**File**: `core/src/main/java/gov/nist/secauto/metaschema/core/model/SimpleResourceLocation.java`

Simple implementation of IResourceLocation:

```java
public class SimpleResourceLocation implements IResourceLocation {
  private final int line;
  private final int column;
  private final long charOffset;
  private final long byteOffset;

  // Static factory methods for XML Location, JSON JsonLocation
}
```

**Acceptance Criteria**:
- [ ] Implements IResourceLocation interface
- [ ] Factory method from javax.xml.stream.Location
- [ ] Factory method from com.fasterxml.jackson.core.JsonLocation
- [ ] Handles -1 for unknown values
- [ ] Javadoc complete

## Phase 2: Tests First - Error Message Scenarios

### Task 2.1: Create test infrastructure

**File**: `databind/src/test/java/gov/nist/secauto/metaschema/databind/io/ValidationErrorMessageTest.java`

Create test class with helper methods for parsing test documents.

**Acceptance Criteria**:
- [ ] Test Metaschema module with required flags, fields, assemblies
- [ ] Helper methods to parse XML and JSON test content
- [ ] Helper to capture and assert on error messages

### Task 2.2: Write tests for format-appropriate names (Issue #595)

**Tests**:
```java
@Test void testMissingRequiredFlagShowsXmlNameForXml();
@Test void testMissingRequiredFlagShowsJsonNameForJson();
@Test void testMissingRequiredFieldShowsXmlNameForXml();
@Test void testMissingRequiredFieldShowsJsonNameForJson();
@Test void testMissingRequiredAssemblyShowsXmlNameForXml();
@Test void testMissingRequiredAssemblyShowsJsonNameForJson();
```

**Acceptance Criteria**:
- [ ] Tests verify error messages contain format-appropriate names
- [ ] Tests cover flags, fields, and assemblies
- [ ] Tests initially fail (no implementation yet)

### Task 2.3: Write tests for location information (Issue #596)

**Tests**:
```java
@Test void testErrorIncludesFileUri();
@Test void testErrorIncludesLineNumber();
@Test void testErrorIncludesColumnNumber();
@Test void testErrorIncludesPath();
@Test void testErrorAtDocumentRoot();
@Test void testErrorWithoutSourceUri();
@Test void testMultipleMissingPropertiesGroupedByType();
```

**Acceptance Criteria**:
- [ ] Tests verify location info in error messages
- [ ] Tests verify path context
- [ ] Tests cover edge cases (root level, no URI)
- [ ] Tests initially fail

### Task 2.4: Write tests for null field values (Issue #205)

**Tests**:
```java
@Test void testNullFieldValueShowsContextualError();
@Test void testNullFieldValueIncludesLocation();
@Test void testNullFieldValueIncludesPath();
@Test void testNullFieldValueDoesNotThrowNpe();
```

**Acceptance Criteria**:
- [ ] Tests verify null field values produce informative errors
- [ ] Tests verify no NPE is thrown
- [ ] Tests verify location and path context
- [ ] Tests initially fail

### Task 2.5: Write tests for edge cases

**Tests**:
```java
@Test void testChoiceGroupOnlyReportsWhenAllMissing();
@Test void testDefaultValueNotReportedAsMissing();
@Test void testPropertyTypeDistinction();
@Test void testSpecialCharsInPathEscaped();
@Test void testYamlUsesJsonNames();
```

**Acceptance Criteria**:
- [ ] Edge case tests written
- [ ] Tests initially fail

## Phase 3: Implementation

### Task 3.1: Update IProblemHandler interface

**File**: `databind/src/main/java/gov/nist/secauto/metaschema/databind/io/IProblemHandler.java`

Add overloaded method with ValidationContext:

```java
default void handleMissingInstances(
    IBoundDefinitionModelComplex parentDefinition,
    IBoundObject targetObject,
    Collection<? extends IBoundProperty<?>> unhandledInstances,
    ValidationContext context) throws IOException {
  // Default delegates to existing method for backward compatibility
  handleMissingInstances(parentDefinition, targetObject, unhandledInstances);
}

void handleNullFieldValue(
    IBoundDefinitionModelFieldComplex fieldDefinition,
    Object parentObject,
    ValidationContext context) throws IOException;
```

**Acceptance Criteria**:
- [ ] New overloaded method with ValidationContext
- [ ] New handleNullFieldValue method
- [ ] Backward compatible default implementation
- [ ] Javadoc complete

### Task 3.2: Update AbstractProblemHandler

**File**: `databind/src/main/java/gov/nist/secauto/metaschema/databind/io/AbstractProblemHandler.java`

Update validateRequiredFields to use ValidationContext:

- Use `getEffectiveName()` instead of `getJsonName()`
- Group missing properties by type (flag/field/assembly)
- Include location and path in error messages
- Implement handleNullFieldValue

**Acceptance Criteria**:
- [ ] validateRequiredFields accepts ValidationContext
- [ ] Error messages use getEffectiveName()
- [ ] Error messages include location info
- [ ] Error messages include path
- [ ] Error messages distinguish property types
- [ ] handleNullFieldValue implemented
- [ ] Existing tests still pass

### Task 3.3: Update MetaschemaJsonReader

**File**: `databind/src/main/java/gov/nist/secauto/metaschema/databind/io/json/MetaschemaJsonReader.java`

Add path tracking and ValidationContext creation:

- Maintain PathTracker during parsing
- Create ValidationContext with current location and path
- Pass context to handleMissingInstances
- Detect null field values and call handleNullFieldValue

**Acceptance Criteria**:
- [ ] PathTracker maintained during parsing
- [ ] ValidationContext created with location from JsonParser
- [ ] Context passed to problem handler
- [ ] Null field value detection implemented

### Task 3.4: Update MetaschemaXmlReader

**File**: `databind/src/main/java/gov/nist/secauto/metaschema/databind/io/xml/MetaschemaXmlReader.java`

Add path tracking and ValidationContext creation:

- Maintain PathTracker during parsing
- Create ValidationContext with current location and path
- Pass context to handleMissingInstances
- Detect null field values and call handleNullFieldValue

**Acceptance Criteria**:
- [ ] PathTracker maintained during parsing
- [ ] ValidationContext created with location from XMLStreamReader
- [ ] Context passed to problem handler
- [ ] Null field value detection implemented

### Task 3.5: Remove requireNonNull from IBoundDefinitionModelFieldComplex

**File**: `databind/src/main/java/gov/nist/secauto/metaschema/databind/model/IBoundDefinitionModelFieldComplex.java`

Update getFieldValue() method at line 77-78:

```java
@Override
@Nullable  // Changed from @NonNull
default Object getFieldValue(@NonNull Object item) {
  return getFieldValue().getValue(item);  // Remove requireNonNull
}
```

**Acceptance Criteria**:
- [ ] requireNonNull removed
- [ ] Return type annotation updated to @Nullable
- [ ] Null values handled during deserialization instead

### Task 3.6: Update format-specific problem handlers

**Files**:
- `databind/src/main/java/gov/nist/secauto/metaschema/databind/io/xml/DefaultXmlProblemHandler.java`
- `databind/src/main/java/gov/nist/secauto/metaschema/databind/io/json/DefaultJsonProblemHandler.java`

Ensure they work with the updated abstract base class.

**Acceptance Criteria**:
- [ ] Both handlers compile with updated base class
- [ ] Handlers pass through to parent implementation correctly

## Phase 4: Verification

### Task 4.1: Run all tests

```bash
mvn -pl databind test
```

**Acceptance Criteria**:
- [ ] All new tests pass
- [ ] All existing tests pass

### Task 4.2: Full build verification

```bash
mvn clean install -PCI -Prelease
```

**Acceptance Criteria**:
- [ ] Build succeeds
- [ ] No SpotBugs issues
- [ ] No PMD violations
- [ ] No Checkstyle violations
- [ ] Coverage >= 60%

### Task 4.3: Commit and PR

**Acceptance Criteria**:
- [ ] Changes committed with descriptive message
- [ ] PR created targeting develop branch
- [ ] PR references issues #595, #596, #205

## Files Changed Summary

| File | Change Type |
|------|-------------|
| `core/.../model/SimpleResourceLocation.java` | New |
| `databind/.../io/ValidationContext.java` | New |
| `databind/.../io/PathTracker.java` | New |
| `databind/.../io/IProblemHandler.java` | Modified |
| `databind/.../io/AbstractProblemHandler.java` | Modified |
| `databind/.../io/json/MetaschemaJsonReader.java` | Modified |
| `databind/.../io/xml/MetaschemaXmlReader.java` | Modified |
| `databind/.../io/xml/DefaultXmlProblemHandler.java` | Modified |
| `databind/.../io/json/DefaultJsonProblemHandler.java` | Modified |
| `databind/.../model/IBoundDefinitionModelFieldComplex.java` | Modified |
| `databind/src/test/.../io/ValidationErrorMessageTest.java` | New |
| Test resources (Metaschema modules, test documents) | New |

## Risk Assessment

| Risk | Mitigation |
|------|------------|
| Breaking existing error message parsing | Maintain backward compatibility in message structure |
| Performance impact of path tracking | PathTracker is lightweight (Deque operations) |
| Missing edge cases | Comprehensive test coverage including edge cases |
| IResourceLocation integration issues | Use existing interface, check for existing implementations |

## Dependencies

- Existing `IResourceLocation` interface in core module
- Jackson JSON parser for JsonLocation
- Woodstox XML parser for Location
