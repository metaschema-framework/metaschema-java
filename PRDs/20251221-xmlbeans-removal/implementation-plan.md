# Implementation Plan: Replace XMLBeans with Metaschema Bindings

This document details each PR for removing XMLBeans and replacing it with Metaschema-based bindings.

---

## Prerequisites

- Familiarity with Metaschema module syntax
- Understanding of the metaschema-maven-plugin configuration
- Access to build and test the full project

## Reference Materials

**Metaschema Module JSON Schema** (for YAML module authoring):
```text
databind-modules/target/generated-resources/schema/json/metaschema-model_schema.json
```

Use this schema for IDE validation when authoring the YAML Metaschema modules.

---

## Test-Driven Development Requirement

**All functional code changes must follow TDD:**

1. Write or update tests first to capture expected behavior
2. Verify tests fail with existing implementation (confirms tests are testing new behavior)
3. Make the code changes
4. Verify tests pass after changes

---

## Phase 1: databind Module Migration

### PR 1: Replace XMLBeans with Metaschema Bindings in databind Module

| Attribute | Value |
|-----------|-------|
| **Files Changed** | 14 |
| **Risk Level** | Medium |
| **Dependencies** | None |
| **Target Branch** | develop |
| **Status** | Complete |
| **Pull Request** | [#566](https://github.com/metaschema-framework/metaschema-java/pull/566) |

#### Files to Create

| File | Description |
|------|-------------|
| `databind/src/main/metaschema/metaschema-bindings.yaml` | Metaschema module defining binding configuration schema (YAML format) |
| `databind/src/main/java/dev/metaschema/databind/config/binding/*.java` | Pre-generated binding classes (multiple files) |
| `databind/src/main/java/dev/metaschema/databind/config/binding/package-info.java` | Package documentation explaining bootstrap approach |

#### Files to Modify

| File | Changes |
|------|---------|
| `databind/pom.xml` | Remove XMLBeans plugin and dependency; keep build-helper if needed |
| `databind/src/main/java/dev/metaschema/databind/codegen/config/DefaultBindingConfiguration.java` | Replace XMLBeans imports/usage with Metaschema binding classes |
| `databind/src/main/java/module-info.java` | Update module exports/requires (remove xmlbeans) |
| `databind/spotbugs-exclude.xml` | Update exclusions for new package instead of xmlbeans |

#### Files to Delete

| File | Reason |
|------|--------|
| `databind/src/main/xsd/metaschema-binding.xsd` | Replaced by Metaschema module |
| `databind/src/schema/xmlconfig.xml` | XMLBeans configuration no longer needed |
| `databind/src/main/java/dev/metaschema/databind/codegen/xmlbeans/package-info.java` | XMLBeans package no longer needed |
| `databind/src/main/java/org/apache/xmlbeans/metadata/system/metaschema/codegen/package-info.java` | XMLBeans metadata package no longer needed |

#### Implementation Approach

1. **Create Metaschema module**: Translate `metaschema-binding.xsd` to a Metaschema module (YAML format)
   - Map XSD complex types to Metaschema assemblies/fields
   - Preserve all element names and structure
   - Define appropriate data types

2. **Generate binding classes**: Use metaschema-cli or maven plugin to generate Java classes
   - Generate to a temporary location
   - Copy generated classes to `databind/src/main/java/.../config/binding/`
   - Add package-info.java documenting the bootstrap process

3. **Update DefaultBindingConfiguration.java**:
   - Replace XMLBeans imports with new binding class imports
   - Replace `MetaschemaBindingsDocument.Factory.parse()` with Metaschema deserializer
   - Update all method calls to use new binding class APIs
   - Maintain same public API for `DefaultBindingConfiguration`

4. **Update POM and module-info**:
   - Remove XMLBeans plugin configuration
   - Remove XMLBeans dependency
   - Remove generated-sources exclusions for xmlbeans
   - Update module-info.java requires/exports

5. **Clean up XMLBeans artifacts**:
   - Delete XSD file
   - Delete xmlconfig.xml
   - Delete XMLBeans package-info files

#### Acceptance Criteria

- [x] Metaschema module created at `databind/src/main/metaschema/metaschema-bindings.yaml`
- [x] Binding classes generated and committed to source control
- [x] `DefaultBindingConfiguration.java` uses new binding classes
- [x] No XMLBeans imports in databind module
- [x] XMLBeans dependency removed from `databind/pom.xml`
- [x] XSD file deleted
- [x] Existing binding configuration files parse correctly
- [x] All databind tests pass
- [x] Build succeeds: `mvn -pl databind install`

---

## Phase 2: metaschema-testing Module Migration and Cleanup

### PR 2: Replace XMLBeans with Metaschema Bindings in metaschema-testing Module

| Attribute | Value |
|-----------|-------|
| **Files Changed** | 15 |
| **Risk Level** | Low |
| **Dependencies** | PR 1 |
| **Target Branch** | develop |
| **Status** | Complete |
| **Pull Request** | [#567](https://github.com/metaschema-framework/metaschema-java/pull/567) |

#### Files to Create

| File | Description |
|------|-------------|
| `metaschema-testing/src/main/metaschema/unit-tests.yaml` | Metaschema module defining test suite schema (YAML format) |

#### Files to Modify

| File | Changes |
|------|---------|
| `metaschema-testing/pom.xml` | Remove XMLBeans plugin/dependency; add metaschema-maven-plugin |
| `metaschema-testing/src/main/java/dev/metaschema/model/testing/AbstractTestSuite.java` | Replace XMLBeans usage with Metaschema binding classes |
| `pom.xml` (parent) | Remove XMLBeans from dependency management |

#### Files to Delete

| File | Reason |
|------|--------|
| `metaschema-testing/src/schema/xmlconfig.xml` | XMLBeans configuration no longer needed |
| `metaschema-testing/src/main/java/dev/metaschema/model/testing/xml/xmlbeans/` | Entire XMLBeans handler package |

**Note**: `core/metaschema/test-suite/unit-tests.xsd` is in the metaschema specification submodule (separate repository) and was not deleted.

#### Implementation Approach

1. **Create Metaschema module**: Translate `unit-tests.xsd` to a Metaschema module (YAML format)
   - Map XSD elements to Metaschema assemblies/fields
   - Preserve element names and structure
   - Handle enumerations (GenerationResultType, ValidationResultType, FormatType, MatchResultType)

2. **Configure Maven plugin**:
   - Add metaschema-maven-plugin to `metaschema-testing/pom.xml`
   - Configure to generate from `src/main/metaschema/unit-tests.yaml`
   - Generate classes to standard `target/generated-sources/metaschema` location

3. **Update AbstractTestSuite.java**:
   - Replace XMLBeans imports with generated binding class imports
   - Replace `TestSuiteDocument.Factory.parse()` with Metaschema deserializer
   - Update iteration over collections (XMLBeans uses `getXxxList()`, Metaschema may differ)
   - Maintain same test generation logic

4. **Delete XMLBeans handler classes**:
   - Remove `FormatType.java`, `GenerationResultType.java`, `ValidationResultType.java`
   - Remove `package-info.java` in xmlbeans handler package

5. **Clean up parent POM**:
   - Remove XMLBeans from `<dependencyManagement>`
   - Remove XMLBeans plugin from `<pluginManagement>` if present

#### Acceptance Criteria

- [x] Metaschema module created at `metaschema-testing/src/main/metaschema/unit-tests.yaml`
- [x] metaschema-maven-plugin configured in testing POM
- [x] `AbstractTestSuite.java` uses generated binding classes
- [x] No XMLBeans imports in metaschema-testing module
- [x] XMLBeans dependency removed from `metaschema-testing/pom.xml`
- [x] XMLBeans removed from parent `pom.xml`
- [x] XSD file in `core/metaschema/test-suite/` is in submodule (separate repo), no longer referenced
- [x] XMLBeans handler package deleted
- [x] Existing test suite files parse correctly
- [x] All metaschema-testing tests pass
- [x] All dependent module tests pass
- [x] Full CI build succeeds: `mvn install -PCI -Prelease`
- [x] No XMLBeans references remain in codebase (verify with grep)

---

## PR Summary Table

| PR | Description | Files | Risk | Dependencies | Status |
|----|-------------|-------|------|--------------|--------|
| 1 | databind module: XMLBeans → Metaschema bindings (bootstrap) | 14 | Medium | None | Complete ([#566](https://github.com/metaschema-framework/metaschema-java/pull/566)) |
| 2 | metaschema-testing module + parent POM cleanup | 15 | Low | PR 1 | Complete ([#567](https://github.com/metaschema-framework/metaschema-java/pull/567)) |

**Total Estimated PRs**: 2
**Total Estimated Files**: ~30-40

---

## Bootstrap Class Regeneration

When the binding configuration schema changes, the bootstrap classes in databind must be regenerated:

```bash
# 1. Update the Metaschema module
#    databind/src/main/metaschema/metaschema-bindings.yaml

# 2. Generate new classes (from project root, after building)
java -jar metaschema-cli/target/metaschema-cli-*-metaschema-cli.jar \
    generate-java \
    --output-dir /tmp/binding-classes \
    databind/src/main/metaschema/metaschema-bindings.yaml

# 3. Copy generated classes to source tree
cp -r /tmp/binding-classes/* \
    databind/src/main/java/

# 4. Verify build still works
mvn -pl databind clean install
```

This process should be documented in the package-info.java or a README in the binding package.
