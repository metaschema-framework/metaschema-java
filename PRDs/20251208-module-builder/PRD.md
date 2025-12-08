# IModule Builder Design

## Overview

Add an `IModuleBuilder` to the existing test builder pattern in `core/src/test/java/gov/nist/secauto/metaschema/core/testing/model/`. This enables programmatic construction of `IModule` instances for testing, reducing dependency on XMLBeans-based `ModuleLoader`.

## Goals

1. Create a fluent builder API for constructing mock `IModule` instances
2. Support recursive assembly definitions (self-referencing)
3. Integrate with existing `IAssemblyBuilder`, `IFieldBuilder`, `IFlagBuilder`
4. Enable migration of 4 test cases away from XML-based module loading

## Design

### New Interfaces and Classes

```
IModuleBuilder (interface)
    └── ModuleBuilder (implementation)

IModelReference (marker interface) - represents a lazy reference
    ├── AssemblyReference extends AbstractMetaschemaBuilder, implements IModelBuilder
    └── FieldReference extends AbstractMetaschemaBuilder, implements IModelBuilder
```

### IModuleBuilder Interface

```java
public interface IModuleBuilder {
    // Static factory
    static IModuleBuilder builder();

    // Reset
    IModuleBuilder reset();

    // Module metadata
    IModuleBuilder namespace(@NonNull URI namespace);
    IModuleBuilder namespace(@NonNull String namespace);
    IModuleBuilder shortName(@NonNull String shortName);
    IModuleBuilder version(@NonNull String version);
    IModuleBuilder name(@NonNull MarkupLine name);
    IModuleBuilder source(@NonNull ISource source);

    // Add definitions (accumulative)
    IModuleBuilder assembly(@NonNull IAssemblyBuilder assembly);
    IModuleBuilder field(@NonNull IFieldBuilder field);
    IModuleBuilder flag(@NonNull IFlagBuilder flag);

    // Reference factories for model instances
    IModelBuilder<?> assemblyRef(@NonNull String name);
    IModelBuilder<?> fieldRef(@NonNull String name);

    // Build
    IModule toModule();
}
```

### Reference Resolution

References allow defining recursive or cross-referencing structures:

```java
IModule module = IModuleBuilder.builder()
    .namespace("http://example.com/ns")
    .shortName("test")
    .version("1.0")
    .source(ISource.externalSource("test"))
    .assembly(IAssemblyBuilder.builder()
        .name("recursive-assembly")
        .rootName("recursive-assembly")
        .modelInstances(List.of(
            moduleBuilder.assemblyRef("recursive-assembly")  // self-reference
        )))
    .toModule();
```

**Resolution order in `toModule()`:**

1. Validate all builders have required data
2. Create mock `IModule` with metadata
3. Build all flag definitions (no dependencies)
4. Build all field definitions (may reference flags)
5. Build all assembly definitions:
   - Direct nested builders → build inline
   - References → resolve by name from already-built definitions
6. Wire up module's definition collections and lookup methods

### Changes to Existing Builders

**AbstractMetaschemaBuilder:**
- Add `applyDefinition(IDefinition, IModule)` overload to set `getContainingModule()`

**IAssemblyBuilder, IFieldBuilder, IFlagBuilder:**
- Add `toDefinition(IModule)` overload for module-aware definition creation
- Existing `toDefinition()` remains for standalone usage

**IModuleMockFactory:**
- Add `default IModuleBuilder module()` convenience method

### IModule Methods to Mock

Based on usage analysis, the following `IModule` methods need mocking:

| Method | Purpose |
|--------|---------|
| `getLocation()` | Module source URI |
| `getLocationHint()` | String hint for location |
| `getSource()` | ISource information |
| `getName()` | Formal MarkupLine name |
| `getVersion()` | Version string |
| `getShortName()` | Unique identifier |
| `getXmlNamespace()` | XML namespace URI |
| `getJsonBaseUri()` | JSON base URI |
| `getQName()` | Qualified name |
| `getAssemblyDefinitions()` | Local assembly collection |
| `getAssemblyDefinitionByName(Integer)` | Lookup by name index |
| `getFieldDefinitions()` | Local field collection |
| `getFieldDefinitionByName(Integer)` | Lookup by name index |
| `getFlagDefinitions()` | Local flag collection |
| `getFlagDefinitionByName(IEnhancedQName)` | Lookup by qname |
| `getExportedAssemblyDefinitions()` | Returns same as local |
| `getExportedFieldDefinitions()` | Returns same as local |
| `getExportedFlagDefinitions()` | Returns same as local |
| `getExportedRootAssemblyDefinitions()` | Root assemblies |
| `getRootAssemblyDefinitions()` | Local root assemblies |
| `getImportedModules()` | Empty list (no import support) |
| `getAssemblyAndFieldDefinitions()` | Combined list |
| `getModuleStaticContext()` | StaticContext for Metapath |

**Simplifications:**
- All definitions are "exported" (no scope distinction)
- No imported modules support
- Scoped lookups (`getScopedXxxByName`) return same as local lookups

## Test Case Migrations

### 1. ExternalConstraintsModulePostProcessorTest

**Current:** Loads `issue184-metaschema.xml` and `issue184-constraints.xml`

**After:** Build module with assembly "a", apply constraints programmatically

### 2. RecursionCollectingNodeItemVisitorTest

**Current:** Loads `metaschema-module-metaschema.xml`

**After:** Build module with self-referencing assembly to test recursion detection

### 3. AbstractRecursionPreventingNodeItemVisitorTest

**Current:** Loads `metaschema-module-metaschema.xml`

**After:** Build module with recursive assembly to test visitation

### 4. MermaidErDiagramGeneratorTest

**Current:** Loads OSCAL metaschema from URL

**After:** Build module with root assembly, child fields/assemblies for diagram generation

## File Changes

### New Files
- `IModuleBuilder.java` - Builder interface
- `ModuleBuilder.java` - Builder implementation
- `AssemblyReference.java` - Lazy assembly reference
- `FieldReference.java` - Lazy field reference
- `IModelReference.java` - Marker interface for references

### Modified Files
- `AbstractMetaschemaBuilder.java` - Add module-aware `applyDefinition`
- `IAssemblyBuilder.java` - Add `toDefinition(IModule)`
- `AssemblyBuilder.java` - Implement `toDefinition(IModule)`
- `IFieldBuilder.java` - Add `toDefinition(IModule)`
- `FieldBuilder.java` - Implement `toDefinition(IModule)`
- `IFlagBuilder.java` - Add `toDefinition(IModule)`
- `FlagBuilder.java` - Implement `toDefinition(IModule)`
- `IModuleMockFactory.java` - Add `module()` method

### Test Migrations
- `ExternalConstraintsModulePostProcessorTest.java`
- `RecursionCollectingNodeItemVisitorTest.java`
- `AbstractRecursionPreventingNodeItemVisitorTest.java`
- `MermaidErDiagramGeneratorTest.java`

## Usage Example

```java
MockedModelTestSupport mocking = new MockedModelTestSupport();
ISource source = ISource.externalSource("https://example.com/module");

IModule module = mocking.module()
    .namespace("http://example.com/ns")
    .shortName("example")
    .version("1.0")
    .source(source)
    // Global flag definition
    .flag(mocking.flag()
        .name("id")
        .dataTypeAdapter(MetaschemaDataTypeProvider.TOKEN))
    // Field with flag
    .field(mocking.field()
        .name("title")
        .dataTypeAdapter(MetaschemaDataTypeProvider.STRING))
    // Root assembly with recursive reference
    .assembly(mocking.assembly()
        .name("root")
        .rootName("root")
        .flags(List.of(mocking.flag().name("uuid")))
        .modelInstances(List.of(
            mocking.field().name("description"),
            mocking.assemblyRef("root")  // recursive
        )))
    .toModule();

// Use with INodeItemFactory
IModuleNodeItem moduleItem = INodeItemFactory.instance().newModuleNodeItem(module);
```
