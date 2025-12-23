# PRD: Replace XMLBeans with Metaschema Bindings

## Document Information

| Field | Value |
|-------|-------|
| **PRD ID** | XMLBEANS-001 |
| **Status** | Draft |
| **Created** | 2025-12-21 |
| **Last Updated** | 2025-12-21 |

---

## 1. Overview

### 1.1 Problem Statement

The metaschema-java project currently uses Apache XMLBeans for XML-to-Java object binding in two modules:

1. **databind module** - Parses binding configuration files (`metaschema-binding.xsd`) that customize Java code generation
2. **metaschema-testing module** - Parses test suite definition files (`unit-tests.xsd`) for dynamic JUnit test generation

This creates several issues:
- **Dependency overhead**: XMLBeans adds a significant transitive dependency tree
- **Inconsistent tooling**: The project implements a binding framework but doesn't use it internally
- **Technical debt**: XMLBeans is an older technology with limited modern development
- **Format limitation**: Current configurations only support XML, not JSON or YAML

### 1.2 Goals

1. Remove the Apache XMLBeans dependency from the project entirely
2. Replace XMLBeans usage with Metaschema-based data binding (dogfooding)
3. Enable multi-format support (XML, JSON, YAML) for configuration files
4. Establish patterns for both bootstrap binding (databind) and normal plugin-based binding (testing)

### 1.3 Non-Goals

- Changing the semantic structure of the binding configuration or test suite formats
- Adding new configuration options beyond format support
- Migrating external consumers of XMLBeans-based APIs (internal use only)

### 1.4 Success Metrics

| Metric | Current | Target |
|--------|---------|--------|
| XMLBeans dependency | Present in 3 POMs | Removed from all POMs |
| Supported config formats | XML only | XML, JSON, YAML |
| Build passes | Yes | Yes (no regression) |

---

## 2. Background

### 2.1 Current State

**databind module:**
- Uses `metaschema-binding.xsd` to define binding configuration schema
- XMLBeans generates Java classes at build time into `target/generated-sources/xmlbeans`
- `DefaultBindingConfiguration.java` uses these classes to parse configuration XML
- Configuration specifies: namespace-to-package mappings, class names, interfaces to implement, base classes to extend

**metaschema-testing module:**
- Uses `unit-tests.xsd` (in `core/metaschema/test-suite/`) to define test suite schema
- XMLBeans generates Java classes at build time
- `AbstractTestSuite.java` uses these classes to dynamically generate JUnit tests
- Test suite defines: test collections, scenarios, validation cases, expected results

### 2.2 Technical Context

**Bootstrap Challenge:**
The databind module presents a circular dependency challenge: it needs binding classes to parse configuration, but it's the module that generates binding classes. This will be solved using pre-generated, source-controlled binding classes.

**Module Dependencies:**
```text
metaschema-testing → metaschema-databind → metaschema-core
```

The testing module depends on databind, so it can use normal Maven plugin-based code generation without circular dependency issues.

---

## 3. Requirements

### 3.1 Functional Requirements

#### FR-1: Metaschema Module for Binding Configuration
Create a Metaschema module (in YAML format) defining the binding configuration schema, equivalent to the current `metaschema-binding.xsd`. Place in `databind/src/main/metaschema/`.

#### FR-2: Pre-generated Binding Classes for databind
Generate Java binding classes from the Metaschema module and commit them to source control. These bootstrap classes enable databind to parse its own configuration.

#### FR-3: Metaschema Module for Test Suite
Create a Metaschema module (in YAML format) defining the test suite schema, equivalent to the current `unit-tests.xsd`. Place in `metaschema-testing/src/main/metaschema/`.

#### FR-4: Maven Plugin Code Generation for Testing
Configure the metaschema-maven-plugin in the testing module to generate binding classes at build time from the test suite Metaschema module.

#### FR-5: Update Configuration Parsing Code
Modify `DefaultBindingConfiguration.java` and `AbstractTestSuite.java` to use the new Metaschema-based binding classes instead of XMLBeans-generated classes.

#### FR-6: Remove XMLBeans Infrastructure
Remove XMLBeans plugin configuration, dependencies, and generated source exclusions from all POMs. Delete the XSD files that are being replaced.

### 3.2 Non-Functional Requirements

#### NFR-1: Build Compatibility
All existing builds must continue to pass. No changes to external APIs.

#### NFR-2: Multi-Format Support
The new binding classes must support XML, JSON, and YAML formats for configuration files (inherent capability of Metaschema bindings).

#### NFR-3: Maintainability
The bootstrap binding classes in databind must be clearly documented with regeneration instructions.

---

## 4. Implementation Phases

### Phase 1: databind Module Migration (PR 1)

Migrate the databind module from XMLBeans to Metaschema bindings using the bootstrap approach:
- Create Metaschema module for binding configuration
- Generate and commit binding classes
- Update `DefaultBindingConfiguration.java`
- Remove XMLBeans from databind module only

See [Implementation Plan](./implementation-plan.md) for details.

### Phase 2: metaschema-testing Module Migration (PR 2)

Migrate the testing module and complete XMLBeans removal:
- Create Metaschema module for test suite
- Configure Maven plugin for code generation
- Update `AbstractTestSuite.java`
- Remove XMLBeans from parent POM entirely

See [Implementation Plan](./implementation-plan.md) for details.

---

## 5. Testing Strategy

### 5.1 Test Approach

Each phase must verify:
1. Existing tests continue to pass
2. Configuration/test suite files parse correctly
3. Full CI build succeeds (`mvn install -PCI -Prelease`)

### 5.2 Verification Checklist

- [ ] All unit tests pass
- [ ] Integration tests pass
- [ ] Binding configuration files parse correctly
- [ ] Test suite files parse correctly
- [ ] No XMLBeans imports remain in codebase
- [ ] Full CI build succeeds

---

## 6. Risks and Mitigations

| Risk | Impact | Likelihood | Mitigation |
|------|--------|------------|------------|
| Schema translation errors | High | Low | Careful XSD-to-Metaschema translation with validation |
| Bootstrap class drift | Medium | Low | Document regeneration process; consider CI check |
| Build order issues | Medium | Low | Verify module dependency order in reactor build |

---

## 7. Open Questions

None - all questions resolved during brainstorming.

---

## 8. Related Documents

- [Implementation Plan](./implementation-plan.md)
- Current XSD schemas:
  - `databind/src/main/xsd/metaschema-binding.xsd`
  - `core/metaschema/test-suite/unit-tests.xsd`
