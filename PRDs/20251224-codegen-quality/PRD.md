# PRD: Code Generator Quality Improvements

## Document Information

| Field | Value |
|-------|-------|
| **PRD ID** | CODEGEN-001 |
| **Status** | Completed |
| **Author** | David Waltermire |
| **Created** | 2025-12-24 |
| **Last Updated** | 2025-12-28 |

---

## 1. Overview

### 1.1 Problem Statement

The metaschema-maven-plugin code generator produces Java binding classes that do not meet the project's Javadoc and null-safety standards. Generated code requires manual editing to:
- Remove extraneous quote characters from Javadoc descriptions
- Add Javadoc to constructors and accessor methods
- Add null-safety annotations

Additionally, the bootstrap approach for modules with pre-generated binding classes (due to circular dependencies) needs standardization across the project.

### 1.2 Goals

1. Generate binding classes with complete, standards-compliant Javadoc
2. Include appropriate null-safety annotations in generated code
3. Standardize the bootstrap pattern for modules with pre-generated bindings
4. Enable fully automated regeneration of databind binding classes

### 1.3 Non-Goals

- Refactoring the code generator architecture
- Adding new Metaschema features
- Performance optimization of code generation

### 1.4 Success Metrics

| Metric | Current | Target |
|--------|---------|--------|
| Generated code passes checkstyle | No (Javadoc violations) | Yes |
| Manual post-generation edits required | Yes | No |
| Modules with documented bootstrap process | 1 (metaschema-testing) | 2 (+ databind) |
| Null-safety annotation coverage | 0% | 100% of generated public API |

---

## 2. Background

### 2.1 Current State

The code generator produces binding classes with several quality issues identified during PR #567 (XMLBeans removal from metaschema-testing):

1. **Extraneous quotes in Javadoc**: When generating Javadoc from YAML Metaschema descriptions, literal quote characters are included in the output
2. **Missing constructor Javadoc**: Both the no-arg and data constructor lack documentation
3. **Missing accessor Javadoc**: Getter and setter methods have no Javadoc, including `@param` and `@return` tags
4. **No null-safety annotations**: Generated code lacks `@Nullable` and `@NonNull` annotations

### 2.2 Technical Context

**Key Files:**
- `INamedInstanceTypeInfo.java:21` - Uses `$S` format in JavaPoet, which adds quotes
- `DefaultMetaschemaClassFactory.java:387-396` - Generates constructors without Javadoc
- `AbstractPropertyTypeInfo.java:63-78` - Generates getters/setters without Javadoc

**Binding Configuration Support:**
The code generator already supports `implement-interface` and `extend-base-class` binding configurations. These are parsed in `DefaultBindingConfiguration.java` and applied in `DefaultMetaschemaClassFactory.java`. Issue #572 relates to documenting this capability and ensuring the databind bootstrap process works correctly.

**Bootstrap Pattern:**
`metaschema-testing` established the pattern in PR #567:
- `pom-bootstrap.xml` for standalone code generation
- Pre-generated classes in `src/main/java`
- README documentation of regeneration process

---

## 3. Requirements

### 3.1 Functional Requirements

#### FR-1: Remove Quotes from Javadoc Descriptions
Generated Javadoc must not include extraneous quote characters from YAML string values. The description text should be rendered directly without surrounding quotes.

#### FR-2: Generate Constructor Javadoc
Both constructors must have complete Javadoc:
- No-arg constructor: Brief description of constructing with no metadata
- Data constructor: Description with `@param` tag for the metadata parameter, including null behavior

#### FR-3: Generate Accessor Method Javadoc
Getter methods must include:
- Description derived from the Metaschema field description
- `@return` tag describing the return value and null behavior

Setter methods must include:
- Description derived from the Metaschema field description
- `@param` tag describing the parameter

#### FR-4: Add Null-Safety Annotations
Generated code must include SpotBugs null-safety annotations:
- `@Nullable` on parameters and returns that can be null
- `@NonNull` where nulls are not permitted (based on `required` attribute)

#### FR-5: Collection Implementation Override
Support overriding the default collection implementation class (`LinkedList`, `LinkedHashMap`) via binding configuration:

```xml
<define-assembly name="test-suite">
  <define-field name="test-collections">
    <collection-class>java.util.ArrayList</collection-class>
  </define-field>
</define-assembly>
```

This enables:
- `ArrayList` instead of `LinkedList` for better random access performance
- `TreeMap` instead of `LinkedHashMap` for sorted key ordering
- Custom collection implementations for specialized use cases

#### FR-6: Databind Bootstrap Support
Create `pom-bootstrap.xml` for the databind module enabling regeneration of binding classes without circular dependency issues.

### 3.2 Non-Functional Requirements

#### NFR-1: Checkstyle Compliance
All generated code must pass the project's checkstyle configuration without violations.

#### NFR-2: Backward Compatibility
Regenerated binding classes must maintain the same public API as existing classes.

#### NFR-3: Documentation
Each module with pre-generated bindings must have documented bootstrap instructions in its README.

---

## 4. Implementation Phases

### PR 1: Code Generator Improvements and Verification ✅
Fix the code generator to produce complete Javadoc and null-safety annotations, then regenerate metaschema-testing binding classes to verify the improvements. This addresses issues #568, #571, and #575.

**Status:** Completed - [PR #577](https://github.com/metaschema-framework/metaschema-java/pull/577)

### PR 2: Collection Class Override Support ✅
Extend the binding configuration to support overriding default collection implementation classes. This addresses issue #572 (partial).

Key changes:
- Add `<collection-class>` element to binding configuration schema
- Parse collection class override in `DefaultBindingConfiguration`
- Pass override to `getCollectionImplementationClass()` in type info classes

**Status:** Completed - [PR #584](https://github.com/metaschema-framework/metaschema-java/pull/584)

### PR 3: Databind Bootstrap and Regeneration ✅
Create databind bootstrap POM and documentation. Regenerate databind binding classes using the improved generator. This addresses issues #572 (remaining) and #573.

**Status:** Completed - Combined with PR 2 in [PR #584](https://github.com/metaschema-framework/metaschema-java/pull/584)

### PR 4: Parser Required Field Validation ✅
Add validation during parsing to emit meaningful errors when required fields are missing, and validate type compatibility for collection class overrides.

Key changes:
- Parser validates required fields are present during deserialization
- Missing required field produces clear error with field name and location
- Collection class override validates type compatibility (Collection/Map)
- Choice group support - only error if ALL options in choice are missing

**Status:** Completed - [PR #593](https://github.com/metaschema-framework/metaschema-java/pull/593)

### PR 5: Choice Instance Support for Bindings ✅
Add full choice instance support to annotation-based bindings, enabling required field validation to work correctly for dynamically compiled modules.

**Status:** Completed - Addressed by [PR #593](https://github.com/metaschema-framework/metaschema-java/pull/593) using typed collections approach

See [Implementation Plan](./implementation-plan.md) for detailed breakdown.

---

## 5. Testing Strategy

### 5.1 Test Approach

1. **Unit Tests**: Add tests for Javadoc generation methods to verify correct output format
2. **Integration Tests**: Regenerate test binding classes and verify checkstyle compliance
3. **Comparison Testing**: Compare regenerated databind classes with existing ones to verify API compatibility

### 5.2 Verification Checklist

- [x] Generated Javadoc contains no extraneous quotes
- [x] Constructor Javadoc includes all required tags
- [x] Accessor Javadoc includes `@param` and `@return` tags
- [x] Null-safety annotations are present on all applicable elements
- [x] `mvn checkstyle:check` passes on generated code
- [x] Regenerated databind classes compile and tests pass
- [x] Bootstrap documentation is complete and accurate
- [x] Required field validation at parse time
- [x] Collection class type compatibility validation
- [x] Choice instance support for annotation-based bindings (PR 5)

---

## 6. Risks and Mitigations

| Risk | Impact | Likelihood | Mitigation |
|------|--------|------------|------------|
| Breaking changes to generated API | High | Low | Compare old/new classes systematically; maintain same public signatures |
| Missing edge cases in Javadoc generation | Medium | Medium | Comprehensive testing with various description formats |
| Circular dependency issues with databind bootstrap | Medium | Low | Follow established metaschema-testing pattern; test regeneration |

---

## 7. Open Questions

1. ~~Should `@NonNull` be used for `required` fields, or should all fields use `@Nullable` since Java objects can be partially constructed?~~
   **Resolved:** PR #577 implements `@NonNull` for required flags (where `required=true`) and model instances (where `minOccurs>=1` and `maxOccurs=1`). Collections use `@NonNull` with lazy initialization.

2. Are there any databind binding classes with custom methods that would be lost on regeneration?

3. What collection types should be validated as compatible with List vs Map configurations?
   - List-compatible: `ArrayList`, `LinkedList`, `CopyOnWriteArrayList`, etc.
   - Map-compatible: `HashMap`, `LinkedHashMap`, `TreeMap`, `ConcurrentHashMap`, etc.

---

## 8. Related Documents

- [Implementation Plan](./implementation-plan.md)
- [GitHub Issue #568](https://github.com/metaschema-framework/metaschema-java/issues/568) - Quotes in Javadoc
- [GitHub Issue #571](https://github.com/metaschema-framework/metaschema-java/issues/571) - Complete Javadoc
- [GitHub Issue #572](https://github.com/metaschema-framework/metaschema-java/issues/572) - Interface patterns
- [GitHub Issue #573](https://github.com/metaschema-framework/metaschema-java/issues/573) - Bootstrap standardization
- [GitHub Issue #575](https://github.com/metaschema-framework/metaschema-java/issues/575) - Consolidated improvements
