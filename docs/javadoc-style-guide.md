# Javadoc Style Guide

This guide defines the Javadoc requirements for the metaschema-java project, based on the Checkstyle configuration in [oss-maven](https://github.com/metaschema-framework/oss-maven/blob/main/oss-build-support/src/main/resources/checkstyle/checkstyle.xml).

## Build Configuration Notes

The project's `pom.xml` configures `maven-javadoc-plugin` with:
- `failOnWarnings: false` - Javadoc warnings do not fail the build (currently)
- `failOnError: false` - Javadoc errors do not fail the build (currently)
- Excluded packages: `*.xmlbeans`, `*.xmlbeans.*`, `*.antlr` (generated code)

**Important**: While these settings currently allow builds to pass with Javadoc issues, the goal is to progressively improve documentation coverage until these can be set to `true`.

## Coverage Requirements

### Scope

Javadoc is **required** for all `protected` and `public` members:
- Classes, interfaces, and enums
- Methods (including constructors)
- Fields

### Exceptions

Javadoc is **not required** for:
- Methods annotated with `@Override` (inherited documentation applies—see below for when to override)
- Methods annotated with `@Test` (test method names should be self-documenting)
- Private members (but encouraged if complex—see below)
- Package-private (default) members (but encouraged if complex—see below)

### Private and Package-Private Members

While not required, Javadoc **is encouraged** for private and package-private members when:
- The implementation is complex or non-obvious
- The member has important invariants or constraints
- Future maintainers would benefit from understanding the design decision
- The member interacts with other parts of the system in subtle ways

```java
/**
 * Cache of resolved definitions, keyed by qualified name. Entries are
 * lazily populated on first access and never evicted. Thread-safe via
 * synchronized access in {@link #getDefinition}.
 */
private final Map<QName, IDefinition> definitionCache = new HashMap<>();
```

### Override Methods and {@inheritDoc}

Methods annotated with `@Override` inherit documentation from the parent class or interface by default. However, you **should add or override Javadoc** when:

- The implementation has behavior beyond what the parent documents
- There are additional constraints, preconditions, or postconditions
- The implementation throws additional exceptions
- Performance characteristics differ significantly
- The implementation has side effects not mentioned in the parent

Use `{@inheritDoc}` to inherit the parent's documentation while adding implementation-specific details:

```java
/**
 * {@inheritDoc}
 * <p>
 * This implementation additionally validates that the module has been
 * initialized before returning the definition. Returns {@code null} if
 * the module is in an uninitialized state.
 *
 * @throws IllegalStateException
 *          if the module has been closed
 */
@Override
public IDefinition getDefinition(QName name) {
```

When the override implementation is straightforward and fully described by the parent documentation, no additional Javadoc is needed.

## Structure and Formatting

### Class/Interface/Enum Documentation

```java
/**
 * Brief summary sentence describing the type's purpose.
 * <p>
 * Additional paragraphs providing more detail as needed.
 *
 * @param <T>
 *          description of type parameter (indent 10 spaces from asterisk)
 */
public class Example<T> {
```

### Method Documentation

```java
/**
 * Brief summary sentence describing what the method does.
 * <p>
 * Additional detail about behavior, side effects, or usage notes.
 *
 * @param paramName
 *          description of the parameter (indent 10 spaces from asterisk)
 * @return description of return value
 * @throws ExceptionType
 *          when this exception is thrown (indent 10 spaces from asterisk)
 */
public String exampleMethod(String paramName) throws ExceptionType {
```

### Tag Order (Enforced)

Tags must appear in this order:
1. `@param` (all parameters, in declaration order)
2. `@return`
3. `@throws` / `@exception`
4. `@deprecated`

### Multi-line Tag Indentation

When a tag description spans multiple lines, continuation lines must be indented:
```java
/**
 * @param config
 *          the configuration object used to initialize the component
 *          and establish default settings
 */
```

## Summary Sentence Rules

### Forbidden Patterns

Do NOT start summaries with these patterns (enforced by Checkstyle):
- Patterns starting with `@return the`—e.g., "Returns the value" (redundant with `@return` tag)
- Patterns starting with `This method returns`—redundant phrasing
- Patterns like `A {@code ClassName} is a`—weak opening

### Good Summary Examples

```java
/** Computes the hash code for the given input. */

/** Retrieves the module definition associated with this instance. */

/** Validates the configuration and throws if invalid. */
```

### Bad Summary Examples

```java
/** This method returns the hash code. */  // BAD: "This method returns"

/** @return the hash code */  // BAD: starts with @return

/** A {@code HashComputer} is a utility class. */  // BAD: weak "is a" pattern
```

## Tag Content Requirements

### Non-Empty Descriptions

All tags must have meaningful descriptions:
```java
// GOOD
@param name the identifier used for lookup
@return the resolved configuration, or null if not found
@throws IllegalArgumentException if name is null or empty

// BAD - empty or trivial descriptions
@param name
@return the name
@throws IllegalArgumentException
```

### Throws Documentation

Document all checked exceptions and significant unchecked exceptions:
```java
/**
 * @throws IllegalArgumentException
 *          if the path is null or refers to a non-existent file
 * @throws IOException
 *          if an I/O error occurs while reading the file
 */
```

### Deprecated Documentation

When using `@deprecated`, you **must** clearly document:
1. **Why** it is deprecated (the reason)
2. **What** to use instead (the replacement)

The `@deprecated` tag should include both pieces of information:

```java
/**
 * Loads a module from the given path.
 *
 * @param path the file path to load
 * @return the loaded module
 * @deprecated This method does not support URI-based loading. Use
 *          {@link #loadModule(URI)} instead, which provides consistent
 *          handling of both file and classpath resources.
 */
@Deprecated
public IModule loadModule(Path path) {
```

For classes and interfaces:
```java
/**
 * Legacy parser for Metaschema v1 format.
 *
 * @deprecated The v1 format is no longer supported as of release 2.0.
 *          Use {@link MetaschemaParser} which supports the current v2 format
 *          and provides better error handling.
 */
@Deprecated
public class LegacyMetaschemaParser {
```

**Note**: Always pair the `@deprecated` Javadoc tag with the `@Deprecated` annotation.

## Single-Line Javadoc

For very brief documentation, single-line format is acceptable:
```java
/** Returns the parent container for this instance. */
@Override
public Container getParent() {
```

Note: Single-line Javadoc should not contain block tags like `@param` or `@return`.

## Field Documentation

Document the purpose of protected/public fields:
```java
/**
 * The cached result of the last computation, or {@code null} if not yet
 * computed.
 */
protected String cachedResult;
```

## Generic Type Parameters

Always document type parameters on generic classes:
```java
/**
 * A container for model instances.
 *
 * @param <PARENT>
 *          the Java type of the parent container
 * @param <DEFINITION>
 *          the Java type of the related definition
 * @param <INSTANCE>
 *          the Java type of the instance implementation
 */
public abstract class AbstractInstance<PARENT, DEFINITION, INSTANCE> {
```

## Inline Tags

Use inline tags for cross-references and code formatting:
- `{@code text}` - for code snippets, class names, method names
- `{@link ClassName#method}` - for cross-references (rendered as links)
- `{@linkplain ClassName text}` - for cross-references with custom text
- `{@inheritDoc}` - to inherit documentation from superclass/interface

```java
/**
 * Processes items using the algorithm defined in {@link Processor#process}.
 * Returns {@code null} if the input is empty.
 *
 * @see OtherClass#relatedMethod
 */
```

## HTML in Javadoc

Use HTML sparingly for formatting:
- `<p>` - paragraph breaks (use before new paragraphs, not after)
- `<ul>/<li>` - unordered lists
- `<ol>/<li>` - ordered lists
- `<pre>` - preformatted code blocks

```java
/**
 * Performs the following operations:
 * <ul>
 *   <li>Validates input parameters</li>
 *   <li>Processes the data</li>
 *   <li>Returns the result</li>
 * </ul>
 * <p>
 * Example usage:
 * <pre>
 * Result result = processor.process(input);
 * </pre>
 */
```

## Verification

These commands require Maven to be configured. See [CLAUDE.md](../CLAUDE.md) for build setup instructions.

Run Checkstyle to verify Javadoc compliance:
```bash
mvn checkstyle:check
```

To see all Javadoc warnings (they are currently warnings, not errors):
```bash
mvn checkstyle:checkstyle
# Then review target/checkstyle-result.xml or target/site/checkstyle.html
```

Generate full Javadoc to check for errors:
```bash
mvn javadoc:javadoc
# Review target/site/apidocs/ or check for errors in output
```

## Progressive Improvement Policy

The codebase has existing Javadoc gaps. Follow this policy:

### New Code (BLOCKING)

All new `public` and `protected` members **must** have complete Javadoc:
- Classes, interfaces, enums: summary + all type parameters documented
- Methods: summary + all `@param`, `@return`, `@throws` tags
- Constructors: summary + all `@param` tags
- Fields: purpose and valid values/states

### Modified Code (Required)

When modifying existing code:
1. Add Javadoc to any undocumented members you touch
2. Update existing Javadoc if it becomes stale or incorrect
3. Add missing tags (`@param`, `@return`, `@throws`) to incomplete Javadoc

### Surrounding Code (Encouraged)

When working in a file, consider documenting nearby undocumented members, especially:
- Public API methods in the same class
- Closely related methods you needed to understand
- Interface methods that implementations rely on

### Excluded Code

Do not add Javadoc to:
- Generated code (`*.xmlbeans`, `*.antlr` packages)
- `module-info.java` files
- Test classes and methods (use descriptive method names instead)
