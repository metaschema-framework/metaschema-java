# PRD: Fix isRequired() for Choice Block Properties

## Problem Statement

The `isRequired()` method in the code generator type info classes returns `true` for fields with `min-occurs="1"`, but doesn't account for fields that are inside `<choice>` blocks.

### Example

In `metaschema-module-metaschema.xml`, the `root-name` field is defined as:

```xml
<choice>
    <define-field name="root-name" as-type="token" min-occurs="1">
        <formal-name>Root Name</formal-name>
        ...
    </define-field>
</choice>
```

The field has `min-occurs="1"`, making `isRequired()` return `true`. However, since it's inside a `<choice>` block, it's only required when that choice branch is taken - not always.

### Impact

When the code generator generates getters with `@NonNull` annotations (based on `isRequired()` returning `true`), fields inside choice blocks that aren't populated can cause issues:
- Static analysis tools expect non-null values
- Runtime `ObjectUtils.requireNonNull()` calls would throw `NullPointerException`

## Goals

1. Fix `isRequired()` to return `false` for properties inside choice blocks
2. Ensure all generated getter/setters have proper `@Nullable`/`@NonNull` annotations based on:
   - Required properties (minOccurs >= 1, not in choice) → `@NonNull`
   - Optional properties (minOccurs = 0 or in choice) → `@Nullable`
   - Collection properties → `@NonNull` (lazy initialized)
3. Ensure Javadocs on generated getters/setters document nullability behavior
4. Document the `isRequired()` behavior in interface Javadoc

## Non-Goals

- Runtime validation of choice constraints (handled by validation framework)
- Changes to flag handling (flags cannot be inside choices in Metaschema)

## Solution

### Approach

Properties inside Metaschema choice blocks are treated as optional for null-safety purposes, regardless of their `min-occurs` value. The requirement is conditional on the choice branch being taken.

### Technical Design

The infrastructure for tracking choice membership already exists:
- `AbstractNamedModelInstanceTypeInfo` has a `choiceId` field
- `setChoiceId()` is called when processing `IChoiceInstance` in `AssemblyDefinitionTypeInfoImpl`
- The `@BoundChoice` annotation is already generated for choice properties

The fix: Update `isRequired()` to check if `getChoiceId() != null` and return `false` in that case.

### Impact on Generated Code

For properties inside choice blocks:
- Getter return annotation: `@NonNull` → `@Nullable`
- Setter parameter annotation: `@NonNull` → `@Nullable`

## Success Metrics

1. All existing tests pass
2. New unit tests verify `isRequired()` behavior for choice properties
3. Regenerated bootstrap binding classes have correct `@Nullable` annotations
4. Build passes with `mvn clean install -PCI -Prelease`

## Related

- GitHub Issue: [#604](https://github.com/metaschema-framework/metaschema-java/issues/604)
