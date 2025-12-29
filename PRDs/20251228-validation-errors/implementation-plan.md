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
- [x] Class created with URI, IResourceLocation, path, and Format fields
- [x] Factory methods for XML and JSON contexts
- [x] Method to format location string (handles null/unknown gracefully)
- [x] Method to format path string (handles root level)
- [x] Javadoc complete

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
- [x] Push/pop segment methods
- [x] getCurrentPath() returns "/" for empty, "/a/b/c" format otherwise
- [x] Thread-safe or documented as not thread-safe
- [x] Javadoc complete

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
- [x] Implements IResourceLocation interface
- [x] Factory method from javax.xml.stream.Location
- [x] Factory method from com.fasterxml.jackson.core.JsonLocation
- [x] Handles -1 for unknown values
- [x] Javadoc complete

## Phase 2: Tests First - Error Message Scenarios

### Task 2.1: Create test infrastructure

**File**: `databind/src/test/java/gov/nist/secauto/metaschema/databind/io/ValidationErrorMessageTest.java`

Create test class with helper methods for parsing test documents.

**Acceptance Criteria**:
- [x] Test Metaschema module with required flags, fields, assemblies
- [x] Helper methods to parse XML and JSON test content
- [x] Helper to capture and assert on error messages

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
- [x] Tests verify error messages contain format-appropriate names
- [x] Tests cover flags, fields, and assemblies
- [x] Tests pass with implementation

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
- [x] Tests verify location info in error messages
- [x] Tests verify path context
- [x] Tests cover edge cases (root level, no URI)
- [x] Tests pass with implementation

### Task 2.4: Write tests for null field values (Issue #205)

**Tests**:
```java
@Test void testNullFieldValueDoesNotThrowNpe();
```

**Acceptance Criteria**:
- [x] Tests verify null field values do not cause NPE
- [x] Tests pass with implementation

### Task 2.5: Write tests for edge cases

**Tests**:
```java
@Test void testDefaultValueNotReportedAsMissing();
@Test void testParentElementNameInErrorMessage();
```

**Acceptance Criteria**:
- [x] Edge case tests written
- [x] Tests pass with implementation

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
```

**Acceptance Criteria**:
- [x] New overloaded method with ValidationContext
- [x] Backward-compatible default implementation
- [x] @FunctionalInterface annotation added
- [x] Javadoc complete

### Task 3.2: Update AbstractProblemHandler

**File**: `databind/src/main/java/gov/nist/secauto/metaschema/databind/io/AbstractProblemHandler.java`

Update validateRequiredFields to use ValidationContext:

- Use format-appropriate terminology (attribute/element for XML, property for JSON)
- Group missing properties by type
- Include location and path in error messages

**Acceptance Criteria**:
- [x] validateRequiredFields accepts ValidationContext
- [x] Error messages use format-appropriate terminology
- [x] Error messages include location info
- [x] Error messages include path
- [x] Error messages distinguish property types (attribute vs element for XML)
- [x] Existing tests still pass

### Task 3.3: Update MetaschemaJsonReader

**File**: `databind/src/main/java/gov/nist/secauto/metaschema/databind/io/json/MetaschemaJsonReader.java`

Add path tracking and ValidationContext creation:

- Maintain PathTracker during parsing
- Create ValidationContext with current location and path
- Pass context to handleMissingInstances

**Acceptance Criteria**:
- [x] PathTracker maintained during parsing
- [x] ValidationContext created with location from JsonParser
- [x] Context passed to problem handler

### Task 3.4: Update MetaschemaXmlReader

**File**: `databind/src/main/java/gov/nist/secauto/metaschema/databind/io/xml/MetaschemaXmlReader.java`

Add path tracking and ValidationContext creation:

- Maintain PathTracker during parsing
- Create ValidationContext with current location and path
- Pass context to handleMissingInstances

**Acceptance Criteria**:
- [x] PathTracker maintained during parsing
- [x] ValidationContext created with location from XMLStreamReader
- [x] Context passed to problem handler

### Task 3.5: Update IXmlProblemHandler interface

**File**: `databind/src/main/java/gov/nist/secauto/metaschema/databind/io/xml/IXmlProblemHandler.java`

Add overloaded methods with ValidationContext for XML-specific handling.

**Acceptance Criteria**:
- [x] New overloaded methods with ValidationContext
- [x] Backward-compatible default implementations
- [x] Javadoc complete

### Task 3.6: Handle null URI gracefully

**Files**:
- `databind/src/main/java/gov/nist/secauto/metaschema/databind/io/xml/DefaultXmlDeserializer.java`
- `databind/src/main/java/gov/nist/secauto/metaschema/databind/io/json/DefaultJsonDeserializer.java`

Use synthetic URI when null is passed to prevent NPE.

**Acceptance Criteria**:
- [x] Null URI handled gracefully with fallback synthetic URI
- [x] No NPE thrown when deserializing without URI
- [x] Error messages still informative without URI

## Phase 4: Verification

### Task 4.1: Run all tests

```bash
mvn -pl databind test
```

**Acceptance Criteria**:
- [x] All new tests pass (16 tests)
- [x] All existing tests pass

### Task 4.2: Full build verification

```bash
mvn clean install -PCI -Prelease
```

**Acceptance Criteria**:
- [x] Build succeeds
- [x] No SpotBugs issues
- [x] No PMD violations
- [x] No Checkstyle violations
- [x] Coverage checks pass

### Task 4.3: Commit and PR

**Acceptance Criteria**:
- [x] Changes committed with descriptive message
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
| `databind/.../io/json/DefaultJsonDeserializer.java` | Modified |
| `databind/.../io/xml/MetaschemaXmlReader.java` | Modified |
| `databind/.../io/xml/DefaultXmlDeserializer.java` | Modified |
| `databind/.../io/xml/IXmlProblemHandler.java` | Modified |
| `databind/src/test/.../io/ValidationErrorMessageTest.java` | New |
| `.claude/rules/error-message-terminology.md` | New |
| Test resources (Metaschema modules) | New |

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
