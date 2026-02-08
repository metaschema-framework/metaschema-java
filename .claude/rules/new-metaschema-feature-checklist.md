# New Metaschema Feature Checklist

When adding a new Metaschema model feature (e.g., a new instance type), systematically evaluate **all** areas below. Missing any area risks incomplete support.

## Mandatory Areas

### 1. Core Model

- [ ] Create interface extending appropriate base (`IModelInstanceAbsolute`, etc.)
- [ ] Add `ModelType` enum value if applicable
- [ ] Update container model interfaces and implementations (`IContainerModelAssemblySupport`, `DefaultContainerModelAssemblySupport`, model builder)
- [ ] Update visitor pattern: `IModelElementVisitor`, `AbstractModelElementVisitor`, and ALL implementing visitors
- [ ] Write core model tests

### 2. Databind Binding Layer

- [ ] Create annotation in `databind/model/annotations/`
- [ ] Create binding interface in `databind/model/` and implementation in `databind/model/impl/`
- [ ] Create Metaschema binding implementation in `databind/model/metaschema/impl/`
- [ ] Update `AssemblyModelGenerator` (and `ChoiceModelGenerator` if applicable)
- [ ] Update class introspection (`DefinitionAssembly` or similar) to scan for new annotation

### 3. Databind I/O (if feature handles data)

- [ ] Update `MetaschemaXmlReader` and `MetaschemaXmlWriter`
- [ ] Update `MetaschemaJsonReader` and `MetaschemaJsonWriter`
- [ ] Create format-specific wrapper classes if needed
- [ ] Write round-trip tests

### 4. Code Generation

- [ ] Create type info class in `databind/codegen/typeinfo/` following existing patterns
- [ ] Update `AssemblyDefinitionTypeInfoImpl` to process the new instance type
- [ ] Update `ITypeResolver` if definition resolution is needed
- [ ] Write codegen tests (compile test module, verify generated annotations/fields)

### 5. Schema Generation

- [ ] Update XML Schema generator for XSD output
- [ ] Update JSON Schema generator for JSON Schema output
- [ ] Write schema generation tests for both formats

### 6. Constraint Processing

- [ ] Update `ConstraintComposingVisitor` visitor method
- [ ] Determine if constraints can target the feature; if not, use `illegalTargetError()`

## Conditional Areas

| Area | When Needed | Key Files |
|------|-------------|-----------|
| Metapath/Query | Feature is queryable or affects traversal | `core/.../metapath/` |
| Maven Plugin | New build configuration needed | `metaschema-maven-plugin/` |
| CLI | New commands or output options | `metaschema-cli/`, `cli-processor/` |
| Testing Module | Test infrastructure changes | `metaschema-testing/`, `unit-tests.yaml` |

## Verification

```bash
mvn clean install -PCI -Prelease
```
