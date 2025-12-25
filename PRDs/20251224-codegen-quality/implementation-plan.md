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

## PR 2: Collection Class Override Support

| Attribute | Value |
|-----------|-------|
| **Files Changed** | ~10 |
| **Risk Level** | Low |
| **Dependencies** | PR 1 |
| **Target Branch** | develop |
| **Status** | Pending |
| **Pull Request** | |

This PR extends the binding configuration to support overriding default collection implementation classes.

### Files to Modify

| File | Changes |
|------|---------|
| `databind/src/main/java/gov/nist/secauto/metaschema/databind/codegen/config/IBindingConfiguration.java` | Add `getCollectionClass()` method to interface |
| `databind/src/main/java/gov/nist/secauto/metaschema/databind/codegen/config/DefaultBindingConfiguration.java` | Parse `<collection-class>` element |
| `databind/src/main/java/gov/nist/secauto/metaschema/databind/codegen/typeinfo/IPropertyTypeInfo.java` | Update `getCollectionImplementationClass()` to accept override |
| `databind/src/main/java/gov/nist/secauto/metaschema/databind/codegen/typeinfo/AbstractModelInstanceTypeInfo.java` | Apply collection class override from binding config |
| `databind-metaschema/src/main/metaschema/metaschema-bindings.yaml` | Add `collection-class` field definition to binding schema |

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

- [ ] Binding configuration schema supports `<collection-class>` element
- [ ] `DefaultBindingConfiguration` parses collection-class from XML
- [ ] Override is applied in `getCollectionImplementationClass()`
- [ ] Type compatibility validation (List vs Map)
- [ ] Unit tests for collection-class parsing and application
- [ ] `mvn checkstyle:check` passes
- [ ] All tests pass: `mvn test`
- [ ] Build succeeds: `mvn clean install -PCI -Prelease`

---

## PR 3: Databind Bootstrap and Regeneration

| Attribute | Value |
|-----------|-------|
| **Files Changed** | ~35 |
| **Risk Level** | Medium |
| **Dependencies** | PR 1, PR 2 |
| **Target Branch** | develop |
| **Status** | Pending |
| **Pull Request** | |

This PR creates the databind bootstrap infrastructure and regenerates the databind binding classes.

### Files to Create

| File | Changes |
|------|---------|
| `databind/pom-bootstrap.xml` | Standalone POM for binding class regeneration |
| `databind/README.md` | Document bootstrap process |

### Files to Update

| File | Changes |
|------|---------|
| `CLAUDE.md` | Reference databind bootstrap in Bootstrap Binding Classes section |

### Files to Regenerate

| File | Changes |
|------|---------|
| `databind/src/main/java/gov/nist/secauto/metaschema/databind/model/metaschema/binding/*.java` | Regenerated binding classes |

### Implementation Approach

1. **Create `pom-bootstrap.xml`**
   - Copy structure from `metaschema-testing/pom-bootstrap.xml`
   - Configure metaschema-maven-plugin for databind module
   - Reference `databind-metaschema/src/main/metaschema-bindings/metaschema-metaschema-bindings.xml`
   - Set output to `target/generated-sources/metaschema`

2. **Create README.md**
   - Document the bootstrap process
   - Explain when regeneration is needed
   - Provide step-by-step commands

3. **Update CLAUDE.md**
   - Add databind to Bootstrap Binding Classes section
   - Reference the README for detailed instructions

4. **Regenerate databind binding classes**
   - Run bootstrap generation: `mvn -f databind/pom-bootstrap.xml generate-sources`
   - Copy generated files to source directory
   - Compare with existing classes to identify any API differences
   - Verify custom interface implementations are preserved (via binding configuration)

### Acceptance Criteria

- [ ] `pom-bootstrap.xml` successfully generates binding classes
- [ ] README documents the complete regeneration process
- [ ] CLAUDE.md references databind bootstrap documentation
- [ ] All databind binding classes regenerated
- [ ] Custom interface implementations preserved via binding configuration
- [ ] Base class extensions preserved via binding configuration
- [ ] Generated Javadoc is complete and properly formatted
- [ ] Null-safety annotations present
- [ ] `mvn checkstyle:check` passes on regenerated files
- [ ] All tests pass: `mvn test`
- [ ] Build succeeds: `mvn clean install -PCI -Prelease`

---

## PR Summary Table

| PR | Description | Files | Risk | Dependencies | Status |
|----|-------------|-------|------|--------------|--------|
| 1 | Code generator improvements + metaschema-testing regeneration | 20 | Low | None | ✅ Completed ([#577](https://github.com/metaschema-framework/metaschema-java/pull/577)) |
| 2 | Collection class override support | ~10 | Low | PR 1 | Pending |
| 3 | Databind bootstrap setup + regeneration | ~35 | Medium | PR 1, PR 2 | Pending |

**Total Estimated PRs**: 3
**Total Estimated Files**: ~65

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
