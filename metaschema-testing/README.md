# Metaschema Testing Module

This module provides unit testing support used by other modules in this project.

## Bootstrap Binding Classes

The test suite binding classes in `gov.nist.secauto.metaschema.model.testing.testsuite` are pre-generated and checked into source control. This is necessary because:

1. The `metaschema-maven-plugin` depends on `metaschema-schema-generator`
2. `metaschema-schema-generator` depends on `metaschema-testing` for test utilities
3. Using the plugin during normal build would create a circular dependency

### Regenerating Binding Classes

When the test suite Metaschema module (`src/main/metaschema/unit-tests.yaml`) changes, regenerate the binding classes:

```bash
# From the project root
mvn -f metaschema-testing/pom-bootstrap.xml generate-sources

# Copy generated classes to source
cp -r metaschema-testing/target/generated-sources/metaschema/gov/nist/secauto/metaschema/model/testing/testsuite/* \
      metaschema-testing/src/main/java/gov/nist/secauto/metaschema/model/testing/testsuite/
```

The binding configuration (`src/main/metaschema-bindings/test-suite-bindings.xml`) maps the namespace to the `gov.nist.secauto.metaschema.model.testing.testsuite` package.
