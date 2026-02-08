# PRD: Support `any` in Java Binding Annotations

**Issue:** [#220](https://github.com/metaschema-framework/metaschema-java/issues/220)
**Date:** 2026-02-06
**Status:** Draft

## Problem Statement

The Metaschema specification defines an `<any/>` instance type that allows assembly definitions to accept additional unmodeled content — content not described by the assembly's explicit model. This is analogous to `xs:any` in XML Schema or `additionalProperties` in JSON Schema.

The metaschema-java framework currently has no support for this feature:

- No core model interface (`IAnyInstance`) exists
- No databind annotation (`@BoundAny`) exists
- The `AssemblyModelGenerator` ignores the `Any` binding object when loading modules
- XML and JSON parsers silently skip unmodeled content
- Schema generators produce no wildcard declarations

OSCAL has `<any/>` commented out in at least four modules, waiting for implementation. The Metaschema test suite's anthology example already uses `<any/>` in the `author` assembly.

## Goals

1. Implement a core model interface for the `any` instance type
2. Implement a `@BoundAny` Java annotation for databind
3. Capture unmodeled content during XML parsing with round-trip fidelity
4. Capture unmodeled content during JSON/YAML parsing with round-trip fidelity
5. Generate correct XML Schema (`xs:any`) and JSON Schema (`additionalProperties`) declarations
6. Handle interaction with JSON value-key and json-key flags

## Non-Goals

- Namespace filtering attributes on `<any/>` (future enhancement if spec evolves)
- Metapath querying into `any` content
- Constraint validation of `any` content

## Design

### Core Model Layer

#### `IAnyInstance`

A new interface extending `IModelInstanceAbsolute`, joining the hierarchy alongside `IChoiceInstance` and `IChoiceGroupInstance`. It is a structural marker — no additional methods beyond what it inherits.

```java
public interface IAnyInstance extends IModelInstanceAbsolute {
}
```

#### `IAnyContent`

A format-neutral interface for representing captured unmodeled content. Lives in the core module so that model-layer code can reference it without depending on databind.

```java
public interface IAnyContent {
  boolean isEmpty();
}
```

No format-specific getters on the interface. Consumers needing format access use `instanceof` checks on the implementation.

#### Container Model Updates

`IContainerModelAssemblySupport` gains:

```java
@Nullable
IAnyInstance getAnyInstance();
```

`DefaultAssemblyModelBuilder` gains an `append(AnyI instance)` method. `DefaultContainerModelAssemblySupport` stores the optional any instance.

#### Model Visitor Updates

Visitor interfaces (`IModelDefinitionVisitor`, etc.) gain a `visitAny(IAnyInstance)` callback.

### Databind Annotation

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BoundAny {
}
```

Minimal marker annotation with no attributes. Annotates a field of type `IAnyContent` on a bound class:

```java
@MetaschemaAssembly(name = "author", ...)
public class Author implements IBoundObject {
  @BoundAny
  private IAnyContent _any;
}
```

### Databind Binding Layer

`IBoundInstanceModelAny` extends both `IAnyInstance` and the databind binding contracts, bridging core and databind layers. Follows the pattern of `IBoundInstanceModelAssembly`.

`DefinitionAssembly` (class introspection) gains scanning for `@BoundAny` fields.

### Format-Specific Content Implementations

```java
// XML: holds List<org.w3c.dom.Element>
public class XmlAnyContent implements IAnyContent {
  private final List<Element> elements;
  public List<Element> getElements() { ... }
  public boolean isEmpty() { return elements.isEmpty(); }
}

// JSON/YAML: holds com.fasterxml.jackson.databind.node.ObjectNode
public class JsonAnyContent implements IAnyContent {
  private final ObjectNode properties;
  public ObjectNode getProperties() { ... }
  public boolean isEmpty() { return properties.isEmpty(); }
}
```

### Metaschema Module Loading

`AssemblyModelGenerator` and `ChoiceModelGenerator` currently ignore the `_any` field on `AssemblyModel` and `AssemblyModel.Choice` bindings. They need to:

1. Check `binding.getAny()` after processing instances
2. If non-null, create an `IAnyInstance` and append it to the model builder

### XML Parsing

`MetaschemaXmlReader` flow with `any` support:

1. Read all known model instances as before
2. Check if the definition has an `IAnyInstance`
3. If yes: capture remaining child elements into `List<Element>` via StAX-to-DOM conversion
4. Wrap in `XmlAnyContent` and set on the bound object via the `@BoundAny` field
5. If no `IAnyInstance`: existing skip/problem-handler behavior

A new utility method converts StAX events to `org.w3c.dom.Element` subtrees.

**Writing:** `MetaschemaXmlWriter` checks for `IAnyInstance` and non-empty content. Serializes `List<Element>` to `XMLStreamWriter` after known model instances.

### JSON/YAML Parsing

`MetaschemaJsonReader` flow with `any` support:

1. During property iteration, unmatched properties are captured (not skipped) when the definition has an `IAnyInstance`
2. Each unmatched property value is read via `parser.readValueAsTree()` into a `JsonNode`
3. Collected into an `ObjectNode` (property name to value)
4. Wrapped in `JsonAnyContent` and set on the bound object

This is a buffer-during-pass approach — no rewinding needed.

**Writing:** `MetaschemaJsonWriter` iterates `ObjectNode` entries and writes each as additional properties after known instances.

YAML shares the Jackson-based JSON implementation.

### Schema Generation

**XML Schema:** Append after known element declarations:

```xml
<xs:any namespace="##other" processContents="lax"
       minOccurs="0" maxOccurs="unbounded"/>
```

Matches the precedent in the Metaschema XSD's `example` type.

**JSON Schema:** Add to the assembly's object schema:

```json
"additionalProperties": true
```

### JSON Value-Key Interaction

When an assembly uses `json-value-key` or `json-key` flags, JSON properties are named dynamically. The `any` content capture must correctly distinguish between:

- Properties matched to known model instances (including those keyed by value-key flags)
- Properties that are truly unmodeled and belong to `any`

The existing property-matching logic already resolves value-key properties before falling through to the problem handler. The `any` capture replaces the problem-handler fallback, so this should work naturally — but explicit testing is required.

## Success Criteria

- [x] `IAnyInstance` interface exists in core with visitor support
- [x] `IAnyContent` interface exists in core
- [x] `@BoundAny` annotation exists in databind
- [x] `IBoundInstanceModelAny` bridges core and databind
- [x] `AssemblyModelGenerator` and `ChoiceModelGenerator` process `<any/>`
- [x] XML parsing captures unmodeled elements into `XmlAnyContent`
- [x] XML writing serializes `XmlAnyContent` back to XML
- [x] JSON/YAML parsing captures unmodeled properties into `JsonAnyContent`
- [x] JSON/YAML writing serializes `JsonAnyContent` back
- [x] XML Schema generation produces `xs:any` for assemblies with `<any/>`
- [x] JSON Schema generation produces `additionalProperties: true`
- [x] Round-trip tests pass for XML, JSON, and YAML
- [x] JSON value-key interaction tests pass
- [x] CI build passes: `mvn clean install -PCI -Prelease`
