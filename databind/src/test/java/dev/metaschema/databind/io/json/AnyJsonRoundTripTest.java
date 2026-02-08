/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.io.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;

import dev.metaschema.core.model.IAnyContent;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.IBindingContext;
import dev.metaschema.databind.codegen.AbstractMetaschemaTest;
import dev.metaschema.databind.model.IBoundDefinitionModelAssembly;
import dev.metaschema.databind.model.test.AnyAssembly;

class AnyJsonRoundTripTest
    extends AbstractMetaschemaTest {

  private static final URI SOURCE = ObjectUtils.notNull(URI.create("https://example.com/test"));

  private IBoundDefinitionModelAssembly getAssemblyDefinition() throws IOException {
    IBindingContext bindingContext = newBindingContext();
    return ObjectUtils.requireNonNull(
        (IBoundDefinitionModelAssembly) bindingContext.getBoundDefinitionForClass(AnyAssembly.class));
  }

  private AnyAssembly readJson(String json) throws IOException {
    IBoundDefinitionModelAssembly assembly = getAssemblyDefinition();
    JsonFactory factory = JsonFactoryFactory.instance();
    try (JsonParser parser = factory.createParser(new StringReader(json))) {
      MetaschemaJsonReader reader = new MetaschemaJsonReader(parser, SOURCE);
      return reader.readObjectRoot(
          assembly,
          ObjectUtils.requireNonNull(assembly.getRootJsonName()));
    }
  }

  private String writeJson(AnyAssembly obj) throws IOException {
    IBoundDefinitionModelAssembly assembly = getAssemblyDefinition();
    StringWriter sw = new StringWriter();
    JsonFactory factory = JsonFactoryFactory.instance();
    try (JsonGenerator generator = factory.createGenerator(sw)) {
      generator.writeStartObject();
      generator.writeFieldName(ObjectUtils.requireNonNull(assembly.getRootJsonName()));

      MetaschemaJsonWriter writer = new MetaschemaJsonWriter(generator);
      writer.write(assembly, obj);

      generator.writeEndObject();
    }
    return sw.toString();
  }

  @Test
  void testReadWithNoExtraProperties() throws IOException {
    String json = "{\"any-assembly\":{\"known-field\":\"hello\"}}";
    AnyAssembly result = readJson(json);

    assertEquals("hello", result.getKnownField());
    assertNull(result.getAny(), "Any content should be null when no extra properties");
  }

  @Test
  void testReadCapturesExtraStringProperty() throws IOException {
    String json = "{\"any-assembly\":{\"known-field\":\"hello\",\"extra-string\":\"value\"}}";
    AnyAssembly result = readJson(json);

    assertEquals("hello", result.getKnownField());

    IAnyContent anyContent = result.getAny();
    assertNotNull(anyContent, "Any content should capture extra properties");
    assertInstanceOf(JsonAnyContent.class, anyContent);

    JsonAnyContent jsonAny = (JsonAnyContent) anyContent;
    assertFalse(jsonAny.isEmpty());
    ObjectNode props = jsonAny.getProperties();
    assertNotNull(props.get("extra-string"));
    assertEquals("value", props.get("extra-string").asText());
  }

  @Test
  void testReadCapturesMultipleExtraProperties() throws IOException {
    String json = "{\"any-assembly\":{\"known-field\":\"hello\""
        + ",\"extra-string\":\"value\""
        + ",\"extra-number\":42"
        + ",\"extra-object\":{\"nested\":\"data\"}"
        + ",\"extra-array\":[1,2,3]"
        + "}}";
    AnyAssembly result = readJson(json);

    assertEquals("hello", result.getKnownField());

    IAnyContent anyContent = result.getAny();
    assertNotNull(anyContent, "Any content should capture extra properties");
    assertInstanceOf(JsonAnyContent.class, anyContent);

    JsonAnyContent jsonAny = (JsonAnyContent) anyContent;
    ObjectNode props = jsonAny.getProperties();

    // Verify string property
    assertNotNull(props.get("extra-string"));
    assertEquals("value", props.get("extra-string").asText());

    // Verify number property
    assertNotNull(props.get("extra-number"));
    assertEquals(42, props.get("extra-number").asInt());

    // Verify object property
    JsonNode objectProp = props.get("extra-object");
    assertNotNull(objectProp);
    assertTrue(objectProp.isObject());
    assertEquals("data", objectProp.get("nested").asText());

    // Verify array property
    JsonNode arrayProp = props.get("extra-array");
    assertNotNull(arrayProp);
    assertTrue(arrayProp.isArray());
    assertEquals(3, arrayProp.size());
  }

  @Test
  void testWriteSerializesAnyContent() throws IOException {
    AnyAssembly obj = new AnyAssembly();
    obj.setKnownField("hello");

    ObjectMapper mapper = new ObjectMapper();
    ObjectNode extraProps = mapper.createObjectNode();
    extraProps.put("extra-string", "value");
    extraProps.put("extra-number", 42);
    obj.setAny(new JsonAnyContent(extraProps));

    String json = writeJson(obj);
    assertNotNull(json);

    // Parse the output back and verify the extra properties are present
    JsonNode root = mapper.readTree(json);
    JsonNode assembly = root.get("any-assembly");
    assertNotNull(assembly, "Root wrapper should exist");

    assertEquals("hello", assembly.get("known-field").asText());
    assertEquals("value", assembly.get("extra-string").asText());
    assertEquals(42, assembly.get("extra-number").asInt());
  }

  @Test
  void testWriteWithNullAnyContent() throws IOException {
    AnyAssembly obj = new AnyAssembly();
    obj.setKnownField("hello");
    // any is null

    String json = writeJson(obj);
    assertNotNull(json);

    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(json);
    JsonNode assembly = root.get("any-assembly");
    assertNotNull(assembly);
    assertEquals("hello", assembly.get("known-field").asText());

    // Should only have the known-field, no extra properties
    assertEquals(1, assembly.size(), "Should only have known-field");
  }

  @Test
  void testWriteWithEmptyAnyContent() throws IOException {
    AnyAssembly obj = new AnyAssembly();
    obj.setKnownField("hello");

    ObjectMapper mapper = new ObjectMapper();
    ObjectNode emptyProps = mapper.createObjectNode();
    obj.setAny(new JsonAnyContent(emptyProps));

    String json = writeJson(obj);
    assertNotNull(json);

    JsonNode root = mapper.readTree(json);
    JsonNode assembly = root.get("any-assembly");
    assertNotNull(assembly);

    // Empty any content should not produce extra fields
    assertEquals(1, assembly.size(), "Empty any content should not add extra fields");
  }

  @Test
  void testRoundTrip() throws IOException {
    String json = "{\"any-assembly\":{\"known-field\":\"hello\""
        + ",\"extra-string\":\"value\""
        + ",\"extra-number\":42"
        + "}}";

    // Read
    AnyAssembly result = readJson(json);
    assertEquals("hello", result.getKnownField());
    assertNotNull(result.getAny());

    // Write back
    String output = writeJson(result);

    // Re-read
    AnyAssembly result2 = readJson(output);

    // Verify the round trip preserved everything
    assertEquals("hello", result2.getKnownField());
    assertNotNull(result2.getAny());
    assertInstanceOf(JsonAnyContent.class, result2.getAny());

    JsonAnyContent jsonAny = (JsonAnyContent) result2.getAny();
    ObjectNode props = jsonAny.getProperties();
    assertEquals("value", props.get("extra-string").asText());
    assertEquals(42, props.get("extra-number").asInt());
  }

  @Test
  void testRoundTripWithNestedObject() throws IOException {
    String json = "{\"any-assembly\":{\"known-field\":\"test\""
        + ",\"complex\":{\"a\":1,\"b\":{\"c\":\"deep\"}}"
        + "}}";

    // Read
    AnyAssembly result = readJson(json);
    assertEquals("test", result.getKnownField());
    assertNotNull(result.getAny());

    // Write back
    String output = writeJson(result);

    // Re-read
    AnyAssembly result2 = readJson(output);
    assertNotNull(result2.getAny());

    JsonAnyContent jsonAny = (JsonAnyContent) result2.getAny();
    ObjectNode props = jsonAny.getProperties();
    JsonNode complex = props.get("complex");
    assertNotNull(complex);
    assertEquals(1, complex.get("a").asInt());
    assertEquals("deep", complex.get("b").get("c").asText());
  }
}
