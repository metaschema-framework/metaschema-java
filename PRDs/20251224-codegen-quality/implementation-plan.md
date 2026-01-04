# Implementation Plan: Code Generator Quality Improvements

This document details each PR in the code generator quality improvements initiative.

---

## Prerequisites

- Understand JavaPoet format specifiers (`$S` adds quotes, `$L` is literal)
- Review existing metaschema-testing bootstrap pattern (PR #567)
- Familiarize with SpotBugs null-safety annotations

---

## Test-Driven Development Requirement

**All functional code changes must follow TDD:**

1. Write or update tests first to capture expected behavior
2. Verify tests pass with existing implementation
3. Make the code changes
4. Verify tests still pass after changes

---

## PR 1: Code Generator Improvements and Verification ✅

| Attribute | Value |
|-----------|-------|
| **Files Changed** | 20 |
| **Risk Level** | Low |
| **Dependencies** | None |
| **Target Branch** | develop |
| **Status** | Completed |
| **Pull Request** | [#577](https://github.com/metaschema-framework/metaschema-java/pull/577) |

This PR fixes the code generator and regenerates metaschema-testing binding classes to verify the improvements work correctly.

### Code Generator Files to Modify

| File | Changes |
|------|---------|
| `databind/src/main/java/gov/nist/secauto/metaschema/databind/codegen/typeinfo/INamedInstanceTypeInfo.java` | Change `$S` to literal format to remove quotes from Javadoc |
| `databind/src/main/java/gov/nist/secauto/metaschema/databind/codegen/typeinfo/DefaultMetaschemaClassFactory.java` | Add Javadoc to constructor generation |
| `databind/src/main/java/gov/nist/secauto/metaschema/databind/codegen/typeinfo/AbstractPropertyTypeInfo.java` | Add Javadoc to getter/setter generation |
| `databind/src/main/java/gov/nist/secauto/metaschema/databind/codegen/typeinfo/AbstractModelDefinitionTypeInfo.java` | Add null-safety annotations to generated code |

### Regenerated Files

| File | Changes |
|------|---------|
| `metaschema-testing/src/main/java/gov/nist/secauto/metaschema/model/testing/testsuite/*.java` | Regenerated binding classes with improved Javadoc |

### Implementation Approach

1. **Fix Javadoc quote issue** (Issue #568)
   - In `INamedInstanceTypeInfo.java`, change the `buildFieldJavadoc` method
   - Replace `$S` format (which adds quotes) with `$L` for literal text
   - Ensure HTML content is properly escaped for Javadoc

2. **Add constructor Javadoc** (Issue #571)
   - In `DefaultMetaschemaClassFactory.java`, modify `buildClass()` method
   - Add Javadoc to no-arg constructor: "Constructs a new {ClassName} instance with no metadata."
   - Add Javadoc to data constructor with `@param` tag for metadata parameter

3. **Add accessor Javadoc** (Issue #571)
   - In `AbstractPropertyTypeInfo.java`, modify `buildExtraMethods()`
   - Generate getter Javadoc with description from Metaschema and `@return` tag
   - Generate setter Javadoc with description and `@param` tag
   - Use the field's description for method documentation

4. **Add null-safety annotations** (Issue #571)
   - Import `edu.umd.cs.findbugs.annotations.Nullable` and `NonNull`
   - Add `@Nullable` to constructor parameter, getter returns, setter parameters
   - Consider `required` attribute for `@NonNull` where appropriate

5. **Regenerate metaschema-testing binding classes**
   - Build the project to get updated code generator
   - Run bootstrap generation: `mvn -f metaschema-testing/pom-bootstrap.xml generate-sources`
   - Copy generated files to source directory
   - Verify generated code passes checkstyle

### Acceptance Criteria

- [x] Generated Javadoc does not contain extraneous quote characters
- [x] No-arg constructor has descriptive Javadoc
- [x] Data constructor has Javadoc with `@param` tag
- [x] Getter methods have Javadoc with `@return` tag describing null behavior
- [x] Setter methods have Javadoc with `@param` tag
- [x] Null-safety annotations present on generated public API
- [x] metaschema-testing binding classes regenerated with improvements
- [x] `mvn checkstyle:check` passes on all modified/regenerated files
- [x] All tests pass: `mvn test`
- [x] Build succeeds: `mvn clean install -PCI -Prelease`

---

## PR 2: Collection Class Override Support ✅

| Attribute | Value |
|-----------|-------|
| **Files Changed** | ~15 |
| **Risk Level** | Low |
| **Dependencies** | PR 1 |
| **Target Branch** | develop |
| **Status** | Completed |
| **Pull Request** | [#584](https://github.com/metaschema-framework/metaschema-java/pull/584) |

This PR extends the binding configuration to support overriding default collection implementation classes.

### Files to Modify

| File | Changes |
|------|---------|
| `databind/src/main/java/gov/nist/secauto/metaschema/databind/codegen/config/IBindingConfiguration.java` | Add `getCollectionClass()` method to interface |
| `databind/src/main/java/gov/nist/secauto/metaschema/databind/codegen/config/DefaultBindingConfiguration.java` | Parse `<collection-class>` element |
| `databind/src/main/java/gov/nist/secauto/metaschema/databind/codegen/typeinfo/IPropertyTypeInfo.java` | Update `getCollectionImplementationClass()` to accept override |
| `databind/src/main/java/gov/nist/secauto/metaschema/databind/codegen/typeinfo/AbstractModelInstanceTypeInfo.java` | Apply collection class override from binding config |
| `databind-modules/src/main/metaschema-bindings/metaschema-bindings.yaml` | Add `collection-class` field definition to binding schema |

### Implementation Approach

1. **Update binding configuration schema**
   - Add `collection-class` element to the metaschema-bindings schema
   - Element should accept fully-qualified class name as string value

2. **Parse collection class in DefaultBindingConfiguration**
   - Read `<collection-class>` element from binding configuration XML
   - Store in property-level configuration
   - Validate class exists and is accessible

3. **Apply override in type info**
   - Modify `getCollectionImplementationClass()` to check for binding override
   - If override specified, use that class instead of default
   - Validate type compatibility:
     - For List fields: class must implement `java.util.List`
     - For Map fields: class must implement `java.util.Map`

4. **Add tests**
   - Test parsing of collection-class from binding configuration
   - Test that override is applied during code generation
   - Test validation rejects incompatible types

### Example Binding Configuration

```xml
<metaschema-bindings xmlns="https://csrc.nist.gov/ns/metaschema-binding/1.0">
  <define-assembly name="test-suite">
    <define-field name="test-collections">
      <collection-class>java.util.ArrayList</collection-class>
    </define-field>
  </define-assembly>
</metaschema-bindings>
```

### Acceptance Criteria

- [x] Binding configuration schema supports `<collection-class>` element
- [x] `DefaultBindingConfiguration` parses collection-class from XML
- [x] Override is applied in `getCollectionImplementationClass()` (infrastructure ready)
- [ ] Type compatibility validation (List vs Map) - deferred to PR 4
- [x] Unit tests for collection-class parsing and application
- [x] `mvn checkstyle:check` passes
- [x] All tests pass: `mvn test`
- [x] Build succeeds: `mvn clean install -PCI -Prelease`

### Files Actually Modified

| File | Changes |
|------|---------|
| `databind/src/main/metaschema/metaschema-bindings.yaml` | Added `collection-class` field and fixed `object-type` keywords |
| `databind/src/main/java/.../config/binding/MetaschemaBindings.java` | Regenerated with `getCollectionClass()` method |
| `databind/src/main/java/.../config/binding/MetaschemaBindingsModule.java` | Regenerated module class |
| `databind/src/main/java/.../config/binding/package-info.java` | Regenerated package info |
| `databind/src/main/java/.../codegen/config/DefaultBindingConfiguration.java` | Added property binding parsing |
| `databind/src/main/java/.../codegen/config/IPropertyBindingConfiguration.java` | New interface for property bindings |
| `databind/src/main/java/.../codegen/config/IMutablePropertyBindingConfiguration.java` | New mutable interface |
| `databind/src/main/java/.../codegen/config/DefaultPropertyBindingConfiguration.java` | New implementation |
| `databind/pom-bootstrap.xml` | Simplified bootstrap POM for direct generation |
| `databind/src/test/resources/metaschema/binding-config-with-collection-class.xml` | Test configuration |

---

## PR 3: Databind Bootstrap and Regeneration ✅

| Attribute | Value |
|-----------|-------|
| **Files Changed** | ~55 |
| **Risk Level** | Medium |
| **Dependencies** | PR 1, PR 2 |
| **Target Branch** | develop |
| **Status** | Completed |
| **Pull Request** | (combined with PR 2 in #584) |

This PR creates the databind bootstrap infrastructure and regenerates the databind binding classes.

### Files Created

| File | Changes |
|------|---------|
| `databind/pom-bootstrap-config.xml` | Bootstrap POM for config binding classes |
| `databind/pom-bootstrap-model.xml` | Bootstrap POM for model binding classes |
| `databind/README.md` | Documents bootstrap process for both binding class sets |
| `databind/src/main/metaschema-bindings/metaschema-model-bindings.xml` | Binding configuration for model classes |

### Files Updated

| File | Changes |
|------|---------|
| `CLAUDE.md` | Updated Bootstrap Binding Classes section with databind POMs |
| `databind/src/main/metaschema-bindings/metaschema-config-bindings.xml` | Renamed from `metaschema-bindings.xml` |

### Files Regenerated

| File | Changes |
|------|---------|
| `databind/src/main/java/.../config/binding/*.java` | Regenerated config binding classes |
| `databind/src/main/java/.../model/metaschema/binding/*.java` | Regenerated model binding classes (~40 files) |

### Implementation Notes

1. **Two separate bootstrap POMs created**
   - `pom-bootstrap-config.xml` - for config binding classes from `metaschema-bindings.yaml`
   - `pom-bootstrap-model.xml` - for model binding classes from `metaschema-module-metaschema.xml`

2. **Binding configuration for model classes**
   - Created `metaschema-model-bindings.xml` to customize generated classes
   - Adds interface implementations (e.g., `IModelConstraintsBase`, `IValueConstraintsBase`)
   - Renames `group-as` to `GroupingAs` to avoid Java keyword conflicts

3. **README documents both bootstrap processes**
   - Separate sections for config and model binding classes
   - Includes regeneration commands and explains when to regenerate

### Acceptance Criteria

- [x] `pom-bootstrap-config.xml` successfully generates config binding classes
- [x] `pom-bootstrap-model.xml` successfully generates model binding classes
- [x] README documents the complete regeneration process
- [x] CLAUDE.md references databind bootstrap documentation
- [x] All databind binding classes regenerated
- [x] Custom interface implementations preserved via binding configuration
- [x] Base class extensions preserved via binding configuration
- [x] Generated Javadoc is complete and properly formatted
- [x] Null-safety annotations present
- [x] `mvn checkstyle:check` passes on regenerated files
- [x] All tests pass: `mvn test`
- [x] Build succeeds: `mvn clean install -PCI -Prelease`

---

## PR 4: Parser Required Field Validation ✅

| Attribute | Value |
|-----------|-------|
| **Files Changed** | ~25 |
| **Risk Level** | Medium |
| **Dependencies** | PR 1 |
| **Target Branch** | develop |
| **Status** | Completed |
| **Pull Request** | [#593](https://github.com/metaschema-framework/metaschema-java/pull/593) |

This PR adds validation during parsing to emit meaningful errors when required fields are missing, and includes type compatibility validation for collection class overrides.

### Motivation

Currently, when a required field/flag is missing from input data, the generated binding classes have `@NonNull` annotations on getters but the parser doesn't actively validate and provide meaningful errors. This can result in confusing null pointer exceptions later rather than clear parse-time errors.

### Files to Modify

| File | Changes |
|------|---------|
| `databind/src/main/java/.../io/xml/DefaultXmlDeserializer.java` | Add required field validation |
| `databind/src/main/java/.../io/json/DefaultJsonDeserializer.java` | Add required field validation |
| `databind/src/main/java/.../codegen/typeinfo/AbstractModelInstanceTypeInfo.java` | Validate collection class type compatibility |

### Implementation Approach

1. **Required field validation at parse time**
   - After parsing an object, check if all required fields (those with `@NonNull` getters) have values
   - Emit meaningful error message with field name and source location
   - Must be efficient - check once after parsing, not on every field access

2. **Collection class type compatibility**
   - When collection-class override is specified, validate at code generation time:
     - For List fields: class must implement `java.util.List`
     - For Map fields: class must implement `java.util.Map`
   - Emit clear error if incompatible type specified

3. **Performance considerations**
   - Required field tracking should use bitsets or similar compact representation
   - Validation should happen once per object, not per field
   - No reflection at runtime - track requirements at class binding time

### Acceptance Criteria

- [x] Parser validates required fields are present during deserialization
- [x] Missing required field produces clear error with field name and location
- [x] Collection class override validates type compatibility (Collection/Map)
- [x] Validation is efficient (no per-field overhead)
- [x] Choice group support - only error if ALL options in choice are missing
- [x] Unit tests for required field validation
- [x] Unit tests for collection class type validation
- [x] Required field validation enabled by default
- [x] CLI validators disable required field validation (schema handles it)
- [x] `mvn checkstyle:check` passes
- [x] All tests pass: `mvn test`
- [x] Build succeeds: `mvn clean install -PCI -Prelease`

---

## PR 5: Choice Instance Support for Annotation-Based Bindings ✅

| Attribute | Value |
|-----------|-------|
| **Files Changed** | (included in PR 4) |
| **Risk Level** | Medium |
| **Dependencies** | PR 4 |
| **Target Branch** | develop |
| **Status** | Completed |
| **Issue** | [#594](https://github.com/metaschema-framework/metaschema-java/issues/594), [#262](https://github.com/metaschema-framework/metaschema-java/issues/262) |
| **Pull Request** | Addressed by [#593](https://github.com/metaschema-framework/metaschema-java/pull/593) |

This PR adds full choice instance support to annotation-based bindings, enabling required field validation to work correctly for dynamically compiled modules.

### Background

Metaschema has two related but distinct concepts:

| Concept | Interface | Purpose | Current Support |
|---------|-----------|---------|-----------------|
| **Choice** | `IChoiceInstance` | Mutually exclusive alternatives | ❌ Not supported |
| **Choice Group** | `IChoiceGroupInstance` | Polymorphic collection with discriminator | ✅ `@BoundChoiceGroup` |

PR 4 added required field validation with choice group support, but only for `DefinitionAssemblyGlobal` (Metaschema-loaded modules). Dynamically compiled modules use `DefinitionAssembly` which returns an empty list for `getChoiceInstances()`.

### Files to Create

| File | Purpose |
|------|---------|
| `databind/.../annotations/BoundChoice.java` | Annotation to mark fields in a choice |
| `databind/.../model/impl/InstanceModelChoice.java` | `IChoiceInstance` implementation for bindings |

### Files to Modify

| File | Changes |
|------|---------|
| `databind/.../model/impl/AssemblyModelGenerator.java` | Group `@BoundChoice` fields, create choice instances |
| `databind/.../codegen/typeinfo/AbstractModelInstanceTypeInfo.java` | Emit `@BoundChoice` for fields in choices |
| Bootstrap binding classes | Regenerate with new annotations |

### Implementation Approach

1. **New `@BoundChoice` annotation**
   - `choiceId` attribute to group mutually exclusive fields
   - Applied to fields within Metaschema `<choice>` elements

2. **New `InstanceModelChoice` class**
   - Implements `IChoiceInstance`
   - Wraps a list of `IBoundInstanceModelNamed<?>` instances
   - Provides `getNamedModelInstances()` for validation

3. **Update `AssemblyModelGenerator`**
   - Collect fields annotated with `@BoundChoice`
   - Group by `choiceId`
   - Create `InstanceModelChoice` for each group
   - Call `builder.append(choiceInstance)`

4. **Update code generator**
   - Track choice context during model traversal
   - Emit `@BoundChoice(choiceId = "choice-N")` on fields within choices

5. **Adjacency validation**
   - Choice fields must be adjacent in the model (same position in serialization order)
   - Validate at binding initialization that all fields with same `choiceId` are consecutive
   - Throw `IllegalStateException` if non-adjacent choice fields detected
   - Catches code generator bugs, manual edits, and inheritance issues

### Acceptance Criteria

> **Note:** This work was addressed as part of PR #593 by using choice groups with typed collections instead of creating a separate `@BoundChoice` annotation. The typed collection approach provides similar functionality while leveraging existing infrastructure.

- [x] Choice group support works for annotation-based bindings
- [x] Required field validation works for dynamically compiled modules
- [x] `mvn checkstyle:check` passes
- [x] All tests pass: `mvn test`
- [x] Build succeeds: `mvn clean install -PCI -Prelease`

---

## PR Summary Table

| PR | Description | Files | Risk | Dependencies | Status |
|----|-------------|-------|------|--------------|--------|
| 1 | Code generator improvements + metaschema-testing regeneration | 20 | Low | None | ✅ Completed ([#577](https://github.com/metaschema-framework/metaschema-java/pull/577)) |
| 2 | Collection class override support | ~15 | Low | PR 1 | ✅ Completed ([#584](https://github.com/metaschema-framework/metaschema-java/pull/584)) |
| 3 | Databind bootstrap setup + regeneration | ~55 | Medium | PR 1, PR 2 | ✅ Completed (combined with PR 2) |
| 4 | Parser required field validation | ~25 | Medium | PR 1 | ✅ Completed ([#593](https://github.com/metaschema-framework/metaschema-java/pull/593)) |
| 5 | Choice instance support for bindings | - | Medium | PR 4 | ✅ Completed (addressed by [#593](https://github.com/metaschema-framework/metaschema-java/pull/593)) |

**Total Actual PRs**: 3 (PRs 2+3 combined, PRs 4+5 combined)
**All planned work completed.**

---

## Verification Commands

After each PR, run:

```bash
# Full CI build
mvn clean install -PCI -Prelease

# Checkstyle verification
mvn checkstyle:check

# Specific module tests
mvn -pl databind test
mvn -pl metaschema-testing test
```

---

## Related Issues

| Issue | Description | Addressed In |
|-------|-------------|--------------|
| #568 | Quotes in Javadoc | ✅ PR 1 |
| #571 | Complete Javadoc | ✅ PR 1 |
| #572 | Interface patterns + collection class override | PR 2, PR 3 |
| #573 | Bootstrap standardization | PR 3 |
| #575 | Consolidated improvements | ✅ PR 1 |
| #594 | Choice instance support for annotation-based bindings | PR 5 |
| #595 | Format-appropriate names in validation error messages | Future |
