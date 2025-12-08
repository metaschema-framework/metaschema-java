# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build and install all modules
mvn install

# Replicate CI/CD build (recommended before pushing)
mvn install -PCI -Prelease

# Build with release profile (includes additional checks)
mvn -Prelease install

# Run tests only
mvn test

# Run a single test class
mvn -pl core test -Dtest=FnCountTest

# Run a single test method
mvn -pl core test -Dtest=FnCountTest#testCount

# Skip tests during build
mvn install -DskipTests

# Build specific module (e.g., core)
mvn -pl core install

# Build module with dependencies
mvn -pl core -am install

# Generate site documentation
mvn site site:stage

# Check license headers in source files
mvn license:check

# Add license headers to source files
mvn license:format

# Check source code formatting
mvn formatter:validate

# Auto-format source code
mvn formatter:format

# Check for Checkstyle issues (e.g., missing Javadoc)
mvn checkstyle:check
```

## Static Analysis

The project uses multiple static analysis tools configured in the parent POM (configurations in [oss-maven](https://github.com/metaschema-framework/oss-maven)):

- **Checkstyle**: Code style enforcement ([checkstyle.xml](https://github.com/metaschema-framework/oss-maven/blob/main/oss-build-support/src/main/resources/checkstyle/checkstyle.xml))
- **PMD**: Source code analysis, fails on priority 2+ violations ([custom.xml](https://github.com/metaschema-framework/oss-maven/blob/main/oss-build-support/src/main/resources/pmd/category/java/custom.xml))
- **SpotBugs**: Bug pattern detection (uses `spotbugs-exclude.xml` for exclusions)
- **Jacoco**: Code coverage analysis (target: 60% coverage)

## Project Architecture

### Module Dependency Hierarchy

```
metaschema-framework (parent)
├── core (metaschema-core) - Core API, Metapath expression language
├── databind (metaschema-databind) - Data binding and code generation
├── schemagen (metaschema-schema-generator) - XML/JSON schema generation
├── databind-metaschema - Metaschema binding modules
├── metaschema-maven-plugin - Maven plugin for code/schema generation
├── metaschema-testing - Testing utilities
├── cli-processor - CLI framework
└── metaschema-cli - Command-line interface
```

### Core Module Structure (gov.nist.secauto.metaschema.core)

- **model/** - Metaschema model interfaces (`IModule`, `IAssemblyDefinition`, `IFieldDefinition`, `IFlagDefinition`)
- **metapath/** - Metapath query language implementation
  - **antlr/** - ANTLR4-generated parser (from `src/main/antlr4`)
  - **cst/** - Concrete syntax tree expressions
  - **function/** - Function library implementation
  - **item/** - Metapath item types (node items, atomic items)
- **datatype/** - Data type adapters for Metaschema types
- **mdm/** - Metaschema Document Model node items

### Metapath and XPath 3.1

Metapath is an implementation of XPath 3.1. The [XPath 3.1 specification](https://www.w3.org/TR/xpath-31/) and [XPath Functions 3.1](https://www.w3.org/TR/xpath-functions-31/) should be used as the authoritative behavioral reference when:
- Implementing new Metapath functions
- Fixing bugs in existing function implementations
- Understanding expected error handling behavior

**When behavior differs from the spec**: If you discover that the current Metapath implementation differs from the XPath 3.1 specification, raise this as a clarification to the user before making changes. Consider:
- Whether the deviation is intentional (Metaschema-specific extension)
- The risk of changing behavior that existing code may depend on
- Whether tests need to be added to verify spec-compliant behavior

### Generated Code

Code generation occurs during Maven build:
- **XMLBeans**: Generated from XSD schemas in `core/metaschema/schema/xml`
- **ANTLR4**: Metapath parser from `core/src/main/antlr4`

Generated sources are placed in `target/generated-sources/` and excluded from style checks.

### Key Interfaces

- `IModule` - Represents a Metaschema module with definitions
- `IModuleLoader` - Loads Metaschema modules from various sources
- `IDataTypeAdapter` - Adapts between Java types and Metaschema data types
- `StaticContext` / `DynamicContext` - Metapath evaluation contexts

## Testing

- Tests use JUnit 5 with parallel execution enabled (Maven Surefire plugin)
- Test utilities are in the `metaschema-testing` module
- Integration tests for the Maven plugin use the maven-invoker-plugin
- All PRs require passing CI checks before merge
- 100% of unit tests must pass before pushing code (BLOCKING)

### Test-Driven Development (TDD)

All code changes should follow TDD principles:

1. **Write tests first**: Before implementing new functionality, write tests that capture the expected behavior
2. **Watch tests fail**: Verify the tests fail with the current implementation (proving the tests are meaningful)
3. **Write minimal code**: Implement just enough code to make the tests pass
4. **Refactor**: Clean up the code while keeping tests green

For existing code without tests:
- When modifying existing files, add tests as you touch the code
- This ensures behavioral equivalence before and after changes

```bash
# Run a single test class
mvn -pl core test -Dtest=FnCountTest

# Run a single test method
mvn -pl core test -Dtest=FnCountTest#testCount
```

## Code Style

- Java 11 target
- Uses SpotBugs annotations (`@NonNull`, `@Nullable`) for null safety
- Package structure follows `gov.nist.secauto.metaschema.*` convention

## Git Workflow

- Repository: https://github.com/metaschema-framework/metaschema-java
- **All PRs MUST be created from a personal fork** (BLOCKING - required by CONTRIBUTING.md)
- **All PRs MUST target the `develop` branch** (BLOCKING - required by CONTRIBUTING.md)
- Clone with submodules: `git clone --recurse-submodules`
- All changes require PR review (CODEOWNERS enforced)
- Squash non-relevant commits before submitting PR (BLOCKING)

## Databind XML Parsing

Key entry points for XML deserialization (see `databind/parsing-api-notes.md`):
- `DefaultXmlDeserializer.deserialize(Reader)` - main entry point
- `AbstractClassBinding.readInternal()` - reads flags then body
- `DefaultAssemblyClassBinding.readBody()` - parses assembly model contents
- `DefaultFieldClassBinding` - handles field value parsing

## PRD Workflow

For larger initiatives requiring multiple PRs, use a structured PRD (Product Requirements Document) approach.

### PRD Structure

PRDs are stored in `PRDs/<YYYYMMDD>-<name>/` with:
- `PRD.md` - Main requirements document (goals, requirements, success metrics)
- `implementation-plan.md` - Detailed PR breakdown with acceptance criteria
- Supporting analysis documents as needed

### PRD Process

1. **Planning Phase**: Create PRD folder with requirements and implementation plan
2. **PR Size Limits**: Target ≤50 files per PR, maximum 100 files per PR
3. **TDD Requirement**: All functional code changes require test-driven development:
   - Write/update tests first to capture expected behavior
   - Verify tests pass with existing implementation
   - Make code changes
   - Verify tests still pass after changes
4. **Verification**: After each PR, run `mvn clean install -PCI -Prelease`
5. **Completion Tracking**: Mark completed items in `implementation-plan.md` acceptance criteria

### Active PRD Management

**When starting development on a PRD:**
- Add entry to "Active PRDs" section below with status "In Progress"

**When completing a PRD:**
- Update status to "Completed" with completion date
- Move to "Completed PRDs" section if list grows

### Active PRDs

| PRD | Description | Status |
|-----|-------------|--------|
| `PRDs/20251206-build-cleanup/` | Build warnings and deprecation removal | In Progress |
