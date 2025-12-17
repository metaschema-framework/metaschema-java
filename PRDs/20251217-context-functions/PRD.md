# PRD: Complete Metapath Context Functions (Issue #162)

## Summary

This PRD covers the implementation of the remaining Metapath context functions from [issue #162](https://github.com/metaschema-framework/metaschema-java/issues/162). These functions allow Metapath expression authors to query information from the dynamic context during evaluation.

### Scope

**In Scope:**
- `fn:position` - Returns the context position from the dynamic context
- `fn:last` - Returns the context size from the dynamic context
- `fn:default-language` - Returns the value of the default language property from the dynamic context

**Already Completed (out of scope):**
- `fn:current-dateTime` - #164
- `fn:current-date` - #274
- `fn:current-time` - #265
- `fn:implicit-timezone` - #265
- `fn:static-base-uri` - already implemented

## Problem Statement

Users writing Metapath expressions (particularly in Metaschema constraints) need access to context information such as:
1. The position of the current item within a sequence being processed
2. The total size of the sequence being processed
3. The default language for locale-sensitive operations

Without `position()` and `last()`, users cannot write predicates like `item[position() = last()]` to select the last item, or `item[position() mod 2 = 0]` to select even-numbered items.

## Technical Analysis

### Current Architecture

The Metapath evaluation architecture passes focus through expressions as follows:

1. **IExpression.evaluate()** receives `(DynamicContext, ISequence<?> focus)`
2. **IFunctionExecutor.execute()** receives `(IFunction, List<ISequence<?>>, DynamicContext, IItem focus)`

Key architectural observations:

#### PredicateExpression (line 98-126)
Currently tracks position locally but doesn't expose it to predicates:

```java
AtomicInteger index = new AtomicInteger();
retval.stream().map(item -> Map.entry(BigInteger.valueOf(index.incrementAndGet()), item))
    .filter(entry -> {
        // Position is available here as entry.getKey()
        // But when evaluating predicate expressions, only the item is passed:
        ISequence<?> innerFocus = ISequence.of(item);  // Position not passed!
        ISequence<?> predicateResult = predicateExpr.accept(dynamicContext, innerFocus);
    })
```

#### DynamicContext
Contains evaluation state but NO position-related fields:
- `currentDateTime` (ZonedDateTime)
- `implicitTimeZone` (ZoneId)
- `availableDocuments` (Map<URI, IDocumentNodeItem>)
- `letVariableMap` (Map for variable bindings)
- `functionResultCache` (caching for deterministic functions)

#### StaticContext
Contains declaration-time state but NO language property:
- `baseUri`
- `knownPrefixToNamespace` / `knownNamespacesToPrefix`
- `defaultModelNamespace` / `defaultFunctionNamespace`
- `functionResolver`

### XPath 3.1 Specification Reference

Per the [XPath 3.1 specification](https://www.w3.org/TR/xpath-31/#eval_context):

#### Context Position and Size
- **Context position**: An integer greater than zero, the position of the context item within the sequence of items currently being processed
- **Context size**: An integer greater than zero, the number of items in the sequence of items currently being processed

#### fn:position()
- **Signature**: `fn:position() as xs:integer`
- **Summary**: Returns the context position from the dynamic context
- **Rules**: Returns the position of the context item within the sequence being processed
- **Error**: XPDY0002 if context is absent

#### fn:last()
- **Signature**: `fn:last() as xs:integer`
- **Summary**: Returns the context size from the dynamic context
- **Rules**: Returns the total number of items in the sequence being processed
- **Error**: XPDY0002 if context is absent

#### fn:default-language()
- **Signature**: `fn:default-language() as xs:language`
- **Summary**: Returns the value of the default language property from the dynamic context
- **Rules**: Returns xs:language value representing the default language (typically "en")

## Requirements

### Functional Requirements

#### FR-1: fn:position Implementation
- Must return the 1-based position of the current item in the enclosing sequence
- Must raise XPDY0002 error if context item is absent
- Must work correctly within predicate expressions
- Must work correctly in FLWOR expressions

#### FR-2: fn:last Implementation
- Must return the total count of items in the enclosing sequence
- Must raise XPDY0002 error if context item is absent
- Must work correctly within predicate expressions
- Must work correctly in FLWOR expressions

#### FR-3: fn:default-language Implementation
- Must return a default language value (typically "en")
- Must be configurable via StaticContext or DynamicContext
- Must return xs:language type value

### Non-Functional Requirements

#### NFR-1: Backwards Compatibility
- Existing Metapath expressions must continue to work unchanged
- Changes to DynamicContext/StaticContext must maintain existing API

#### NFR-2: Performance
- Adding position tracking must not significantly impact evaluation performance
- Position tracking should be lazy/efficient (avoid computing sequence size unless `last()` is called)

## Solution Design

### Architectural Changes

#### Phase 1: Focus Context Enhancement

Create a new `FocusContext` class to encapsulate focus information including position:

```java
public final class FocusContext {
    private final ISequence<?> sequence;    // The full sequence
    private final int position;              // 1-based current position
    private final int size;                  // Total sequence size (lazy?)

    // Factory methods
    public static FocusContext of(ISequence<?> sequence);
    public static FocusContext of(IItem item, int position, int size);

    // Accessors
    public IItem getContextItem();
    public int getPosition();
    public int getSize();
}
```

#### Phase 2: DynamicContext Enhancement

Extend DynamicContext to hold focus context as an instance field (not in SharedState, since focus is local to each evaluation scope):

```java
public class DynamicContext {
    // New instance field (not in SharedState)
    @Nullable
    private FocusContext focusContext;

    // New methods
    @Nullable
    public FocusContext getFocusContext();

    /**
     * Generate a new dynamic context with the specified focus context.
     * <p>
     * This is used by predicates to establish a new focus for position()/last().
     *
     * @param focusContext the focus context for the new sub-context
     * @return a new dynamic context with the focus context set
     */
    @NonNull
    public DynamicContext subContext(@NonNull FocusContext focusContext);
}
```

**Important**: The existing `subContext()` (no args) must **preserve** the focusContext from the parent. This is required for expressions like:

```xpath
(1,2,3)[some $x in (4,5,6) satisfies $x > . and position() = 1]
```

Here, the `some` expression creates a sub-context for `$x` binding, but `position()` inside the `satisfies` clause must still access the outer predicate's focus context.

| Method | Behavior |
|--------|----------|
| `subContext()` | Preserves parent's focusContext (for variable binding scopes) |
| `subContext(FocusContext)` | Sets new focusContext (for predicates) |

#### Phase 3: PredicateExpression Modification

Update PredicateExpression to pass position information through sub-context using the single-operation `subContext(FocusContext)`:

```java
// In PredicateExpression.evaluate()
AtomicInteger index = new AtomicInteger();
int size = retval.size();  // Compute size once

retval.stream().map(item -> {
    int pos = index.incrementAndGet();
    // Single operation: create sub-context with focus in one call
    DynamicContext subContext = dynamicContext.subContext(
        FocusContext.of(item, pos, size));
    return Map.entry(subContext, item);
}).filter(entry -> {
    ISequence<?> innerFocus = ISequence.of(entry.getValue());
    ISequence<?> predicateResult = predicateExpr.accept(entry.getKey(), innerFocus);
    return FnBoolean.fnBoolean(predicateResult).toBoolean();
});
```

#### Phase 4: StaticContext Enhancement (for default-language)

```java
public final class StaticContext {
    @Nullable
    private final String defaultLanguage;  // New field

    // Builder addition
    public Builder defaultLanguage(@NonNull String language);

    // Accessor
    @NonNull
    public String getDefaultLanguage();  // Returns "en" if not set
}
```

### Function Implementations

#### FnPosition.java

```java
public final class FnPosition {
    static final IFunction SIGNATURE = IFunction.builder()
        .name("position")
        .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
        .deterministic()
        .contextDependent()
        .focusDependent()  // Important: depends on focus
        .returnType(IIntegerItem.type())
        .returnOne()
        .functionHandler(FnPosition::execute)
        .build();

    private static ISequence<IIntegerItem> execute(
            IFunction function,
            List<ISequence<?>> arguments,
            DynamicContext dynamicContext,
            IItem focus) {
        FocusContext focusContext = dynamicContext.getFocusContext();
        if (focusContext == null) {
            throw new ContextAbsentDynamicMetapathException(
                ContextAbsentDynamicMetapathException.CONTEXT_ITEM_ABSENT,
                "The context position is absent");
        }
        return ISequence.of(IIntegerItem.valueOf(focusContext.getPosition()));
    }
}
```

#### FnLast.java

```java
public final class FnLast {
    static final IFunction SIGNATURE = IFunction.builder()
        .name("last")
        .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
        .deterministic()
        .contextDependent()
        .focusDependent()  // Important: depends on focus
        .returnType(IIntegerItem.type())
        .returnOne()
        .functionHandler(FnLast::execute)
        .build();

    private static ISequence<IIntegerItem> execute(
            IFunction function,
            List<ISequence<?>> arguments,
            DynamicContext dynamicContext,
            IItem focus) {
        FocusContext focusContext = dynamicContext.getFocusContext();
        if (focusContext == null) {
            throw new ContextAbsentDynamicMetapathException(
                ContextAbsentDynamicMetapathException.CONTEXT_ITEM_ABSENT,
                "The context size is absent");
        }
        return ISequence.of(IIntegerItem.valueOf(focusContext.getSize()));
    }
}
```

#### FnDefaultLanguage.java

```java
public final class FnDefaultLanguage {
    private static final String DEFAULT_LANGUAGE = "en";

    static final IFunction SIGNATURE = IFunction.builder()
        .name("default-language")
        .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
        .deterministic()
        .contextDependent()
        .focusIndependent()
        .returnType(IStringItem.type())  // xs:language is a string subtype
        .returnOne()
        .functionHandler(FnDefaultLanguage::execute)
        .build();

    private static ISequence<IStringItem> execute(
            IFunction function,
            List<ISequence<?>> arguments,
            DynamicContext dynamicContext,
            IItem focus) {
        String language = dynamicContext.getStaticContext().getDefaultLanguage();
        return ISequence.of(IStringItem.valueOf(
            language != null ? language : DEFAULT_LANGUAGE));
    }
}
```

## Implementation Plan

### PR 1: Add FocusContext Infrastructure (Foundation)

**Files to create:**
- `core/src/main/java/gov/nist/secauto/metaschema/core/metapath/FocusContext.java`

**Files to modify:**
- `core/src/main/java/gov/nist/secauto/metaschema/core/metapath/DynamicContext.java`

**Acceptance Criteria:**
- [ ] FocusContext class created with position/size fields
- [ ] DynamicContext extended with focusContext field and methods
- [ ] subContext() preserves focus context
- [ ] All existing tests pass
- [ ] Unit tests for FocusContext
- [ ] Run `mvn clean install -PCI -Prelease` passes

### PR 2: Implement fn:position and fn:last

**Files to create:**
- `core/src/main/java/gov/nist/secauto/metaschema/core/metapath/function/library/FnPosition.java`
- `core/src/main/java/gov/nist/secauto/metaschema/core/metapath/function/library/FnLast.java`
- `core/src/test/java/gov/nist/secauto/metaschema/core/metapath/function/library/FnPositionTest.java`
- `core/src/test/java/gov/nist/secauto/metaschema/core/metapath/function/library/FnLastTest.java`

**Files to modify:**
- `core/src/main/java/gov/nist/secauto/metaschema/core/metapath/function/library/DefaultFunctionLibrary.java`
- `core/src/main/java/gov/nist/secauto/metaschema/core/metapath/cst/logic/PredicateExpression.java`

**Acceptance Criteria:**
- [ ] FnPosition implementation complete
- [ ] FnLast implementation complete
- [ ] PredicateExpression sets FocusContext during evaluation
- [ ] Positive test cases: `position()`, `last()`, `position() = last()`, `position() mod 2`
- [ ] Negative test cases: error when context absent
- [ ] Integration tests with predicates
- [ ] Run `mvn clean install -PCI -Prelease` passes

### PR 3: Add default-language Support

**Files to create:**
- `core/src/main/java/gov/nist/secauto/metaschema/core/metapath/function/library/FnDefaultLanguage.java`
- `core/src/test/java/gov/nist/secauto/metaschema/core/metapath/function/library/FnDefaultLanguageTest.java`

**Files to modify:**
- `core/src/main/java/gov/nist/secauto/metaschema/core/metapath/StaticContext.java`
- `core/src/main/java/gov/nist/secauto/metaschema/core/metapath/function/library/DefaultFunctionLibrary.java`

**Acceptance Criteria:**
- [ ] StaticContext extended with defaultLanguage field
- [ ] FnDefaultLanguage implementation complete
- [ ] Returns "en" by default
- [ ] Configurable via StaticContext.Builder
- [ ] Positive test cases for default-language()
- [ ] Run `mvn clean install -PCI -Prelease` passes

### PR 4: Documentation and Cleanup

**Files to modify:**
- Update any affected documentation
- Close issue #162 with PR reference

**Acceptance Criteria:**
- [ ] All functions documented in code (Javadoc)
- [ ] README or website docs updated if needed
- [ ] Issue #162 closed

## Risks and Mitigations

| Risk | Impact | Mitigation |
|------|--------|------------|
| Performance impact from position tracking | Medium | Lazy computation of size; only compute when last() is called |
| Breaking changes to DynamicContext | High | New methods are additive; existing API unchanged |
| Complex integration with FLWOR expressions | Medium | Focus on predicate expressions first; FLWOR integration in follow-up if needed |
| xs:language type not implemented | Low | Use IStringItem for now; add proper xs:language type later |

## Success Metrics

1. All three functions (`position()`, `last()`, `default-language()`) implemented
2. All acceptance criteria from issue #162 met:
   - Positive test cases with full coverage for each function
   - Negative test cases covering error conditions
   - CI-CD build passes on PR
3. No regression in existing functionality
4. Performance impact < 5% on predicate evaluation benchmarks

## References

- [Issue #162](https://github.com/metaschema-framework/metaschema-java/issues/162)
- [XPath 3.1 Specification](https://www.w3.org/TR/xpath-31/)
- [XPath Functions 3.1](https://www.w3.org/TR/xpath-functions-31/)
- [fn:position spec](https://www.w3.org/TR/xpath-functions-31/#func-position)
- [fn:last spec](https://www.w3.org/TR/xpath-functions-31/#func-last)
- [fn:default-language spec](https://www.w3.org/TR/xpath-functions-31/#func-default-language)
