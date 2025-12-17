# PRD: External Constraint Set Builder for Test Support

## Problem Statement

The `XmlMetaConstraintLoader` class depends on XMLBeans to parse external constraint XML files. To eliminate XMLBeans from the core module's test dependencies, we need a programmatic builder that can construct `IConstraintSet` objects for testing without loading XML files.

Currently, two test classes use `XmlMetaConstraintLoader`:
1. `ExternalConstraintsModulePostProcessorTest` - tests constraint application to modules
2. `MetaConstraintLoaderTest` - tests constraint loading and evaluation

## Goals

1. Create a fluent builder API for constructing `IConstraintSet` instances programmatically
2. Migrate existing tests away from `XmlMetaConstraintLoader` to use the new builder
3. Reduce XMLBeans dependencies in test code
4. Maintain test coverage and behavior equivalence

## Non-Goals

1. Replacing `XmlMetaConstraintLoader` in production code (it will continue to exist)
2. Supporting all constraint features initially (focus on what tests need)
3. Building a general-purpose constraint authoring tool

## Requirements

### Functional Requirements

#### FR1: Constraint Set Builder
- Create `IConstraintSetBuilder` interface with fluent API
- Support building `MetaConstraintSet` instances with:
  - Source configuration
  - Imported constraint sets
  - Context hierarchy

#### FR2: Context Builder
- Support creating constraint contexts with:
  - Metapath target expressions (e.g., `//*`, `/(computer|vendor)/@id`)
  - Nested child contexts
  - Constraints applied within the context

#### FR3: Constraint Builders
Support building the constraint types used in existing tests:
- `IAllowedValuesConstraint` - with enum values
- `IMatchesConstraint` - with datatype specification

Additional constraint types (for future extensibility):
- `IExpectConstraint`
- `IIndexConstraint`
- `IUniqueConstraint`
- `ICardinalityConstraint`
- `IIndexHasKeyConstraint`

#### FR4: Integration with Test Support Infrastructure
- Integrate with `IModuleMockFactory` interface
- Follow the same patterns as `IModuleBuilder`
- Place in `gov.nist.secauto.metaschema.core.testsupport.builder` package

### Test Migration Requirements

#### TM1: ExternalConstraintsModulePostProcessorTest
Current XML (`issue184-constraints.xml`):
```xml
<context>
    <metapath target="//*"/>
    <constraints>
        <allowed-values target="@value">
            <enum value="value1">Value #1</enum>
        </allowed-values>
    </constraints>
</context>
```
Target API:
```java
IConstraintSet constraints = mocking.constraintSet()
    .context(ctx -> ctx
        .metapath("//*")
        .allowedValues(av -> av
            .target("@value")
            .allowedValue("value1", "Value #1")))
    .build();
```

#### TM2: MetaConstraintLoaderTest
Current XML (`computer-metaschema-meta-constraints.xml`):
```xml
<context>
    <metapath target="/(computer|vendor)/@id"/>
    <constraints>
        <matches target="." datatype="uuid"/>
    </constraints>
</context>
```
Target API:
```java
IConstraintSet constraints = mocking.constraintSet()
    .context(ctx -> ctx
        .metapath("/(computer|vendor)/@id")
        .matches(m -> m
            .target(".")
            .datatype(MetaschemaDataTypeProvider.UUID)))
    .build();
```

**Note:** `MetaConstraintLoaderTest` also uses `ModuleLoader` to load a module from XML. This test may need to remain XML-based or be restructured to use `IModuleBuilder` as well.

## Success Metrics

1. All migrated tests pass with programmatic constraint construction
2. No new XMLBeans usage in migrated test code
3. Builder API is intuitive and follows established patterns

## Technical Design

### Package Structure
```
gov.nist.secauto.metaschema.core.testsupport.builder/
├── IConstraintSetBuilder.java      # Main builder interface
├── ConstraintSetBuilder.java       # Implementation
├── IContextBuilder.java            # Context builder interface
├── ContextBuilder.java             # Context implementation
├── IAllowedValuesBuilder.java      # Allowed values constraint builder
├── AllowedValuesBuilder.java
├── IMatchesBuilder.java            # Matches constraint builder
├── MatchesBuilder.java
└── ... (other constraint builders as needed)
```

### Key Interfaces

```java
public interface IConstraintSetBuilder {
    IConstraintSetBuilder source(ISource source);
    IConstraintSetBuilder imports(IConstraintSet... imports);
    IConstraintSetBuilder context(Consumer<IContextBuilder> contextConfigurer);
    IConstraintSet build();
}

public interface IContextBuilder {
    IContextBuilder metapath(String target);
    IContextBuilder allowedValues(Consumer<IAllowedValuesBuilder> configurer);
    IContextBuilder matches(Consumer<IMatchesBuilder> configurer);
    IContextBuilder childContext(Consumer<IContextBuilder> configurer);
}
```

### Integration with IModuleMockFactory

Add to `IModuleMockFactory`:
```java
IConstraintSetBuilder constraintSet();
```

## Dependencies

- Existing `MetaConstraintSet` and `MetaConstraintSet.Context` classes
- Existing constraint implementations (e.g., `DefaultAllowedValuesConstraint`)
- `IModuleMockFactory` and `MockedModelTestSupport`

## Risks and Mitigations

| Risk | Impact | Mitigation |
|------|--------|------------|
| Complex constraint hierarchies hard to express | Medium | Start with simple cases, iterate based on test needs |
| Test behavior differs from XML loading | High | Verify test assertions match original behavior |
| MetaConstraintLoaderTest requires ModuleLoader | Medium | May need to keep this test XML-based or defer migration |

## Out of Scope for Initial Implementation

- Let expressions (`ILet`)
- Remarks/documentation on constraints
- Complex property expressions
- Level/severity configuration (use defaults)
