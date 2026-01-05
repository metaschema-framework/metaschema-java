# Implementation Plan: IModule Builder

## PR 1: Core Module Builder Infrastructure

**Files to create:**
- `core/src/test/java/dev/metaschema/core/testing/model/IModuleBuilder.java`
- `core/src/test/java/dev/metaschema/core/testing/model/ModuleBuilder.java`

**Files to modify:**
- `core/src/test/java/dev/metaschema/core/testing/model/IModuleMockFactory.java` - Add `module()` method

**Acceptance Criteria:**
- [ ] `IModuleBuilder` interface with metadata methods (namespace, shortName, version, source)
- [ ] `ModuleBuilder` implementation creates mock `IModule`
- [ ] Basic module metadata methods are mocked (getXmlNamespace, getShortName, getVersion, etc.)
- [ ] `IModuleMockFactory.module()` returns new builder
- [ ] Unit test demonstrating basic module creation
- [ ] `mvn -pl core test` passes

---

## PR 2: Definition Builder Integration

**Files to modify:**
- `core/src/test/java/dev/metaschema/core/testing/model/AbstractMetaschemaBuilder.java`
- `core/src/test/java/dev/metaschema/core/testing/model/IFlagBuilder.java`
- `core/src/test/java/dev/metaschema/core/testing/model/FlagBuilder.java`
- `core/src/test/java/dev/metaschema/core/testing/model/IFieldBuilder.java`
- `core/src/test/java/dev/metaschema/core/testing/model/FieldBuilder.java`
- `core/src/test/java/dev/metaschema/core/testing/model/IAssemblyBuilder.java`
- `core/src/test/java/dev/metaschema/core/testing/model/AssemblyBuilder.java`
- `core/src/test/java/dev/metaschema/core/testing/model/ModuleBuilder.java`

**Acceptance Criteria:**
- [ ] `AbstractMetaschemaBuilder.applyDefinition(IDefinition, IModule)` sets `getContainingModule()`
- [ ] `IFlagBuilder.toDefinition(IModule)` creates flag with module reference
- [ ] `IFieldBuilder.toDefinition(IModule)` creates field with module reference
- [ ] `IAssemblyBuilder.toDefinition(IModule)` creates assembly with module reference
- [ ] `IModuleBuilder.flag()`, `.field()`, `.assembly()` accumulate builders
- [ ] `toModule()` builds all definitions with proper module wiring
- [ ] Module's `getAssemblyDefinitions()`, `getFieldDefinitions()`, `getFlagDefinitions()` return built definitions
- [ ] Module's lookup methods (`getAssemblyDefinitionByName`, etc.) work correctly
- [ ] Unit test demonstrating module with flag, field, and assembly definitions
- [ ] `mvn -pl core test` passes

---

## PR 3: Reference Support for Recursive Assemblies

**Files to create:**
- `core/src/test/java/dev/metaschema/core/testing/model/IModelReference.java`
- `core/src/test/java/dev/metaschema/core/testing/model/AssemblyReference.java`
- `core/src/test/java/dev/metaschema/core/testing/model/FieldReference.java`

**Files to modify:**
- `core/src/test/java/dev/metaschema/core/testing/model/IModuleBuilder.java`
- `core/src/test/java/dev/metaschema/core/testing/model/ModuleBuilder.java`

**Acceptance Criteria:**
- [ ] `IModelReference` marker interface for lazy references
- [ ] `AssemblyReference` stores referenced name, implements `IModelBuilder`
- [ ] `FieldReference` stores referenced name, implements `IModelBuilder`
- [ ] `IModuleBuilder.assemblyRef(String)` creates `AssemblyReference`
- [ ] `IModuleBuilder.fieldRef(String)` creates `FieldReference`
- [ ] `ModuleBuilder.toModule()` resolves references to built definitions
- [ ] Unit test demonstrating recursive assembly (self-referencing)
- [ ] Unit test demonstrating cross-reference between assemblies
- [ ] `mvn -pl core test` passes

---

## PR 4: Export and Root Assembly Support

**Files to modify:**
- `core/src/test/java/dev/metaschema/core/testing/model/ModuleBuilder.java`

**Acceptance Criteria:**
- [ ] `getExportedAssemblyDefinitions()` returns all assemblies (same as local)
- [ ] `getExportedFieldDefinitions()` returns all fields (same as local)
- [ ] `getExportedFlagDefinitions()` returns all flags (same as local)
- [ ] `getRootAssemblyDefinitions()` returns assemblies with rootName set
- [ ] `getExportedRootAssemblyDefinitions()` returns same as local roots
- [ ] `getExportedRootAssemblyDefinitionByName()` lookup works
- [ ] `getAssemblyAndFieldDefinitions()` returns combined list
- [ ] `getScopedXxxByName()` methods return same as local (no imports)
- [ ] `getImportedModules()` returns empty list
- [ ] `getModuleStaticContext()` returns valid StaticContext
- [ ] Unit test verifying export methods
- [ ] `mvn -pl core test` passes

---

## PR 5: Migrate ExternalConstraintsModulePostProcessorTest

**Files to modify:**
- `core/src/test/java/dev/metaschema/core/model/constraint/ExternalConstraintsModulePostProcessorTest.java`

**Acceptance Criteria:**
- [ ] Test uses `IModuleBuilder` instead of `ModuleLoader`
- [ ] Module built with assembly "a" in namespace "http://csrc.nist.gov/ns/test/metaschema/constraint-targeting-test"
- [ ] External constraints still loaded from XML (or built programmatically)
- [ ] Constraint application works correctly
- [ ] Test assertion passes (1 constraint on assembly)
- [ ] `mvn -pl core test -Dtest=ExternalConstraintsModulePostProcessorTest` passes

---

## PR 6: Migrate Recursion Visitor Tests

**Files to modify:**
- `core/src/test/java/dev/metaschema/core/metapath/item/node/RecursionCollectingNodeItemVisitorTest.java`
- `core/src/test/java/dev/metaschema/core/metapath/item/node/AbstractRecursionPreventingNodeItemVisitorTest.java`

**Acceptance Criteria:**
- [ ] Both tests use `IModuleBuilder` instead of `ModuleLoader`
- [ ] Module built with self-referencing assembly structure
- [ ] `RecursionCollectingNodeItemVisitor` correctly identifies recursive assemblies
- [ ] `AbstractRecursionPreventingNodeItemVisitor` handles recursion without infinite loop
- [ ] `mvn -pl core test -Dtest=RecursionCollectingNodeItemVisitorTest` passes
- [ ] `mvn -pl core test -Dtest=AbstractRecursionPreventingNodeItemVisitorTest` passes

---

## PR 7: Migrate MermaidErDiagramGeneratorTest

**Files to modify:**
- `core/src/test/java/dev/metaschema/core/util/MermaidErDiagramGeneratorTest.java`

**Acceptance Criteria:**
- [ ] Test uses `IModuleBuilder` instead of `ModuleLoader`
- [ ] Module built with root assembly containing child fields and assemblies
- [ ] Diagram generation produces valid Mermaid ER diagram output
- [ ] `mvn -pl core test -Dtest=MermaidErDiagramGeneratorTest` passes

---

## Verification

After all PRs are merged:
- [ ] `mvn clean install -PCI -Prelease` passes
- [ ] All 4 migrated tests no longer depend on `ModuleLoader`
- [ ] No new test files reference XMLBeans classes
