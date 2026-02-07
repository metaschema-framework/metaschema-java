/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.io.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.jupiter.api.Test;

class JsonAnyContentTest {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Test
  void testEmptyObjectNodeIsEmpty() {
    ObjectNode node = MAPPER.createObjectNode();
    JsonAnyContent content = new JsonAnyContent(node);

    assertTrue(content.isEmpty(), "Empty ObjectNode should report isEmpty() as true");
  }

  @Test
  void testEmptyObjectNodeGetProperties() {
    ObjectNode node = MAPPER.createObjectNode();
    JsonAnyContent content = new JsonAnyContent(node);

    assertNotNull(content.getProperties());
    assertSame(node, content.getProperties());
    assertEquals(0, content.getProperties().size());
  }

  @Test
  void testNonEmptyObjectNodeIsNotEmpty() {
    ObjectNode node = MAPPER.createObjectNode();
    node.put("key", "value");
    JsonAnyContent content = new JsonAnyContent(node);

    assertFalse(content.isEmpty(), "Non-empty ObjectNode should report isEmpty() as false");
  }

  @Test
  void testGetPropertiesReturnsSameNode() {
    ObjectNode node = MAPPER.createObjectNode();
    node.put("key", "value");
    JsonAnyContent content = new JsonAnyContent(node);

    assertSame(node, content.getProperties(), "getProperties() should return the same ObjectNode instance");
  }

  @Test
  void testStringProperty() {
    ObjectNode node = MAPPER.createObjectNode();
    node.put("name", "test-value");
    JsonAnyContent content = new JsonAnyContent(node);

    assertFalse(content.isEmpty());
    assertEquals("test-value", content.getProperties().get("name").asText());
  }

  @Test
  void testNumericProperty() {
    ObjectNode node = MAPPER.createObjectNode();
    node.put("count", 42);
    JsonAnyContent content = new JsonAnyContent(node);

    assertFalse(content.isEmpty());
    assertEquals(42, content.getProperties().get("count").asInt());
  }

  @Test
  void testBooleanProperty() {
    ObjectNode node = MAPPER.createObjectNode();
    node.put("active", true);
    JsonAnyContent content = new JsonAnyContent(node);

    assertFalse(content.isEmpty());
    assertTrue(content.getProperties().get("active").asBoolean());
  }

  @Test
  void testArrayProperty() {
    ObjectNode node = MAPPER.createObjectNode();
    node.putArray("items").add("a").add("b").add("c");
    JsonAnyContent content = new JsonAnyContent(node);

    assertFalse(content.isEmpty());
    assertTrue(content.getProperties().get("items").isArray());
    assertEquals(3, content.getProperties().get("items").size());
  }

  @Test
  void testNestedObjectProperty() {
    ObjectNode node = MAPPER.createObjectNode();
    ObjectNode nested = MAPPER.createObjectNode();
    nested.put("inner-key", "inner-value");
    node.set("nested", nested);
    JsonAnyContent content = new JsonAnyContent(node);

    assertFalse(content.isEmpty());
    assertTrue(content.getProperties().get("nested").isObject());
    assertEquals("inner-value", content.getProperties().get("nested").get("inner-key").asText());
  }

  @Test
  void testMultipleProperties() {
    ObjectNode node = MAPPER.createObjectNode();
    node.put("string-prop", "hello");
    node.put("number-prop", 99);
    node.put("bool-prop", false);
    JsonAnyContent content = new JsonAnyContent(node);

    assertFalse(content.isEmpty());
    assertEquals(3, content.getProperties().size());
    assertEquals("hello", content.getProperties().get("string-prop").asText());
    assertEquals(99, content.getProperties().get("number-prop").asInt());
    assertFalse(content.getProperties().get("bool-prop").asBoolean());
  }
}
