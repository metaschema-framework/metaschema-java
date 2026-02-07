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
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.Map;

import dev.metaschema.core.model.IAnyContent;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.IBindingContext;
import dev.metaschema.databind.codegen.AbstractMetaschemaTest;
import dev.metaschema.databind.model.IBoundDefinitionModelAssembly;
import dev.metaschema.databind.model.test.AnyWithJsonKeyAssembly;

/**
 * Tests verifying that {@code @BoundAny} content capture correctly
 * distinguishes between properties matched by json-key flags and truly
 * unmodeled properties.
 *
 * <p>
 * When an assembly uses json-key with {@code inJson = KEYED}, the group name is
 * a known property in the parent's JSON properties map. This test verifies that
 * such properties are resolved as known instances and NOT captured as "any"
 * content, while genuinely unknown properties are correctly captured.
 */
class AnyJsonValueKeyTest
    extends AbstractMetaschemaTest {

  private static final URI SOURCE = ObjectUtils.notNull(URI.create("https://example.com/test"));

  private IBoundDefinitionModelAssembly getAssemblyDefinition() throws IOException {
    IBindingContext bindingContext = newBindingContext();
    return ObjectUtils.requireNonNull(
        (IBoundDefinitionModelAssembly) bindingContext.getBoundDefinitionForClass(
            AnyWithJsonKeyAssembly.class));
  }

  private AnyWithJsonKeyAssembly readJson(String json) throws IOException {
    IBoundDefinitionModelAssembly assembly = getAssemblyDefinition();
    JsonFactory factory = JsonFactoryFactory.instance();
    try (JsonParser parser = factory.createParser(new StringReader(json))) {
      MetaschemaJsonReader reader = new MetaschemaJsonReader(parser, SOURCE);
      return reader.readObjectRoot(
          assembly,
          ObjectUtils.requireNonNull(assembly.getRootJsonName()));
    }
  }

  @Test
  void testKeyedFieldParsedCorrectlyWithNoExtra() throws IOException {
    // JSON with a known-field and keyed-fields, but no unknown properties.
    // The "keyed-fields" property is a known json-key group name.
    String json = "{\"any-json-key-assembly\":{"
        + "\"known-field\":\"hello\","
        + "\"keyed-fields\":{\"key1\":\"value1\",\"key2\":\"value2\"}"
        + "}}";

    AnyWithJsonKeyAssembly result = readJson(json);

    // Verify known field is correctly parsed
    assertEquals("hello", result.getKnownField());

    // Verify keyed fields are correctly parsed
    Map<String, AnyWithJsonKeyAssembly.KeyedField> keyed = result.getKeyedField();
    assertNotNull(keyed, "Keyed field map should not be null");
    assertEquals(2, keyed.size(), "Should have 2 keyed entries");

    AnyWithJsonKeyAssembly.KeyedField entry1 = keyed.get("key1");
    assertNotNull(entry1, "Entry 'key1' should exist");
    assertEquals("key1", entry1.getId());
    assertEquals("value1", entry1.getValue());

    AnyWithJsonKeyAssembly.KeyedField entry2 = keyed.get("key2");
    assertNotNull(entry2, "Entry 'key2' should exist");
    assertEquals("key2", entry2.getId());
    assertEquals("value2", entry2.getValue());

    // No unknown properties, so any should be null
    assertNull(result.getAny(),
        "Any content should be null when all properties are known");
  }

  @Test
  void testKeyedFieldWithExtraPropertyGoesToAny() throws IOException {
    // JSON with known-field, keyed-fields, AND an unknown "extra" property.
    // The "keyed-fields" group name should be matched as a known property.
    // The "extra" property should be captured as any content.
    String json = "{\"any-json-key-assembly\":{"
        + "\"known-field\":\"hello\","
        + "\"keyed-fields\":{\"item1\":\"val1\"}"
        + ",\"extra-prop\":\"extra-value\""
        + "}}";

    AnyWithJsonKeyAssembly result = readJson(json);

    // Known field correctly parsed
    assertEquals("hello", result.getKnownField());

    // Keyed field correctly parsed
    Map<String, AnyWithJsonKeyAssembly.KeyedField> keyed = result.getKeyedField();
    assertNotNull(keyed);
    assertEquals(1, keyed.size());
    assertNotNull(keyed.get("item1"));
    assertEquals("val1", keyed.get("item1").getValue());

    // Unknown property captured in any
    IAnyContent anyContent = result.getAny();
    assertNotNull(anyContent, "Any content should capture the unknown property");
    assertInstanceOf(JsonAnyContent.class, anyContent);

    JsonAnyContent jsonAny = (JsonAnyContent) anyContent;
    assertFalse(jsonAny.isEmpty());
    ObjectNode props = jsonAny.getProperties();
    assertNotNull(props.get("extra-prop"));
    assertEquals("extra-value", props.get("extra-prop").asText());

    // The keyed-fields group name must NOT appear in any content
    assertNull(props.get("keyed-fields"),
        "The json-key group name should not be captured as any content");
    // The known-field must NOT appear in any content
    assertNull(props.get("known-field"),
        "The known field should not be captured as any content");
  }

  @Test
  void testOnlyUnknownPropertiesCapturedAsAny() throws IOException {
    // JSON with ONLY unknown properties (no known-field, no keyed-fields).
    // All properties should go to any.
    String json = "{\"any-json-key-assembly\":{"
        + "\"unknown1\":\"value1\","
        + "\"unknown2\":42"
        + "}}";

    AnyWithJsonKeyAssembly result = readJson(json);

    // Known fields are null
    assertNull(result.getKnownField());
    assertTrue(result.getKeyedField() == null || result.getKeyedField().isEmpty());

    // Unknown properties captured in any
    IAnyContent anyContent = result.getAny();
    assertNotNull(anyContent, "Any content should capture unknown properties");
    assertInstanceOf(JsonAnyContent.class, anyContent);

    JsonAnyContent jsonAny = (JsonAnyContent) anyContent;
    ObjectNode props = jsonAny.getProperties();
    assertEquals(2, props.size());
    assertEquals("value1", props.get("unknown1").asText());
    assertEquals(42, props.get("unknown2").asInt());
  }

  @Test
  void testMultipleUnknownWithKeyedAndKnown() throws IOException {
    // Comprehensive test with all three types of properties:
    // known-field, keyed-fields, and multiple unknown properties.
    String json = "{\"any-json-key-assembly\":{"
        + "\"known-field\":\"test\","
        + "\"keyed-fields\":{\"a\":\"alpha\",\"b\":\"beta\"}"
        + ",\"extra-string\":\"foo\""
        + ",\"extra-object\":{\"nested\":true}"
        + ",\"extra-array\":[1,2,3]"
        + "}}";

    AnyWithJsonKeyAssembly result = readJson(json);

    // Verify known field
    assertEquals("test", result.getKnownField());

    // Verify keyed fields
    Map<String, AnyWithJsonKeyAssembly.KeyedField> keyed = result.getKeyedField();
    assertNotNull(keyed);
    assertEquals(2, keyed.size());
    assertEquals("alpha", keyed.get("a").getValue());
    assertEquals("beta", keyed.get("b").getValue());

    // Verify any content captures only the unknown properties
    IAnyContent anyContent = result.getAny();
    assertNotNull(anyContent);
    assertInstanceOf(JsonAnyContent.class, anyContent);

    JsonAnyContent jsonAny = (JsonAnyContent) anyContent;
    ObjectNode props = jsonAny.getProperties();
    assertEquals(3, props.size(),
        "Should capture exactly 3 unknown properties");

    // Verify each unknown property
    assertEquals("foo", props.get("extra-string").asText());
    assertTrue(props.get("extra-object").isObject());
    assertTrue(props.get("extra-object").get("nested").asBoolean());
    assertTrue(props.get("extra-array").isArray());
    assertEquals(3, props.get("extra-array").size());

    // Verify known properties are NOT in any content
    assertNull(props.get("known-field"));
    assertNull(props.get("keyed-fields"));
  }

  @Test
  void testPropertyNameMatchingKnownFieldGoesToField() throws IOException {
    // Verify that a JSON property whose name exactly matches a known field's
    // use-name is directed to the field, not to any content.
    String json = "{\"any-json-key-assembly\":{"
        + "\"known-field\":\"matched-value\""
        + "}}";

    AnyWithJsonKeyAssembly result = readJson(json);

    assertEquals("matched-value", result.getKnownField());
    assertNull(result.getAny(),
        "No unknown properties means any should be null");
  }
}
