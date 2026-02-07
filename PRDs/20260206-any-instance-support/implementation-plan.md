# `any` Instance Support Implementation Plan

**Goal:** Add full support for the Metaschema `<any/>` instance type — core model interfaces, `@BoundAny` annotation, XML/JSON/YAML parsing with round-trip fidelity, and schema generation.

**Architecture:** A format-neutral `IAnyContent` interface in core wraps native content representations (W3C DOM for XML, Jackson `ObjectNode` for JSON/YAML). A new `IAnyInstance` model interface marks assemblies that accept unmodeled content. The databind layer provides `@BoundAny` annotation, parsing capture, and serialization. Schema generators emit `xs:any` (XML) and `additionalProperties` (JSON).

**Tech Stack:** Java 11, JUnit 5, StAX (XML parsing), Jackson (JSON), W3C DOM, ANTLR4 (existing Metapath grammar — no changes needed).

**Issue:** [#220](https://github.com/metaschema-framework/metaschema-java/issues/220)

**Worktree:** `.worktrees/any-instance` (branch `feature/220-any-instance`)

**Single PR** targeting `develop` branch.

**Specification Documentation:** Metaschema language-level documentation for `<any/>` is tracked in companion PR [metaschema-framework/metaschema#171](https://github.com/metaschema-framework/metaschema/pull/171).

---

## Phase 1: Core Model Layer and `@BoundAny` Annotation [COMPLETE]

Establishes the foundational interfaces, annotation, container model changes, and module loading support.

### Task 1: Create `IAnyContent` Interface [COMPLETE]

**Files:**
- Create: `core/src/main/java/dev/metaschema/core/model/IAnyContent.java`

**Step 1: Create the interface**

```java
package dev.metaschema.core.model;

/**
 * A format-neutral representation of unmodeled content captured from an
 * assembly instance that declares {@code <any/>} in its model.
 *
 * <p>Implementations hold native content representations specific to each
 * serialization format (e.g., W3C DOM for XML, Jackson ObjectNode for JSON).
 * Consumers needing format-specific access should use {@code instanceof}
 * checks on the implementation class.
 */
public interface IAnyContent {
  /**
   * Determine if this content container has no captured content.
   *
   * @return {@code true} if no unmodeled content was captured, {@code false}
   *         otherwise
   */
  boolean isEmpty();
}
```

**Step 2: Verify compilation**

Run: `mvn -pl core compile -q`
Expected: BUILD SUCCESS

**Step 3: Commit**

```text
feat: add IAnyContent interface for unmodeled content

Introduces a format-neutral interface for representing captured
unmodeled content from assemblies with <any/> declarations.
```

---

### Task 2: Create `IAnyInstance` Interface [COMPLETE]

**Files:**
- Create: `core/src/main/java/dev/metaschema/core/model/IAnyInstance.java`
- Reference: `core/src/main/java/dev/metaschema/core/model/IChoiceInstance.java` (pattern to follow)

**Step 1: Create the interface**

Model `IAnyInstance` after `IChoiceInstance`. It extends `IModelInstanceAbsolute` since it's a structural member of an assembly model, not a named instance. Key semantics: always optional (`minOccurs=0`), always unbounded (`maxOccurs=-1`), returns `ModelType.ANY`.

```java
package dev.metaschema.core.model;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents an {@code <any/>} instance in a Metaschema assembly model,
 * declaring that the assembly accepts additional unmodeled content.
 *
 * <p>This is analogous to {@code xs:any} in XML Schema or
 * {@code additionalProperties} in JSON Schema.
 */
public interface IAnyInstance extends IModelInstanceAbsolute {

  @Override
  default ModelType getModelType() {
    return ModelType.ANY;
  }

  @Override
  default int getMinOccurs() {
    return 0;
  }

  @Override
  default int getMaxOccurs() {
    return -1;
  }
}
```

**Step 2: Add `ANY` to `ModelType` enum**

Modify: `core/src/main/java/dev/metaschema/core/model/ModelType.java`

Add `ANY` enum constant. Check the existing enum values and add after the last one.

**Step 3: Verify compilation**

Run: `mvn -pl core compile -q`
Expected: BUILD SUCCESS

**Step 4: Commit**

```text
feat: add IAnyInstance interface and ModelType.ANY

Introduces the core model interface for <any/> instances in
assembly models. Adds ANY to ModelType enum.
```

---

### Task 3: Update Container Model Interfaces and Implementations [COMPLETE]

**Files:**
- Modify: `core/src/main/java/dev/metaschema/core/model/IContainerModelAssemblySupport.java`
- Modify: `core/src/main/java/dev/metaschema/core/model/impl/DefaultContainerModelAssemblySupport.java`
- Modify: `core/src/main/java/dev/metaschema/core/model/DefaultAssemblyModelBuilder.java`

**Step 1: Write tests for container model**

Create: `core/src/test/java/dev/metaschema/core/model/DefaultAssemblyModelBuilderTest.java`

Write tests verifying:
- Building an assembly model with no any instance returns `null` from `getAnyInstance()`
- Building an assembly model with an any instance returns the instance from `getAnyInstance()`
- The empty container's `getAnyInstance()` returns `null`

**Step 2: Run tests to verify they fail**

Run: `mvn -pl core test -Dtest=DefaultAssemblyModelBuilderTest -q`
Expected: FAIL (methods don't exist yet)

**Step 3: Add `getAnyInstance()` to `IContainerModelAssemblySupport`**

In `IContainerModelAssemblySupport.java`, add a new type parameter `ANI extends IAnyInstance` and a method:

```java
/**
 * Get the any instance declared in this model, if any.
 *
 * @return the any instance, or {@code null} if no any is declared
 */
@Nullable
ANI getAnyInstance();
```

Update the `empty()` static method to return `null` for `getAnyInstance()`.

Note: Adding a type parameter is a breaking change to all implementations. All classes that implement or extend this interface will need updating. Check all implementors and update their type parameter lists.

**Step 4: Add `anyInstance` field to `DefaultContainerModelAssemblySupport`**

Add a `@Nullable ANI anyInstance` field. Update both constructors (empty mutable and full). Update the `EMPTY` static constant. Add getter.

**Step 5: Add `append` and getter to `DefaultAssemblyModelBuilder`**

Add a `@Nullable` any instance field, an `append(ANI instance)` method, a `getAnyInstance()` getter, and pass it to `buildAssembly()`.

**Step 6: Run tests to verify they pass**

Run: `mvn -pl core test -Dtest=DefaultAssemblyModelBuilderTest -q`
Expected: PASS

**Step 7: Fix all compilation errors from type parameter changes**

The type parameter addition to `IContainerModelAssemblySupport` will cause compilation errors in all implementors. Fix each one by adding the new type parameter. This includes classes in both `core` and `databind` modules.

Run: `mvn -pl core,databind compile -q`
Expected: BUILD SUCCESS

**Step 8: Run full test suite**

Run: `mvn -pl core,databind test -q`
Expected: All tests pass

**Step 9: Commit**

```text
feat: add any instance support to container model

Updates IContainerModelAssemblySupport with getAnyInstance(),
DefaultContainerModelAssemblySupport with storage, and
DefaultAssemblyModelBuilder with append support.
```

---

### Task 4: Update Model Visitor [COMPLETE]

**Files:**
- Modify: `core/src/main/java/dev/metaschema/core/model/IModelElementVisitor.java`
- Modify all visitor implementations that handle model instance types

**Step 1: Add `visitAny` to visitor interface**

In `IModelElementVisitor.java`, add:

```java
/**
 * Visit an any instance.
 *
 * @param instance
 *          the any instance to visit
 * @param context
 *          the processing context
 * @return the visitation result
 */
RESULT visitAny(@NonNull IAnyInstance instance, CONTEXT context);
```

**Step 2: Add `accept` method to `IAnyInstance`**

Add a default `accept` method similar to `IChoiceInstance`:

```java
@Override
default <CONTEXT, RESULT> RESULT accept(@NonNull IModelElementVisitor<CONTEXT, RESULT> visitor, CONTEXT context) {
  return visitor.visitAny(this, context);
}
```

**Step 3: Fix all visitor implementations**

Find all classes implementing `IModelElementVisitor` and add the `visitAny` method. Most can return a default/no-op result initially.

**Step 4: Verify compilation and tests**

Run: `mvn -pl core,databind compile -q && mvn -pl core,databind test -q`
Expected: BUILD SUCCESS, all tests pass

**Step 5: Commit**

```text
feat: add visitAny to model visitor interface

Extends IModelElementVisitor with visitAny callback for
traversing any instances in assembly models.
```

---

### Task 5: Create `@BoundAny` Annotation [COMPLETE]

**Files:**
- Create: `databind/src/main/java/dev/metaschema/databind/model/annotations/BoundAny.java`
- Reference: `databind/src/main/java/dev/metaschema/databind/model/annotations/BoundAssembly.java` (pattern)

**Step 1: Create the annotation**

```java
package dev.metaschema.databind.model.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks a field of type {@link dev.metaschema.core.model.IAnyContent} on a
 * bound class to receive unmodeled content from assemblies that declare
 * {@code <any/>} in their model.
 *
 * <p>During deserialization, content not matching any declared model instance
 * is captured into this field. During serialization, captured content is
 * written back after all declared model instances.
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface BoundAny {
}
```

**Step 2: Verify compilation**

Run: `mvn -pl databind compile -q`
Expected: BUILD SUCCESS

**Step 3: Commit**

```text
feat: add @BoundAny annotation for unmodeled content

Minimal marker annotation for IAnyContent fields on bound
classes. Marks fields to receive captured unmodeled content.
```

---

### Task 6: Create `IBoundInstanceModelAny` and Implementation [COMPLETE]

**Files:**
- Create: `databind/src/main/java/dev/metaschema/databind/model/IBoundInstanceModelAny.java`
- Create: `databind/src/main/java/dev/metaschema/databind/model/impl/InstanceModelAny.java`
- Modify: `databind/src/main/java/dev/metaschema/databind/model/impl/AssemblyModelGenerator.java`
- Reference: `databind/src/main/java/dev/metaschema/databind/model/IBoundInstanceModelAssembly.java` (pattern)

**Step 1: Create `IBoundInstanceModelAny` interface**

This bridges `IAnyInstance` with the databind binding layer. It should extend `IAnyInstance` and the necessary databind interfaces for field access. The exact superinterfaces depend on what's needed for reading/writing the `@BoundAny` field — study `IBoundInstanceModelAssembly` for the pattern.

Key methods needed:
- Access to the underlying Java `Field` annotated with `@BoundAny`
- Getter/setter for `IAnyContent` on the bound object
- Reference to the containing definition

**Step 2: Create `InstanceModelAny` implementation**

Implementation that wraps a `java.lang.reflect.Field` annotated with `@BoundAny`. Provides:
- `getField()` returning the annotated field
- `getValue(Object parent)` reading the `IAnyContent` from the parent object
- `setValue(Object parent, IAnyContent value)` setting it
- `getContainingDefinition()` returning the assembly definition

**Step 3: Update `AssemblyModelGenerator` to scan for `@BoundAny`**

In `AssemblyModelGenerator.java`, in the `of()` method or `getModelInstanceStream()`, add scanning for `@BoundAny` fields. When found, create an `InstanceModelAny` and append it to the builder.

Only one `@BoundAny` field should be allowed per class — validate this and throw if multiple are found.

**Step 4: Verify compilation and existing tests pass**

Run: `mvn -pl databind compile -q && mvn -pl databind test -q`
Expected: BUILD SUCCESS, all tests pass

**Step 5: Commit**

```text
feat: add IBoundInstanceModelAny and annotation scanning

Bridges IAnyInstance with the databind layer. AssemblyModelGenerator
now scans for @BoundAny fields during class introspection.
```

---

### Task 7: Update Module Loading (AssemblyModelGenerator and ChoiceModelGenerator) [COMPLETE]

**Files:**
- Modify: `databind/src/main/java/dev/metaschema/databind/model/metaschema/impl/AssemblyModelGenerator.java` (lines ~88-145)
- Modify: `databind/src/main/java/dev/metaschema/databind/model/metaschema/impl/ChoiceModelGenerator.java` (lines ~61-113)
- Create implementation of `IAnyInstance` for module-loaded definitions

Note: There are TWO `AssemblyModelGenerator` classes — one in `databind/model/impl/` (Task 6, scans annotations) and one in `databind/model/metaschema/impl/` (this task, processes loaded module bindings). This task addresses the module-loading one.

**Step 1: Write test for module loading**

Create: `databind/src/test/java/dev/metaschema/databind/model/metaschema/AnyInstanceLoadingTest.java`

Write a test that:
1. Loads a Metaschema module containing `<any/>` in an assembly model
2. Retrieves the assembly definition
3. Asserts `getAnyInstance()` is not null

Use the anthology metaschema from the test suite submodule, or create a minimal test metaschema.

**Step 2: Run test to verify it fails**

Run: `mvn -pl databind test -Dtest=AnyInstanceLoadingTest -q`
Expected: FAIL (any instance not loaded)

**Step 3: Create `IAnyInstance` implementation for module-loaded definitions**

Create a concrete class in `databind/model/metaschema/impl/` that implements `IAnyInstance` for module-loaded assemblies.

**Step 4: Update `AssemblyModelGenerator` (metaschema/impl)**

After the existing `forEach` loop that processes instances, add:

```java
Any any = binding.getAny();
if (any != null) {
  generator.append(new ModuleAnyInstance(parent));
}
```

**Step 5: Update `ChoiceModelGenerator`**

Similarly process the `any` field from `AssemblyModel.Choice`:

```java
Any any = binding.getAny();
if (any != null) {
  // handle any in choice context
}
```

**Step 6: Run test to verify it passes**

Run: `mvn -pl databind test -Dtest=AnyInstanceLoadingTest -q`
Expected: PASS

**Step 7: Run full test suite**

Run: `mvn -pl core,databind test -q`
Expected: All tests pass

**Step 8: Commit**

```text
feat: process <any/> during module loading

AssemblyModelGenerator and ChoiceModelGenerator now create
IAnyInstance entries when loading modules with <any/> declarations.
```

---

---

## Phase 2: Content Capture and XML/JSON Parsing [COMPLETE]

Adds format-specific `IAnyContent` implementations and parsing support.

### Task 8: Create `XmlAnyContent` Implementation [COMPLETE]

**Files:**
- Create: `databind/src/main/java/dev/metaschema/databind/io/xml/XmlAnyContent.java`

**Step 1: Write tests**

Create: `databind/src/test/java/dev/metaschema/databind/io/xml/XmlAnyContentTest.java`

Test:
- Empty content reports `isEmpty() == true`
- Content with elements reports `isEmpty() == false`
- `getElements()` returns the stored elements

**Step 2: Run tests to verify they fail**

Run: `mvn -pl databind test -Dtest=XmlAnyContentTest -q`
Expected: FAIL

**Step 3: Implement `XmlAnyContent`**

```java
package dev.metaschema.databind.io.xml;

import dev.metaschema.core.model.IAnyContent;
import org.w3c.dom.Element;

import java.util.Collections;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * XML-specific implementation of {@link IAnyContent} that stores captured
 * unmodeled content as W3C DOM {@link Element} instances.
 */
public class XmlAnyContent implements IAnyContent {
  @NonNull
  private final List<Element> elements;

  /**
   * Construct with captured elements.
   *
   * @param elements
   *          the captured DOM elements, must not be null
   */
  public XmlAnyContent(@NonNull List<Element> elements) {
    this.elements = Collections.unmodifiableList(elements);
  }

  @Override
  public boolean isEmpty() {
    return elements.isEmpty();
  }

  /**
   * Get the captured DOM elements.
   *
   * @return an unmodifiable list of captured elements
   */
  @NonNull
  public List<Element> getElements() {
    return elements;
  }
}
```

**Step 4: Run tests to verify they pass**

Run: `mvn -pl databind test -Dtest=XmlAnyContentTest -q`
Expected: PASS

**Step 5: Commit**

```text
feat: add XmlAnyContent for captured XML elements
```

---

### Task 9: Create `JsonAnyContent` Implementation [COMPLETE]

**Files:**
- Create: `databind/src/main/java/dev/metaschema/databind/io/json/JsonAnyContent.java`

**Step 1: Write tests**

Create: `databind/src/test/java/dev/metaschema/databind/io/json/JsonAnyContentTest.java`

Test:
- Empty `ObjectNode` reports `isEmpty() == true`
- `ObjectNode` with properties reports `isEmpty() == false`
- `getProperties()` returns the stored node

**Step 2: Run tests to verify they fail**

Run: `mvn -pl databind test -Dtest=JsonAnyContentTest -q`
Expected: FAIL

**Step 3: Implement `JsonAnyContent`**

```java
package dev.metaschema.databind.io.json;

import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.metaschema.core.model.IAnyContent;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * JSON/YAML-specific implementation of {@link IAnyContent} that stores
 * captured unmodeled content as a Jackson {@link ObjectNode}.
 */
public class JsonAnyContent implements IAnyContent {
  @NonNull
  private final ObjectNode properties;

  /**
   * Construct with captured properties.
   *
   * @param properties
   *          the captured JSON properties, must not be null
   */
  public JsonAnyContent(@NonNull ObjectNode properties) {
    this.properties = properties;
  }

  @Override
  public boolean isEmpty() {
    return properties.isEmpty();
  }

  /**
   * Get the captured JSON properties.
   *
   * @return the captured ObjectNode
   */
  @NonNull
  public ObjectNode getProperties() {
    return properties;
  }
}
```

**Step 4: Run tests to verify they pass**

Run: `mvn -pl databind test -Dtest=JsonAnyContentTest -q`
Expected: PASS

**Step 5: Commit**

```text
feat: add JsonAnyContent for captured JSON/YAML properties
```

---

### Task 10: XML Parsing — Capture Unmodeled Content [COMPLETE]

**Files:**
- Modify: `databind/src/main/java/dev/metaschema/databind/io/xml/MetaschemaXmlReader.java` (lines ~274-313)
- Create or modify: utility for StAX-to-DOM conversion

**Step 1: Write round-trip test**

Create: `databind/src/test/java/dev/metaschema/databind/io/xml/AnyXmlRoundTripTest.java`

Create a test Metaschema module with an assembly containing `<any/>`, and a bound test class with `@BoundAny`. Create test XML with unknown elements. Verify:
1. Parsing captures the unknown elements into `XmlAnyContent`
2. The captured elements have correct names, attributes, and nested content
3. Writing back produces equivalent XML

**Step 2: Run test to verify it fails**

Expected: FAIL (content not captured)

**Step 3: Use `XmlDomUtil.staxToElement()` for StAX-to-DOM conversion**

Use the existing `XmlDomUtil.staxToElement()` method in `databind/src/main/java/dev/metaschema/databind/io/xml/XmlDomUtil.java` to convert StAX events to DOM elements. This utility reads from the current start element through the matching end element and builds a `org.w3c.dom.Element`. The `DocumentBuilderFactory` in `XmlDomUtil.newDocument()` is hardened against XXE by setting `ACCESS_EXTERNAL_DTD` and `ACCESS_EXTERNAL_SCHEMA` to empty strings.

**Step 4: Modify `MetaschemaXmlReader.readModelInstances()`**

In `readModelInstances()` (around line 297-312), replace the skip logic:

```java
// Before: XmlEventUtil.skipElement(reader);
// After:
if (definition.getAnyInstance() != null) {
  // capture into DOM elements
  List<Element> captured = new ArrayList<>();
  while (!reader.peek().isEndElement()) {
    XmlEventUtil.skipWhitespace(reader);
    if (!reader.peek().isEndElement()) {
      captured.add(staxToDom(reader));
      XmlEventUtil.skipWhitespace(reader);
    }
  }
  if (!captured.isEmpty()) {
    XmlAnyContent anyContent = new XmlAnyContent(captured);
    // set on the bound object via the any instance
    anyInstance.setValue(targetObject, anyContent);
  }
} else {
  // existing skip behavior
  XmlEventUtil.skipElement(reader);
}
```

Adapt this pseudocode to match the actual reader API and field access patterns.

**Error handling and validation for captured content:**

- **Malformed content:** If StAX-to-DOM conversion fails (e.g., `XMLStreamException`), propagate the error to fail the parse with a clear message indicating the location and nature of the failure. Do not silently skip malformed any content.
- **Content treatment:** Captured any content is treated as opaque — no schema or constraint validation is applied to the captured DOM/JSON nodes. The content is preserved exactly as received for round-trip fidelity.
- **Resource limits:** The StAX parser's existing resource limits (entity expansion, max attributes) apply during capture. No additional per-capture limits are imposed, as the StAX layer already enforces configurable bounds.
- **Error surfacing:** Parse failures in any content paths produce `IOException` with descriptive messages including the element name and namespace, consistent with how other parse failures are reported in `MetaschemaXmlReader` and `MetaschemaJsonReader`.
- **JSON capture path:** The JSON reader uses `parser.readValueAsTree()` (Jackson `TreeNode`) for value capture, which inherits the parser's configured limits (max string length, max nesting depth). Capture failures propagate as `IOException`.

**Step 5: Run test to verify it passes**

Run: `mvn -pl databind test -Dtest=AnyXmlRoundTripTest -q`
Expected: PASS

**Step 6: Run full test suite**

Run: `mvn -pl databind test -q`
Expected: All tests pass (existing behavior for assemblies without `<any/>` unchanged)

**Step 7: Commit**

```text
feat: capture unmodeled XML content into XmlAnyContent

MetaschemaXmlReader now captures unknown child elements into
XmlAnyContent when the assembly definition has <any/>.
```

---

### Task 11: XML Writing — Serialize Captured Content [COMPLETE]

**Files:**
- Modify: `databind/src/main/java/dev/metaschema/databind/io/xml/MetaschemaXmlWriter.java` (around lines 203-210)

**Step 1: Extend round-trip test from Task 11**

Add test assertions that verify writing a bound object with `XmlAnyContent` produces XML that includes the captured elements in the correct position (after known model instances).

**Step 2: Run test to verify it fails**

Expected: FAIL (captured content not written)

**Step 3: Modify `MetaschemaXmlWriter`**

In `writeAssemblyModel()` (around line 203-210), after writing all model instances, check for any content:

```java
// After the model instance loop:
IBoundInstanceModelAny anyInstance = definition.getAnyInstance();
if (anyInstance != null) {
  IAnyContent anyContent = anyInstance.getValue(parentItem);
  if (anyContent instanceof XmlAnyContent) {
    for (Element element : ((XmlAnyContent) anyContent).getElements()) {
      // write DOM element to XMLStreamWriter
      writeDomElement(element, writer);
    }
  }
}
```

Add a `writeDomElement()` utility that walks a DOM `Element` tree and writes to `XMLStreamWriter2`.

**Step 4: Run tests to verify they pass**

Run: `mvn -pl databind test -Dtest=AnyXmlRoundTripTest -q`
Expected: PASS

**Step 5: Commit**

```text
feat: serialize XmlAnyContent back to XML output

MetaschemaXmlWriter writes captured DOM elements after all
known model instances for assemblies with <any/>.
```

---

### Task 12: JSON Parsing — Capture Unmodeled Properties [COMPLETE]

**Files:**
- Modify: `databind/src/main/java/dev/metaschema/databind/io/json/MetaschemaJsonReader.java` (lines ~658-668)

**Step 1: Write round-trip test**

Create: `databind/src/test/java/dev/metaschema/databind/io/json/AnyJsonRoundTripTest.java`

Create a test with a bound class containing `@BoundAny`. Create JSON with extra properties (string, number, object, array values). Verify:
1. Parsing captures unmatched properties into `JsonAnyContent`
2. Known properties are still parsed correctly
3. The captured `ObjectNode` contains the right property names and values

**Step 2: Run test to verify it fails**

Expected: FAIL (properties skipped instead of captured)

**Step 3: Modify `MetaschemaJsonReader`**

In `PropertyBodyHandler.accept()` (around lines 658-668), replace the skip logic:

```java
// Before: JsonUtil.skipNextValue(parser, resource);
// After:
if (definition.getAnyInstance() != null) {
  // capture into ObjectNode
  if (anyNode == null) {
    anyNode = parser.getCodec().createObjectNode();
  }
  JsonUtil.assertAndAdvance(parser, resource, JsonToken.FIELD_NAME);
  JsonNode value = parser.readValueAsTree();
  anyNode.set(propertyName, value);
} else {
  // existing skip behavior
  JsonUtil.assertAndAdvance(parser, resource, JsonToken.FIELD_NAME);
  JsonUtil.skipNextValue(parser, resource);
}
```

After the property loop, if `anyNode` is non-null and non-empty:

```java
if (anyNode != null && !anyNode.isEmpty()) {
  JsonAnyContent anyContent = new JsonAnyContent(anyNode);
  anyInstance.setValue(parent, anyContent);
}
```

**Step 4: Run test to verify it passes**

Run: `mvn -pl databind test -Dtest=AnyJsonRoundTripTest -q`
Expected: PASS

**Step 5: Run full test suite**

Run: `mvn -pl databind test -q`
Expected: All tests pass

**Step 6: Commit**

```text
feat: capture unmodeled JSON properties into JsonAnyContent

MetaschemaJsonReader now captures unmatched properties into
JsonAnyContent when the assembly definition has <any/>.
```

---

### Task 13: JSON Writing — Serialize Captured Content [COMPLETE]

**Files:**
- Modify: `databind/src/main/java/dev/metaschema/databind/io/json/MetaschemaJsonWriter.java` (around lines 259-273)

**Step 1: Extend round-trip test from Task 13**

Add write-back assertions: serialize the bound object to JSON and verify the extra properties appear.

**Step 2: Run test to verify it fails**

Expected: FAIL (captured properties not written)

**Step 3: Modify `MetaschemaJsonWriter`**

In `writeObjectProperties()` (around line 259-273), after writing all known properties, check for any content:

```java
// After the property loop:
IBoundInstanceModelAny anyInstance = definition.getAnyInstance();
if (anyInstance != null) {
  IAnyContent anyContent = anyInstance.getValue(parent);
  if (anyContent instanceof JsonAnyContent) {
    ObjectNode props = ((JsonAnyContent) anyContent).getProperties();
    Iterator<Map.Entry<String, JsonNode>> fields = props.fields();
    while (fields.hasNext()) {
      Map.Entry<String, JsonNode> entry = fields.next();
      generator.writeFieldName(entry.getKey());
      generator.writeTree(entry.getValue());
    }
  }
}
```

**Step 4: Run tests to verify they pass**

Run: `mvn -pl databind test -Dtest=AnyJsonRoundTripTest -q`
Expected: PASS

**Step 5: Commit**

```text
feat: serialize JsonAnyContent back to JSON output

MetaschemaJsonWriter writes captured ObjectNode properties after
all known properties for assemblies with <any/>.
```

---

### Task 14: JSON Value-Key Interaction Tests [COMPLETE]

**Files:**
- Create: `databind/src/test/java/dev/metaschema/databind/io/json/AnyJsonValueKeyTest.java`

**Step 1: Write tests for value-key interaction**

Create a bound class where:
1. An assembly uses `json-key` flag (properties are keyed by flag value)
2. The assembly also has `@BoundAny`
3. JSON input contains both keyed known instances and extra unknown properties

Verify:
- Known instances keyed by value-key are correctly parsed
- Extra properties (not matching any key) are captured in `JsonAnyContent`
- No known instances are incorrectly captured as "any" content
- No "any" content is incorrectly matched to known instances

**Step 2: Run tests**

Run: `mvn -pl databind test -Dtest=AnyJsonValueKeyTest -q`
Expected: PASS (if the property-matching logic correctly resolves before falling through to any capture)

If tests fail, fix the capture logic to properly check value-key resolution before capturing as "any".

**Step 3: Commit**

```text
test: verify any content capture with json-value-key flags

Ensures unmodeled content capture correctly distinguishes between
value-key-matched properties and truly unmodeled properties.
```

---

### Task 14A: Security Hardening for Captured Content [COMPLETE]

**Files:**
- Modify: `databind/src/main/java/dev/metaschema/databind/io/xml/XmlDomUtil.java`

**Step 1: Harden `XmlDomUtil.newDocument()` against XXE**

The `DocumentBuilderFactory` used to create DOM documents for captured any content must deny external entity resolution:

```java
DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
dbf.setNamespaceAware(true);
dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
```

This prevents XXE attacks (billion laughs, external entity injection) when building DOM trees from StAX events. Although `newDocument()` only creates blank documents (not parsing external input), this follows defense-in-depth principles.

**Step 2: Verify existing StAX parser protections**

The StAX `XMLEventReader` used by `MetaschemaXmlReader` already inherits the JVM's default entity expansion limits and security features. Verify that:
- External entity resolution is controlled by the StAX factory configuration
- Entity expansion limits are enforced by the JVM defaults (`jdk.xml.entityExpansionLimit`)
- No additional `DocumentBuilder.parse()` calls are introduced (all DOM construction uses `newDocument()` + manual population)

**Step 3: Verify Jackson parser protections**

Jackson's `JsonParser` used in `MetaschemaJsonReader` enforces:
- Maximum string length (`StreamReadConstraints`)
- Maximum nesting depth (`StreamReadConstraints`)
- Maximum number length

The `parser.readValueAsTree()` call for any content capture inherits these limits.

**Step 4: Run existing tests**

Run: `mvn -pl databind test -q`
Expected: All tests pass (hardening should not change behavior)

**Step 5: Commit**

```text
fix: harden XmlDomUtil against XXE in DocumentBuilderFactory

Sets ACCESS_EXTERNAL_DTD and ACCESS_EXTERNAL_SCHEMA to empty
strings as defense-in-depth for DOM document creation.
```

---

---

## Phase 3: Schema Generation [COMPLETE]

### Task 15: XML Schema Generation for `any` [COMPLETE]

**Files:**
- Modify: `schemagen/src/main/java/dev/metaschema/schemagen/xml/impl/schematype/XmlComplexTypeAssemblyDefinition.java` (lines ~57-77, 92-151)

**Step 1: Write test**

Create: `schemagen/src/test/java/dev/metaschema/schemagen/xml/AnyXmlSchemaGenerationTest.java`

Load a Metaschema module with `<any/>` in an assembly. Generate XML Schema. Verify the output contains:

```xml
<xs:any namespace="##other" processContents="lax"
       minOccurs="0" maxOccurs="unbounded"/>
```

after the assembly's element declarations.

**Step 2: Run test to verify it fails**

Run: `mvn -pl schemagen test -Dtest=AnyXmlSchemaGenerationTest -q`
Expected: FAIL

**Step 3: Update `generateTypeBody()` or `generateModelInstance()`**

In `XmlComplexTypeAssemblyDefinition`, after iterating model instances in the `<xs:sequence>`, check for `IAnyInstance`:

```java
IAnyInstance anyInstance = definition.getAnyInstance();
if (anyInstance != null) {
  state.writeStartElement(XmlSchemaGenerator.PREFIX_XML_SCHEMA, "any", ...);
  state.writeAttribute("namespace", "##other");
  state.writeAttribute("processContents", "lax");
  state.writeAttribute("minOccurs", "0");
  state.writeAttribute("maxOccurs", "unbounded");
  state.writeEndElement();
}
```

Or handle it in `generateModelInstance()` with a `case ANY:` in the switch statement, depending on whether `IAnyInstance` appears in the model instances collection or is accessed separately via `getAnyInstance()`.

**Step 4: Run test to verify it passes**

Run: `mvn -pl schemagen test -Dtest=AnyXmlSchemaGenerationTest -q`
Expected: PASS

**Step 5: Commit**

```text
feat: generate xs:any in XML Schema for <any/> declarations

XML Schema generator emits xs:any with namespace="##other"
and processContents="lax" for assemblies with <any/>.
```

---

### Task 16: JSON Schema Generation for `any` [COMPLETE]

**Files:**
- Modify: `schemagen/src/main/java/dev/metaschema/schemagen/json/impl/JsonSchemaDefinitionAssembly.java`
- Possibly modify: `schemagen/src/main/java/dev/metaschema/schemagen/json/impl/JsonSchemaHelper.java`

**Step 1: Write test**

Create: `schemagen/src/test/java/dev/metaschema/schemagen/json/AnyJsonSchemaGenerationTest.java`

Load a Metaschema module with `<any/>`. Generate JSON Schema. Verify the assembly's schema object contains `"additionalProperties": true`.

**Step 2: Run test to verify it fails**

Run: `mvn -pl schemagen test -Dtest=AnyJsonSchemaGenerationTest -q`
Expected: FAIL

**Step 3: Update JSON schema generation**

In `JsonSchemaDefinitionAssembly` or `JsonSchemaHelper`, when building the assembly's JSON Schema object, check for `IAnyInstance`:

```java
if (definition.getAnyInstance() != null) {
  objectNode.put("additionalProperties", true);
}
```

Also ensure `IAnyInstance` is filtered out in `buildModelProperties()` (line 292 of `JsonSchemaHelper.java`) similar to how `IChoiceInstance` is filtered:

```java
.filter(instance -> !(instance instanceof IChoiceInstance))
.filter(instance -> !(instance instanceof IAnyInstance))
```

**Step 4: Run test to verify it passes**

Run: `mvn -pl schemagen test -Dtest=AnyJsonSchemaGenerationTest -q`
Expected: PASS

**Step 5: Commit**

```text
feat: generate additionalProperties in JSON Schema for <any/>

JSON Schema generator sets additionalProperties: true for
assemblies with <any/> declarations.
```

---

---

## Phase 4: Final Verification and PR [COMPLETE]

### Task 17: Javadoc Completeness [COMPLETE]

Ensure 100% Javadoc coverage on all new `public`/`protected` members:

- `IAnyContent` — interface and `isEmpty()` method
- `IAnyInstance` — interface, default methods, `accept()` method
- `@BoundAny` — annotation type Javadoc
- `IBoundInstanceModelAny` — interface and all methods
- `InstanceModelAny` — implementation class and constructor/methods
- `XmlAnyContent` — class, constructor, `getElements()`
- `JsonAnyContent` — class, constructor, `getProperties()`
- `XmlDomUtil` — class, `staxToElement()`, `elementToStax()`, and all helper methods
- `ModelType.ANY` — enum constant Javadoc

Document namespace behavior for XML any content (captured elements preserve their original namespace URIs and prefixes). Document that JSON any content captures unmatched properties as Jackson `ObjectNode` entries.

Run: `mvn -pl core,databind checkstyle:check -q`
Expected: No Javadoc violations in new code

---

### Task 18: Full CI Build and PR Creation [COMPLETE]

**Step 1: Run full CI build**

Run: `mvn clean install -PCI -Prelease` (in the worktree)
Expected: BUILD SUCCESS with all checks passing

**Step 2: Create PR**

Push to personal fork (`me` remote), create PR targeting `develop` branch.
Reference: Issue #220
Title: `feat: support any in Java binding annotations (#220)`

Include reference to companion specification PR [metaschema-framework/metaschema#171](https://github.com/metaschema-framework/metaschema/pull/171) in the PR description.

---

## Files Changed Summary

### Core Module (`core/`)

| File | Change Type |
|------|-------------|
| `core/.../model/IAnyContent.java` | Create |
| `core/.../model/IAnyInstance.java` | Create |
| `core/.../model/ModelType.java` | Modify (add `ANY`) |
| `core/.../model/IContainerModelAssemblySupport.java` | Modify (add type param, `getAnyInstance()`) |
| `core/.../model/impl/DefaultContainerModelAssemblySupport.java` | Modify (add field, constructor param) |
| `core/.../model/DefaultAssemblyModelBuilder.java` | Modify (add `append`, field) |
| `core/.../model/IModelElementVisitor.java` | Modify (add `visitAny`) |
| All `IContainerModelAssemblySupport` implementors | Modify (type parameter) |
| All `IModelElementVisitor` implementors | Modify (add `visitAny`) |

### Databind Module (`databind/`)

| File | Change Type |
|------|-------------|
| `databind/.../model/annotations/BoundAny.java` | Create |
| `databind/.../model/IBoundInstanceModelAny.java` | Create |
| `databind/.../model/impl/InstanceModelAny.java` | Create |
| `databind/.../model/impl/AssemblyModelGenerator.java` | Modify (scan `@BoundAny`) |
| `databind/.../model/metaschema/impl/AssemblyModelGenerator.java` | Modify (process `Any` binding) |
| `databind/.../model/metaschema/impl/ChoiceModelGenerator.java` | Modify (process `Any` binding) |
| `databind/.../io/xml/XmlAnyContent.java` | Create |
| `databind/.../io/xml/XmlDomUtil.java` | Create (StAX-to-DOM conversion, XXE-hardened) |
| `databind/.../io/xml/MetaschemaXmlReader.java` | Modify (capture content) |
| `databind/.../io/xml/MetaschemaXmlWriter.java` | Modify (write content) |
| `databind/.../io/json/JsonAnyContent.java` | Create |
| `databind/.../io/json/MetaschemaJsonReader.java` | Modify (capture properties) |
| `databind/.../io/json/MetaschemaJsonWriter.java` | Modify (write properties) |

### Schema Generation Module (`schemagen/`)

| File | Change Type |
|------|-------------|
| `schemagen/.../xml/impl/schematype/XmlComplexTypeAssemblyDefinition.java` | Modify |
| `schemagen/.../json/impl/JsonSchemaDefinitionAssembly.java` | Modify |
| `schemagen/.../json/impl/JsonSchemaHelper.java` | Modify (filter `IAnyInstance`) |

### Test Files

| File | Change Type |
|------|-------------|
| `core/.../model/DefaultAssemblyModelBuilderTest.java` | Create |
| `databind/.../model/metaschema/AnyInstanceLoadingTest.java` | Create |
| `databind/.../io/xml/XmlAnyContentTest.java` | Create |
| `databind/.../io/xml/AnyXmlRoundTripTest.java` | Create |
| `databind/.../io/json/JsonAnyContentTest.java` | Create |
| `databind/.../io/json/AnyJsonRoundTripTest.java` | Create |
| `databind/.../io/json/AnyJsonValueKeyTest.java` | Create |
| `schemagen/.../xml/AnyXmlSchemaGenerationTest.java` | Create |
| `schemagen/.../json/AnyJsonSchemaGenerationTest.java` | Create |
