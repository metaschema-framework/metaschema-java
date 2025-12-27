# Metaschema Java Data Binding and Code Generation

This module provides the data binding framework and code generation capabilities for Metaschema.

## Bootstrap Binding Classes

This module contains two sets of pre-generated binding classes that are checked into source control due to circular dependency constraints with the `metaschema-maven-plugin`.

**Prerequisites:** The model binding classes require the `core/metaschema` git submodule. Initialize submodules before regenerating:

```bash
git submodule update --init --recursive
```

### Config Binding Classes

Package: `gov.nist.secauto.metaschema.databind.config.binding`

These classes are generated from the binding configuration schema (`src/main/metaschema/metaschema-bindings.yaml`) and are used to parse binding configuration files.

**Regeneration:**

```bash
# From the project root - first ensure the project is built
mvn install -DskipTests

# Generate binding classes
mvn -f databind/pom-bootstrap-config.xml generate-sources

# Verify the build still passes
mvn install -PCI -Prelease
```

### Model Binding Classes

Package: `gov.nist.secauto.metaschema.databind.model.metaschema.binding`

These classes are generated from the core Metaschema module (`core/metaschema/schema/metaschema/metaschema-module-metaschema.xml`) and are used for parsing Metaschema module definitions.

**Regeneration:**

```bash
# From the project root - first ensure the project is built
mvn install -DskipTests

# Generate binding classes
mvn -f databind/pom-bootstrap-model.xml generate-sources

# Verify the build still passes
mvn install -PCI -Prelease
```

## Binding Configurations

### Config Bindings

**File:** `src/main/metaschema-bindings/metaschema-config-bindings.xml`

Customizes the generated config binding classes.

### Model Bindings

**File:** `src/main/metaschema-bindings/metaschema-model-bindings.xml`

Customizes the generated model binding classes:

- Maps the Metaschema namespace to `gov.nist.secauto.metaschema.databind.model.metaschema.binding`
- Renames `group-as` to `GroupingAs` (avoids Java keyword conflict)
- Adds interface implementations to constraint classes (e.g., `IModelConstraintsBase`)
- Adds interface implementations to inline definitions for typed collections

## Related Bootstrap POMs

| Bootstrap POM | Generated Package | Source Metaschema |
|--------------|-------------------|-------------------|
| `databind/pom-bootstrap-config.xml` | `...databind.config.binding` | `metaschema-bindings.yaml` |
| `databind/pom-bootstrap-model.xml` | `...databind.model.metaschema.binding` | `metaschema-module-metaschema.xml` |
| `metaschema-testing/pom-bootstrap.xml` | `...model.testing.testsuite` | `unit-tests.yaml` |
