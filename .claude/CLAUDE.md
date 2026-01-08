# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

The Metaschema Java framework provides libraries for working with models defined by the Metaschema modeling language. It supports creating, modifying, parsing, and writing XML, JSON, and YAML instance data conforming to Metaschema models.

**Metapath** is the project's implementation of XPath 3.1. When implementing or fixing Metapath functions, use the [XPath 3.1 specification](https://www.w3.org/TR/xpath-31/) and [XPath Functions 3.1](https://www.w3.org/TR/xpath-functions-31/) as authoritative references. If behavior differs from the spec, clarify with the user before making changes.

## Build Commands

```bash
# Initialize submodules (required before first build, or in new worktrees)
git submodule update --init --recursive

# Build and install all modules
mvn install

# CI/CD build (REQUIRED before pushing - includes all quality checks)
mvn install -PCI -Prelease

# Run tests only
mvn test

# Run single test class/method
mvn -pl core test -Dtest=FnCountTest
mvn -pl core test -Dtest=FnCountTest#testCount

# Build specific module (with dependencies)
mvn -pl core -am install

# Skip tests during build
mvn install -DskipTests

# Run the CLI locally (after building)
java -jar metaschema-cli/target/metaschema-cli-*-metaschema-cli.jar --help
```

### Code Quality Commands

```bash
mvn license:check            # Check license headers
mvn license:format           # Add license headers
mvn formatter:validate       # Check code formatting
mvn formatter:format         # Auto-format code
mvn checkstyle:check         # Check Javadoc and style
mvn javadoc:javadoc          # Generate Javadoc (find errors)
```

## Static Analysis

Tools configured in parent POM ([oss-maven](https://github.com/metaschema-framework/oss-maven)):

- **Checkstyle**: Code style, Javadoc enforcement
- **PMD**: Source code analysis (fails on priority 2+ violations)
- **SpotBugs**: Bug pattern detection (uses `spotbugs-exclude.xml`)
- **Jacoco**: Code coverage (target: 60%)

## Project Architecture

### Module Hierarchy

```text
metaschema-framework (parent)
├── core                    - Core API, Metapath expression language
├── databind                - Data binding and code generation
├── schemagen               - XML/JSON schema generation
├── databind-modules        - Metaschema binding modules
├── metaschema-maven-plugin - Maven plugin for code/schema generation
├── metaschema-testing      - Testing utilities
├── cli-processor           - CLI framework
└── metaschema-cli          - Command-line interface
```

### Core Module Structure (dev.metaschema.core)

- **model/** - Metaschema model interfaces (`IModule`, `IAssemblyDefinition`, `IFieldDefinition`, `IFlagDefinition`)
- **metapath/** - Metapath query language implementation
  - **antlr/** - ANTLR4-generated parser (from `src/main/antlr4`)
  - **cst/** - Concrete syntax tree expressions
  - **function/** - Function library (`core/src/main/java/.../function/library/`)
  - **item/** - Metapath item types (node items, atomic items)
- **datatype/** - Data type adapters for Metaschema types
- **mdm/** - Metaschema Document Model node items

### Key Interfaces

- `IModule` - Represents a Metaschema module with definitions
- `IModuleLoader` - Loads Metaschema modules from various sources
- `IDataTypeAdapter` - Adapts between Java types and Metaschema data types
- `StaticContext` / `DynamicContext` - Metapath evaluation contexts

### Generated Code

- **Metaschema bindings**: Generated from Metaschema modules via metaschema-maven-plugin
- **ANTLR4**: Metapath parser from `core/src/main/antlr4`

Generated sources go to `target/generated-sources/` and are excluded from style checks.

### Bootstrap Binding Classes

Several modules contain pre-generated binding classes due to circular dependencies. **Never manually edit these** - modify the source Metaschema module instead.

| Module | Bootstrap POM | Source Metaschema |
|--------|--------------|-------------------|
| metaschema-testing | `pom-bootstrap.xml` | `unit-tests.yaml` |
| databind | `pom-bootstrap-config.xml` | `metaschema-bindings.yaml` |
| databind | `pom-bootstrap-model.xml` | `metaschema-module-metaschema.xml` |

To regenerate:

```bash
mvn install -DskipTests
mvn -f {module}/pom-bootstrap.xml process-sources
```

### Metaschema Module Authoring

**YAML-first approach**: Prefer YAML format over XML for new modules.
- Store in `src/main/metaschema/` with `.yaml` extension
- Use JSON schema at `databind-modules/target/generated-resources/schema/json/metaschema-model_schema.json` for IDE validation

## Testing

- Tests use JUnit 5 with parallel execution enabled
- Test utilities in `metaschema-testing` module
- 100% of unit tests must pass before pushing (BLOCKING)

### Test-Driven Development (TDD)

All code changes must follow TDD:
1. **Write tests first** - capture expected behavior
2. **Watch tests fail** - verify tests are meaningful
3. **Write minimal code** - make tests pass
4. **Refactor** - clean up while keeping tests green

## Code Style

- Java 11 target
- SpotBugs annotations (`@NonNull`, `@Nullable`) for null safety
- Package structure: `dev.metaschema.*`

### Javadoc Requirements (BLOCKING)

Use `dev-metaschema:javadoc-style-guide` skill for full guide.

- **New code**: 100% Javadoc on `public`/`protected` members (BLOCKING)
- **Modified code**: Add/update Javadoc on touched members
- **Exceptions**: `@Override` methods, `@Test` methods, generated code

## Git Workflow

- Repository: <https://github.com/metaschema-framework/metaschema-java>
- **All PRs MUST be created from a personal fork** (BLOCKING)
- **All PRs MUST target the `develop` branch** (BLOCKING)
- Clone with submodules: `git clone --recurse-submodules`
- All changes require PR review (CODEOWNERS enforced)

## Git Worktrees (MANDATORY)

**All development work MUST be done in a git worktree**, not in the main checkout.

Worktrees are stored in `.worktrees/` (gitignored).

```bash
# Create worktree for new feature
git worktree add .worktrees/<feature-name> -b <feature-branch> origin/develop
cd .worktrees/<feature-name>
git submodule update --init --recursive

# Check existing worktrees
git worktree list

# Remove after PR merged
git worktree remove .worktrees/<feature-name>
```

## PRD Workflow

For larger initiatives, use PRDs stored in `PRDs/<YYYYMMDD>-<name>/`:
- `PRD.md` - Requirements document
- `implementation-plan.md` - Detailed PR breakdown

**PR Size**: Target ≤50 files, maximum 100 files per PR.

**Verification**: Run `mvn clean install -PCI -Prelease` after each PR.

### Active PRDs

| PRD | Description | Status |
|-----|-------------|--------|
| `PRDs/20251206-build-cleanup/` | Build warnings and deprecation removal | In Progress |

### Completed PRDs

| PRD | Description | Completed |
|-----|-------------|-----------|
| `PRDs/20251229-javadoc-coverage/` | Complete Javadoc coverage for schemagen, databind, and maven-plugin modules | 2025-12-29 |
| `PRDs/20251228-targeted-report-constraint/` | Add constraint processing support for TargetedReportConstraint (issue #592) | 2025-12-29 |
| `PRDs/20251228-validation-errors/` | Validation error message improvements (#595, #596, #205) | 2025-12-28 |
| `PRDs/20251224-codegen-quality/` | Code generator Javadoc and quality improvements | 2025-12-28 |
| `PRDs/20251217-cli-processor-refactor/` | CLI processor refactoring (issue #252) | 2025-12-21 |
| `PRDs/20251217-context-functions/` | Complete Metapath context functions (issue #162) | 2025-12-17 |
| `PRDs/20251221-xmlbeans-removal/` | Replace XMLBeans with Metaschema bindings | 2025-12-24 |

## Available Skills

Use these skills via the Skill tool when working in this repository:

### Metaschema-Specific (dev-metaschema plugin)

- `dev-metaschema:development-workflow` - TDD, PRD workflow, parallel agents, debugging
- `dev-metaschema:prd-construction` - Creating PRDs and implementation plans
- `dev-metaschema:repo-organization` - Project structure and module relationships
- `dev-metaschema:unit-test-writing` - Edge case checklist, test patterns
- `dev-metaschema:javadoc-style-guide` - Javadoc requirements and patterns
- `dev-metaschema:claude-md-conventions` - CLAUDE.md structure and content rules
- `dev-metaschema:pr-feedback` - Addressing PR review feedback

### Metaschema Concepts (metaschema plugin)

- `metaschema:metaschema-basics` - Introduction and overview
- `metaschema:metaschema-module-authoring` - Module structure, definitions
- `metaschema:metaschema-constraints-authoring` - Constraint types, validation
- `metaschema:metapath-expressions` - Path syntax, operators, functions

### Metaschema Java Library (metaschema-tools plugin)

- `metaschema-tools:metaschema-java-library` - Key interfaces, exceptions, Metapath evaluation
- `metaschema-tools:using-metaschema-java` - CLI commands for validation, schema generation

### Development Workflow (superpowers plugin)

- `superpowers:brainstorming` - Refine ideas into designs through collaborative questioning
- `superpowers:writing-plans` - Create detailed implementation plans
- `superpowers:executing-plans` - Execute tasks in batches with review checkpoints
- `superpowers:test-driven-development` - Write tests first, then implementation
- `superpowers:systematic-debugging` - 4-phase debugging framework
- `superpowers:root-cause-tracing` - Trace bugs backward to find source
- `superpowers:verification-before-completion` - Confirm work is complete before claiming done
- `superpowers:requesting-code-review` - Review implementation against plan
- `superpowers:receiving-code-review` - Handle code review feedback properly
- `superpowers:dispatching-parallel-agents` - Concurrent work on independent tasks
- `superpowers:subagent-driven-development` - Quality-gated autonomous work
- `superpowers:using-git-worktrees` - Isolated worktree creation and management
- `superpowers:testing-anti-patterns` - Avoid common testing mistakes
