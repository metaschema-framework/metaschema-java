---
name: metaschema-java-library
description: Use when working with metaschema-java library code - covers key interfaces, exception hierarchy, Metapath evaluation, constraint validation, and serialization/deserialization
---

# Using the metaschema-java Library

This skill covers how to use the metaschema-java library for loading modules, evaluating Metapath expressions, validating constraints, and serializing/deserializing data.

**Related user-focused skills** for Metaschema concepts:
- `metaschema-module-authoring` - Module structure, definitions, data types
- `metaschema-constraints-authoring` - Constraint types and validation patterns
- `metapath-expressions` - Metapath syntax, operators, and functions

## Module Structure

```text
metaschema-framework (parent)
├── core                    - Core API, Metapath engine, model interfaces
├── databind                - Data binding, serialization, code generation
├── schemagen               - XML/JSON schema generation
├── databind-metaschema     - Metaschema binding modules
├── metaschema-maven-plugin - Maven plugin for code/schema generation
├── metaschema-testing      - Testing utilities
├── cli-processor           - CLI framework
└── metaschema-cli          - Command-line interface
```

## Key Packages

| Package | Purpose |
|---------|---------|
| `gov.nist.secauto.metaschema.core.model` | Model interfaces (`IModule`, `IAssemblyDefinition`, etc.) |
| `gov.nist.secauto.metaschema.core.metapath` | Metapath expression engine |
| `gov.nist.secauto.metaschema.core.metapath.function` | Function library |
| `gov.nist.secauto.metaschema.core.metapath.item` | Item types (nodes, atomics) |
| `gov.nist.secauto.metaschema.core.model.constraint` | Constraint validation |
| `gov.nist.secauto.metaschema.databind` | Data binding context |
| `gov.nist.secauto.metaschema.databind.io` | Serialization/deserialization |

## Loading Metaschema Modules

### Using IBindingContext

```java
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.model.IBoundModule;

// Create binding context
IBindingContext bindingContext = IBindingContext.newInstance();

// Load module from a bound Java class
IBoundModule module = bindingContext.registerModule(MyRootClass.class);

// Or use the builder for more control
IBindingContext context = IBindingContext.builder()
    .constraintValidationHandler(handler)
    .build();
```

### Loading Module Files

```java
import gov.nist.secauto.metaschema.databind.model.metaschema.IBindingModuleLoader;

IBindingContext bindingContext = IBindingContext.newInstance();
IBindingModuleLoader loader = bindingContext.newModuleLoader();

// Load from URI
IBindingMetaschemaModule module = loader.load(moduleUri);
```

## Metapath Expression Evaluation

### Compiling and Evaluating Expressions

```java
import gov.nist.secauto.metaschema.core.metapath.MetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;

// Compile expression with static context
StaticContext staticContext = StaticContext.builder()
    .defaultModelNamespace(namespace)
    .build();

MetapathExpression expr = MetapathExpression.compile("./title", staticContext);

// Evaluate with dynamic context
DynamicContext dynamicContext = new DynamicContext(staticContext);
ISequence<?> result = expr.evaluate(focusItem, dynamicContext);
```

### Static and Dynamic Context

```java
// Static context - compile-time settings
StaticContext staticContext = StaticContext.builder()
    .namespace("prefix", "http://example.com/ns")
    .defaultModelNamespace(namespace)
    .baseUri(baseUri)
    .build();

// Dynamic context - runtime settings
DynamicContext dynamicContext = new DynamicContext(staticContext);
dynamicContext.setDocumentLoader(loader);
```

## Serialization and Deserialization

### Deserializing Content

```java
import gov.nist.secauto.metaschema.databind.io.IBoundLoader;
import gov.nist.secauto.metaschema.databind.io.Format;

IBindingContext bindingContext = IBindingContext.newInstance();
IBoundLoader loader = bindingContext.newBoundLoader();

// Auto-detect format from content
MyRootClass object = loader.load(MyRootClass.class, path);

// Or use format-specific deserializer
IDeserializer<MyRootClass> deserializer = bindingContext.newDeserializer(
    Format.XML, MyRootClass.class);
MyRootClass object = deserializer.deserialize(inputStream);
```

### Serializing Content

```java
import gov.nist.secauto.metaschema.databind.io.ISerializer;

ISerializer<MyRootClass> serializer = bindingContext.newSerializer(
    Format.JSON, MyRootClass.class);
serializer.serialize(object, outputStream);
```

### Supported Formats

| Format | Description |
|--------|-------------|
| `Format.XML` | XML serialization |
| `Format.JSON` | JSON serialization |
| `Format.YAML` | YAML serialization |

## Constraint Validation

### Basic Validation

```java
import gov.nist.secauto.metaschema.core.model.constraint.IConstraintValidator;
import gov.nist.secauto.metaschema.core.model.constraint.DefaultConstraintValidator;
import gov.nist.secauto.metaschema.core.model.constraint.FindingCollectingConstraintValidationHandler;

// Create handler to collect findings
FindingCollectingConstraintValidationHandler handler =
    new FindingCollectingConstraintValidationHandler();

// Create validator
IConstraintValidator validator = new DefaultConstraintValidator(handler);

// Validate document
validator.validate(documentNodeItem, dynamicContext);

// Check results
IValidationResult result = handler.toValidationResult();
if (!result.isPassing()) {
    result.getFindings().forEach(finding -> {
        System.err.println(finding.getMessage());
    });
}
```

### Loading External Constraints

```java
import gov.nist.secauto.metaschema.core.model.constraint.IConstraintSet;
import gov.nist.secauto.metaschema.databind.model.metaschema.BindingConstraintLoader;

BindingConstraintLoader constraintLoader = new BindingConstraintLoader(bindingContext);
IConstraintSet constraintSet = constraintLoader.load(constraintUri);
```

## Exception Hierarchy

### Base Exception

All Metapath exceptions extend `MetapathException`:

```text
MetapathException (base)
├── StaticMetapathException    - Compile-time errors (MPST prefix)
├── DynamicMetapathException   - Runtime errors (MPDY prefix)
├── TypeMetapathException      - Type-related errors (MPTY prefix)
└── InvalidMetapathGrammarException - Parser errors
```

### Error Code Prefixes

| Prefix | Category | Description |
|--------|----------|-------------|
| `MPST` | Static | Compile-time/static context errors |
| `MPDY` | Dynamic | Runtime/dynamic context errors |
| `MPTY` | Type | Type mismatch errors |
| `FOAR` | Arithmetic | Arithmetic function errors |
| `FOCA` | Cast | Cast function errors |
| `FODC` | Document | Document function errors |
| `FODT` | DateTime | Date/time function errors |
| `FONS` | Namespace | Namespace errors |
| `FORG` | Argument | Invalid argument errors |
| `FORX` | Regex | Regular expression errors |
| `FOTY` | Type | Function type errors |

### Common Static Errors

| Code | Constant | Description |
|------|----------|-------------|
| `MPST0003` | `INVALID_PATH_GRAMMAR` | Invalid Metapath grammar |
| `MPST0008` | `NOT_DEFINED` | Undefined name reference |
| `MPST0017` | `UNKNOWN_FUNCTION` | Unknown function |
| `MPST0051` | `UNSUPPORTED_AXIS` | Unsupported axis |

### Common Dynamic Errors

| Code | Constant | Description |
|------|----------|-------------|
| `MPDY0002` | `DYNAMIC_CONTEXT_ABSENT` | Required context is absent |
| `MPDY0050` | `TREAT_DOES_NOT_MATCH_TYPE` | Type mismatch in treat expression |

### Function-Specific Exceptions

```java
// Arithmetic errors
ArithmeticFunctionException  // FOAR prefix

// Cast errors
CastFunctionException        // FOCA prefix
InvalidValueForCastFunctionException

// Document errors
DocumentFunctionException    // FODC prefix

// DateTime errors
DateTimeFunctionException    // FODT prefix

// Argument errors
InvalidArgumentFunctionException  // FORG prefix
InvalidTypeFunctionException

// Regex errors
RegularExpressionMetapathException  // FORX prefix

// Array errors
ArrayMetapathException
IndexOutOfBoundsArrayMetapathException
NegativeLengthArrayMetapathException
```

## Implementing Metapath Functions

### Function Structure

```java
import gov.nist.secauto.metaschema.core.metapath.function.IFunction;
import gov.nist.secauto.metaschema.core.metapath.function.IArgument;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;

public final class FnMyFunction {
  private static final String NAME = "my-function";

  @NonNull
  static final IFunction SIGNATURE = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextIndependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("arg")
          .type(IStringItem.type())
          .zeroOrOne()
          .build())
      .returnType(IStringItem.type())
      .returnOne()
      .functionHandler(FnMyFunction::execute)
      .build();

  private FnMyFunction() {
    // disable construction
  }

  @NonNull
  private static ISequence<IStringItem> execute(
      @NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {

    ISequence<? extends IStringItem> arg = FunctionUtils.asType(
        ObjectUtils.requireNonNull(arguments.get(0)));

    // Implementation logic
    return ISequence.of(result);
  }
}
```

### Function Properties

| Property | Description |
|----------|-------------|
| `deterministic()` | Same input always produces same output |
| `contextIndependent()` | Does not depend on static/dynamic context |
| `focusIndependent()` | Does not depend on focus item |
| `contextDependent()` | Depends on context |
| `focusDependent()` | Depends on focus item |

### Registering Functions

Functions are registered in `DefaultFunctionLibrary`:

```java
// In DefaultFunctionLibrary constructor
registerFunction(FnMyFunction.SIGNATURE);
```

### Multiple Signatures

For functions with optional parameters, create multiple signatures:

```java
static final IFunction SIGNATURE_ONE_ARG = IFunction.builder()
    .name(NAME)
    // ... one argument
    .functionHandler(FnMyFunction::executeOneArg)
    .build();

static final IFunction SIGNATURE_TWO_ARG = IFunction.builder()
    .name(NAME)
    // ... two arguments
    .functionHandler(FnMyFunction::executeTwoArg)
    .build();
```

## Item Types

### Item Hierarchy

```text
IItem (base)
├── INodeItem           - Document model nodes
│   ├── IDocumentNodeItem
│   ├── IAssemblyNodeItem
│   ├── IFieldNodeItem
│   └── IFlagNodeItem
├── IAtomicItem         - Atomic values
│   ├── IStringItem
│   ├── INumericItem
│   │   ├── IIntegerItem
│   │   └── IDecimalItem
│   ├── IBooleanItem
│   ├── IDateItem
│   ├── IDateTimeItem
│   └── ...
├── IArrayItem          - Array values
└── IMapItem            - Map values
```

### Working with Sequences

```java
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;

// Create sequences
ISequence<IStringItem> seq = ISequence.of(item1, item2);
ISequence<?> empty = ISequence.empty();

// Query sequences
boolean isEmpty = sequence.isEmpty();
int size = sequence.size();
IItem first = sequence.getFirstItem(false);

// Stream sequences
sequence.stream().forEach(item -> { ... });
```

## Testing Patterns

### Testing Metapath Functions

```java
@Test
void testMyFunction() {
  StaticContext staticContext = StaticContext.instance();
  DynamicContext dynamicContext = new DynamicContext(staticContext);

  MetapathExpression expr = MetapathExpression.compile(
      "my-function('input')", staticContext);

  ISequence<?> result = expr.evaluate(null, dynamicContext);

  assertEquals(1, result.size());
  assertEquals("expected", result.getFirstItem(true).asString());
}
```

### Testing with Document Context

```java
@Test
void testWithDocument() {
  IBindingContext bindingContext = IBindingContext.newInstance();
  IBoundLoader loader = bindingContext.newBoundLoader();

  IBoundObject object = loader.load(MyClass.class, testFile);
  IDocumentNodeItem document = bindingContext.toNodeItem(object);

  DynamicContext dynamicContext = new DynamicContext(staticContext);
  MetapathExpression expr = MetapathExpression.compile("//item", staticContext);

  ISequence<?> result = expr.evaluate(document, dynamicContext);
  // assertions
}
```

## References

- [XPath 3.1 Specification](https://www.w3.org/TR/xpath-31/)
- [XPath Functions 3.1](https://www.w3.org/TR/xpath-functions-31/)
- [Metaschema Specification](https://framework.metaschema.dev/specification/)
