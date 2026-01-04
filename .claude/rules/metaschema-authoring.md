# Metaschema Authoring Conventions

This rule defines repository-specific conventions for authoring Metaschema modules and constraints in metaschema-java.

## Format Selection

**YAML is the preferred format** for new Metaschema modules in this project.

- Use XML only when modifying existing XML modules
- This aligns with the project's YAML-first approach documented in CLAUDE.md

## File Locations

| Content Type | Location |
|--------------|----------|
| Metaschema modules | `{module}/src/main/metaschema/*.yaml` |
| Binding configurations | `{module}/src/main/metaschema-bindings/*.xml` |
| External constraints | `{module}/src/main/metaschema-constraints/*.xml` |
| Test modules | `{module}/src/test/resources/metaschema/*.yaml` |

## IDE Validation

For YAML Metaschema modules, configure IDE validation using the generated JSON schema:

```text
databind-modules/target/generated-resources/schema/json/metaschema-model_schema.json
```

Build with `mvn install` first to generate the schema.

## Namespace Conventions

| Project | Namespace Pattern |
|---------|-------------------|
| Core Metaschema | `http://csrc.nist.gov/ns/oscal/metaschema/1.0` |
| Test modules | `http://csrc.nist.gov/ns/metaschema/test-suite/1.0` |
| Binding config | `https://csrc.nist.gov/ns/metaschema-binding/1.0` |
| Custom modules | `http://example.com/ns/{module-name}` |

## Short Name Conventions

- Use lowercase with hyphens: `my-module-name`
- Keep concise but descriptive
- Must be unique across imported modules

## Testing Metaschema Modules

```bash
# Build and validate all modules
mvn -pl databind install

# Run constraint validation tests
mvn -pl core test -Dtest=*Constraint*

# Run specific validation test
mvn -pl databind test -Dtest=ConstraintValidationTest
```

## Bootstrap Binding Classes

When Metaschema modules are used for code generation but create circular dependencies:

1. Create a `pom-bootstrap.xml` for standalone generation
2. Pre-generate binding classes and check into source control
3. Document regeneration process in module README.md

See `metaschema-testing/` for an example of this pattern.

### Generated Binding Class Locations

The following packages contain binding classes derived from Metaschema modules:

| Package | Source Metaschema | Bootstrap POM |
|---------|------------------|---------------|
| `databind/.../config/binding/` | `databind/src/main/metaschema/metaschema-bindings.yaml` | `databind/pom-bootstrap-config.xml` |
| `databind/.../model/metaschema/binding/` | `core/metaschema/schema/metaschema/metaschema-module-metaschema.xml` | `databind/pom-bootstrap-model.xml` |
| `metaschema-testing/.../testsuite/` | `metaschema-testing/src/main/metaschema/unit-tests.yaml` | `metaschema-testing/pom-bootstrap.xml` |

### CRITICAL: Never Manually Edit Generated Binding Classes

**All changes to generated binding classes MUST be driven through the source Metaschema module.**

When you need to modify a generated binding class:

1. **Identify the source Metaschema** - Find the `.yaml` or `.xml` module that generates the class
2. **Modify the Metaschema module** - Update the `.yaml` or `.xml` module definition
3. **Build the project first** - Run `mvn install` to ensure the code generator is up to date
4. **Regenerate the binding classes** - Run the bootstrap POM: `mvn -f {module}/pom-bootstrap.xml generate-sources`
5. **Verify the changes** - Check that the regenerated classes contain the expected changes

**Why this matters:**
- Manual edits will be lost when classes are regenerated
- Manual edits may diverge from the Metaschema schema definition
- The Metaschema module is the authoritative source for the data model

**Red flags that you're about to make a mistake:**
- Opening a file in `.../config/binding/`, `.../model/metaschema/binding/`, or `.../testsuite/` for editing
- Adding fields, methods, or annotations directly to these classes
- Copying code patterns from these files to create new bindings manually

## Code Generation

After modifying Metaschema modules that generate Java bindings:

```bash
# Regenerate binding classes
mvn -pl {module} generate-sources

# Verify generated code compiles
mvn -pl {module} compile

# Run full verification
mvn clean install -PCI -Prelease
```

## Maintaining Skill Documentation

When modifying Metapath functions or Metaschema features, update the corresponding skill documentation:

| Change Type | Update Required |
|-------------|-----------------|
| New Metapath function | Add to `.claude/skills/metapath-expressions.md` function tables |
| New function variant | Document in skill if significantly different usage |
| New constraint type | Add to `.claude/skills/metaschema-constraints-authoring.md` |
| New module feature | Add to `.claude/skills/metaschema-module-authoring.md` |
| New exception type | Add to `.claude/skills/metaschema-java-library.md` exception hierarchy |
| New key interface | Add to `.claude/skills/metaschema-java-library.md` |

**Function location:** `core/src/main/java/gov/nist/secauto/metaschema/core/metapath/function/library/`

## Related Skills

For detailed syntax and patterns, use these skills:

- `metaschema-module-authoring` - Module structure, definitions, format-specific features
- `metaschema-constraints-authoring` - Constraint types, validation patterns
- `metapath-expressions` - Path syntax, operators, functions based on XPath 3.1
- `metaschema-java-library` - Key interfaces, exception hierarchy, Metapath evaluation
