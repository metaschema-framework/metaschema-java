/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.testing.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.DefaultBindingContext;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.io.Format;
import gov.nist.secauto.metaschema.databind.io.IDeserializer;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelAssembly;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelComplex;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceFlag;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelField;
import gov.nist.secauto.metaschema.databind.testing.model.RootAssemblyWithFields.JsonKeyField;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
import java.util.List;
import java.util.Map;

class ModelTest
    extends ModelTestBase {
  private static final String NS = "https://csrc.nist.gov/ns/test/xml";

  @Nested
  class TestRootAssemblyWithFlags {

    @Test
    void testRootAssemblyWithFlags() {
      IBindingContext context = new DefaultBindingContext();

      IBoundDefinitionModelComplex definition = ObjectUtils.requireNonNull(
          context.getBoundDefinitionForClass(RootAssemblyWithFlags.class));

      IBoundInstanceFlag idFlag = ObjectUtils.requireNonNull(definition.getFlagInstanceByName(
          IEnhancedQName.of("id").getIndexPosition()));
      IBoundInstanceFlag defaultFlag = ObjectUtils.requireNonNull(definition.getFlagInstanceByName(
          IEnhancedQName.of("defaultFlag").getIndexPosition()));
      IBoundInstanceFlag numberFlag = ObjectUtils.requireNonNull(definition.getFlagInstanceByName(
          IEnhancedQName.of("number").getIndexPosition()));

      assertAll(
          "root assembly",
          () -> assertInstanceOf(IBoundDefinitionModelAssembly.class, definition),
          () -> assertAssemblyDefinition(
              RootAssemblyWithFlags.class,
              (IBoundDefinitionModelAssembly) definition),
          () -> assertFlagInstance(RootAssemblyWithFlags.class, "id", idFlag, context),
          () -> assertFlagInstance(RootAssemblyWithFlags.class, "defaultFlag", defaultFlag, context),
          () -> assertFlagInstance(RootAssemblyWithFlags.class, "number", numberFlag, context));
    }

    @Test
    void parseXmlMinimal() throws IOException {
      IBindingContext context = new DefaultBindingContext();

      String xml = new StringBuilder()
          .append("<root-assembly-with-flags xmlns='https://csrc.nist.gov/ns/test/xml' id='id'/>")
          .toString();

      try (InputStream is = new ByteArrayInputStream(xml.getBytes())) {
        IDeserializer<RootAssemblyWithFlags> deserializer
            = context.newDeserializer(Format.XML, RootAssemblyWithFlags.class);
        RootAssemblyWithFlags item = deserializer.deserialize(is, ObjectUtils.notNull(URI.create(".")));

        assertAll(
            "minimal xml",
            () -> assertNotNull(item),
            () -> assertEquals(
                "id",
                ObjectUtils.requireNonNull(item).getId(),
                "id"),
            () -> assertEquals(
                BigInteger.ONE,
                ObjectUtils.requireNonNull(item).getNumber(),
                "number"));
      }
    }

    @Test
    void parseJsonMinimal() throws IOException {
      IBindingContext context = new DefaultBindingContext();

      String json = new StringBuilder()
          .append("{\n")
          .append("  \"root-assembly-with-flags\": {\n")
          .append("    \"id\": \"id\"\n")
          .append("  }\n")
          .append("}\n")
          .toString();

      try (InputStream is = new ByteArrayInputStream(json.getBytes())) {
        IDeserializer<RootAssemblyWithFlags> deserializer
            = context.newDeserializer(Format.JSON, RootAssemblyWithFlags.class);
        RootAssemblyWithFlags item = deserializer.deserialize(is, ObjectUtils.notNull(URI.create(".")));

        assertAll(
            "minimal json",
            () -> assertNotNull(item),
            () -> assertEquals(
                "id",
                ObjectUtils.requireNonNull(item).getId(),
                "id"),
            () -> assertEquals(
                BigInteger.ONE,
                ObjectUtils.requireNonNull(item).getNumber(),
                "number"));
      }
    }
  }

  @Nested
  class TestRootAssemblyWithFields {

    @Test
    void testRootAssemblyWithFields() {
      IBindingContext context = new DefaultBindingContext();

      IBoundDefinitionModelAssembly definition = ObjectUtils.requireNonNull(
          (IBoundDefinitionModelAssembly) context.getBoundDefinitionForClass(RootAssemblyWithFields.class));

      IBoundInstanceModelField<?> defaultField = ObjectUtils.requireNonNull(
          definition.getFieldInstanceByName(IEnhancedQName.of(NS, "defaultField").getIndexPosition()));
      IBoundInstanceModelField<?> collectionField = ObjectUtils.requireNonNull(
          definition.getFieldInstanceByName(IEnhancedQName.of(NS, "field2").getIndexPosition()));
      IBoundInstanceModelField<?> specifiedValueKeyField = ObjectUtils.requireNonNull(
          definition.getFieldInstanceByName(IEnhancedQName.of(NS, "field-value-key").getIndexPosition()));
      IBoundInstanceModelField<?> defaultValueKeyField = ObjectUtils.requireNonNull(
          definition.getFieldInstanceByName(IEnhancedQName.of(NS, "field-default-value-key").getIndexPosition()));
      IBoundInstanceModelField<?> flagValueKeyField = ObjectUtils.requireNonNull(
          definition.getFieldInstanceByName(IEnhancedQName.of(NS, "field-flag-value-key").getIndexPosition()));
      IBoundInstanceModelField<?> flagJsonKeyField = ObjectUtils.requireNonNull(
          definition.getFieldInstanceByName(IEnhancedQName.of(NS, "field-json-key").getIndexPosition()));

      assertAll(
          "root assembly",
          () -> assertInstanceOf(IBoundDefinitionModelAssembly.class, definition),
          () -> assertAssemblyDefinition(
              RootAssemblyWithFields.class,
              definition),
          () -> assertFieldInstance(RootAssemblyWithFields.class, "defaultField", defaultField, context),
          () -> assertFieldInstance(RootAssemblyWithFields.class, "_field2", collectionField, context),
          () -> assertFieldInstance(RootAssemblyWithFields.class, "field3", specifiedValueKeyField, context),
          () -> assertFieldInstance(RootAssemblyWithFields.class, "field4", defaultValueKeyField, context),
          () -> assertFieldInstance(RootAssemblyWithFields.class, "field5", flagValueKeyField, context),
          () -> assertFieldInstance(RootAssemblyWithFields.class, "field6", flagJsonKeyField, context));
    }

    @Test
    void parseXmlMinimal() throws IOException {
      IBindingContext context = new DefaultBindingContext();

      String xml = new StringBuilder()
          .append("<root-assembly-with-fields xmlns='https://csrc.nist.gov/ns/test/xml'/>")
          .toString();

      try (InputStream is = new ByteArrayInputStream(xml.getBytes())) {
        IDeserializer<RootAssemblyWithFields> deserializer
            = context.newDeserializer(Format.XML, RootAssemblyWithFields.class);
        RootAssemblyWithFields item = deserializer.deserialize(is, ObjectUtils.notNull(URI.create(".")));

        assertAll(
            "minimal xml",
            () -> assertNotNull(item),
            () -> assertNull(
                ObjectUtils.requireNonNull(item).getField1(),
                "defaultField"),
            () -> {
              List<String> value = ObjectUtils.requireNonNull(item).getField2();
              assertAll(
                  "field2",
                  () -> assertNotNull(value, "field2"),
                  () -> assertTrue(value.isEmpty(), "not empty"));
            },
            () -> assertNull(
                ObjectUtils.requireNonNull(item).getField3(),
                "field-value-key"),
            () -> assertNull(
                ObjectUtils.requireNonNull(item).getField4(),
                "field-default-value-key"),
            () -> assertNull(
                ObjectUtils.requireNonNull(item).getField5(),
                "field-flag-value-key"),
            () -> {
              Map<String, JsonKeyField> value = ObjectUtils.requireNonNull(item).getField6();
              assertAll(
                  "field-json-key",
                  () -> assertNotNull(value, "field-json-key"),
                  () -> assertTrue(value.isEmpty(), "not empty"));
            });
      }
    }

    @Test
    void parseXmlPartial() throws IOException {
      IBindingContext context = new DefaultBindingContext();

      String xml = new StringBuilder()
          .append("<root-assembly-with-fields xmlns='https://csrc.nist.gov/ns/test/xml'>\n")
          .append("  <defaultField>value1</defaultField>\n")
          .append("  <fields2>\n")
          .append("    <field2>value2</field2>\n")
          .append("    <field2>value3</field2>\n")
          .append("  </fields2>\n")
          .append("  <field-value-key>value4</field-value-key>\n")
          .append("  <field-default-value-key>value5</field-default-value-key>\n")
          .append("  <field-flag-value-key flag='flag-value3'>value6</field-flag-value-key>\n")
          .append("  <field-json-key key='flag-value4' valueKey='flag-value5'>value7</field-json-key>\n")
          .append("</root-assembly-with-fields>\n")
          .toString();

      try (InputStream is = new ByteArrayInputStream(xml.getBytes())) {
        IDeserializer<RootAssemblyWithFields> deserializer
            = context.newDeserializer(Format.XML, RootAssemblyWithFields.class);
        RootAssemblyWithFields item = deserializer.deserialize(is, ObjectUtils.notNull(URI.create(".")));

        assertAll(
            "partial",
            () -> assertNotNull(item),
            () -> assertEquals(
                "value1",
                ObjectUtils.requireNonNull(item).getField1(),
                "defaultField"),
            () -> {
              List<String> value = ObjectUtils.requireNonNull(item).getField2();
              assertAll(
                  "field2",
                  () -> assertNotNull(value, "field2"),
                  () -> assertEquals(
                      List.of("value2", "value3"),
                      value,
                      "list contents not equal"));
            },
            () -> {
              RootAssemblyWithFields.ValueKeyField value = ObjectUtils.requireNonNull(item).getField3();
              assertAll(
                  "field3",
                  () -> assertNotNull(value, "field3"),
                  () -> assertNull(ObjectUtils.requireNonNull(value).getFlag()),
                  () -> assertEquals(
                      "value4",
                      ObjectUtils.requireNonNull(value).getValue(),
                      "list contents not equal"));
            },
            () -> {
              RootAssemblyWithFields.DefaultValueKeyField value = ObjectUtils.requireNonNull(item).getField4();
              assertAll(
                  "field4",
                  () -> assertNotNull(value, "field4"),
                  () -> assertNull(ObjectUtils.requireNonNull(value).getFlag()),
                  () -> assertEquals(
                      "value5",
                      ObjectUtils.requireNonNull(value).getValue(),
                      "list contents not equal"));
            },
            () -> {
              RootAssemblyWithFields.FlagValueKeyField value = ObjectUtils.requireNonNull(item).getField5();
              assertAll(
                  "field5",
                  () -> assertNotNull(value, "field5"),
                  () -> assertEquals(
                      "flag-value3",
                      ObjectUtils.requireNonNull(value).getFlag(),
                      "flag value not equal"),
                  () -> assertEquals(
                      "value6",
                      ObjectUtils.requireNonNull(value).getValue(),
                      "list contents not equal"));
            },
            () -> {
              Map<String, RootAssemblyWithFields.JsonKeyField> value = ObjectUtils.requireNonNull(item).getField6();
              assertAll(
                  "field6",
                  () -> assertNotNull(value, "field6"),
                  () -> assertEquals(1, value.size(), "map containts 1 entry"),
                  () -> assertEquals(
                      "flag-value4",
                      ObjectUtils.requireNonNull(value).get("flag-value4").getKey(),
                      "flag value not equal"),
                  () -> assertEquals(
                      "flag-value5",
                      ObjectUtils.requireNonNull(value).get("flag-value4").getValueKey(),
                      "flag value not equal"),
                  () -> assertEquals(
                      "value7",
                      ObjectUtils.requireNonNull(value).get("flag-value4").getValue(),
                      "list contents not equal"));
            });
      }
    }

    @Test
    void parseXmlComplete() throws IOException {
      IBindingContext context = new DefaultBindingContext();

      String xml = new StringBuilder()
          .append("<root-assembly-with-fields xmlns='https://csrc.nist.gov/ns/test/xml'>")
          .append("  <defaultField>value1</defaultField>")
          .append("  <fields2>")
          .append("    <field2>value2</field2>")
          .append("    <field2>value3</field2>")
          .append("  </fields2>")
          .append("  <field-value-key flag='flag-value1'>value4</field-value-key>")
          .append("  <field-default-value-key flag='flag-value2'>value5</field-default-value-key>")
          .append("  <field-flag-value-key flag='flag-value3'>value6</field-flag-value-key>")
          .append("  <field-json-key key='flag-value4' valueKey='flag-value5'>value7</field-json-key>")
          .append("  <field-json-key key='flag-value6' valueKey='flag-value7'>value8</field-json-key>")
          .append("</root-assembly-with-fields>")
          .toString();

      try (InputStream is = new ByteArrayInputStream(xml.getBytes())) {
        IDeserializer<RootAssemblyWithFields> deserializer
            = context.newDeserializer(Format.XML, RootAssemblyWithFields.class);
        RootAssemblyWithFields item = deserializer.deserialize(is, ObjectUtils.notNull(URI.create(".")));

        assertAll(
            "complete",
            () -> assertNotNull(item),
            () -> assertEquals(
                "value1",
                ObjectUtils.requireNonNull(item).getField1(),
                "defaultField"),
            () -> {
              List<String> value = ObjectUtils.requireNonNull(item).getField2();
              assertAll(
                  "field2",
                  () -> assertNotNull(value, "field2"),
                  () -> assertEquals(
                      List.of("value2", "value3"),
                      value,
                      "list contents not equal"));
            },
            () -> {
              RootAssemblyWithFields.ValueKeyField value = ObjectUtils.requireNonNull(item).getField3();
              assertAll(
                  "field3",
                  () -> assertNotNull(value, "field3"),
                  () -> assertEquals(
                      "flag-value1",
                      ObjectUtils.requireNonNull(value).getFlag(),
                      "flag value not equal"),
                  () -> assertEquals(
                      "value4",
                      ObjectUtils.requireNonNull(value).getValue(),
                      "list contents not equal"));
            },
            () -> {
              RootAssemblyWithFields.DefaultValueKeyField value = ObjectUtils.requireNonNull(item).getField4();
              assertAll(
                  "field4",
                  () -> assertNotNull(value, "field4"),
                  () -> assertEquals(
                      "flag-value2",
                      ObjectUtils.requireNonNull(value).getFlag(),
                      "flag value not equal"),
                  () -> assertEquals(
                      "value5",
                      ObjectUtils.requireNonNull(value).getValue(),
                      "list contents not equal"));
            },
            () -> {
              RootAssemblyWithFields.FlagValueKeyField value = ObjectUtils.requireNonNull(item).getField5();
              assertAll(
                  "field5",
                  () -> assertNotNull(value, "field5"),
                  () -> assertEquals(
                      "flag-value3",
                      ObjectUtils.requireNonNull(value).getFlag(),
                      "flag value not equal"),
                  () -> assertEquals(
                      "value6",
                      ObjectUtils.requireNonNull(value).getValue(),
                      "list contents not equal"));
            },
            () -> {
              Map<String, RootAssemblyWithFields.JsonKeyField> value = ObjectUtils.requireNonNull(item).getField6();
              assertAll(
                  "field6",
                  () -> assertNotNull(value, "field6"),
                  () -> assertEquals(2, value.size(), "map containts 2 entries"),
                  () -> assertEquals(
                      "flag-value4",
                      ObjectUtils.requireNonNull(value).get("flag-value4").getKey(),
                      "flag value not equal"),
                  () -> assertEquals(
                      "flag-value5",
                      ObjectUtils.requireNonNull(value).get("flag-value4").getValueKey(),
                      "flag value not equal"),
                  () -> assertEquals(
                      "value7",
                      ObjectUtils.requireNonNull(value).get("flag-value4").getValue(),
                      "list contents not equal"),
                  () -> assertEquals(
                      "flag-value6",
                      ObjectUtils.requireNonNull(value).get("flag-value6").getKey(),
                      "flag value not equal"),
                  () -> assertEquals(
                      "flag-value7",
                      ObjectUtils.requireNonNull(value).get("flag-value6").getValueKey(),
                      "flag value not equal"),
                  () -> assertEquals(
                      "value8",
                      ObjectUtils.requireNonNull(value).get("flag-value6").getValue(),
                      "list contents not equal"));
            });
      }
    }

    @Test
    void parseJsonMinimal() throws IOException {
      IBindingContext context = new DefaultBindingContext();

      String json = new StringBuilder()
          .append('{')
          .append("  \"root-assembly-with-fields\": {}")
          .append('}')
          .toString();

      try (InputStream is = new ByteArrayInputStream(json.getBytes())) {
        IDeserializer<RootAssemblyWithFields> deserializer
            = context.newDeserializer(Format.JSON, RootAssemblyWithFields.class);
        RootAssemblyWithFields item = deserializer.deserialize(is, ObjectUtils.notNull(URI.create(".")));

        assertAll(
            "minimal xml",
            () -> assertNotNull(item),
            () -> assertNull(
                ObjectUtils.requireNonNull(item).getField1(),
                "defaultField"),
            () -> {
              List<String> value = ObjectUtils.requireNonNull(item).getField2();
              assertAll(
                  "field2",
                  () -> assertNotNull(value, "field2"),
                  () -> assertTrue(value.isEmpty(), "not empty"));
            },
            () -> assertNull(
                ObjectUtils.requireNonNull(item).getField3(),
                "field-value-key"),
            () -> assertNull(
                ObjectUtils.requireNonNull(item).getField4(),
                "field-default-value-key"),
            () -> assertNull(
                ObjectUtils.requireNonNull(item).getField5(),
                "field-flag-value-key"),
            () -> {
              Map<String, JsonKeyField> value = ObjectUtils.requireNonNull(item).getField6();
              assertAll(
                  "field-json-key",
                  () -> assertNotNull(value, "field-json-key"),
                  () -> assertTrue(value.isEmpty(), "not empty"));
            });
      }
    }

    @Test
    void parseJsonValueKeyFlag() throws IOException {
      IBindingContext context = new DefaultBindingContext();

      String json = new StringBuilder()
          .append("{\n")
          .append("  \"root-assembly-with-fields\": {\n")
          .append("    \"field-flag-value-key\": {\n")
          .append("      \"flag-value3\": \"value6\"\n")
          .append("    },\n")
          .append("    \"fields6\": {\n")
          .append("      \"flag-value4\": {\n")
          .append("        \"flag-value5\": \"value7\"\n")
          .append("      }\n")
          .append("    }\n")
          .append("  }\n")
          .append("}\n")
          .toString();

      try (InputStream is = new ByteArrayInputStream(json.getBytes())) {
        IDeserializer<RootAssemblyWithFields> deserializer
            = context.newDeserializer(Format.JSON, RootAssemblyWithFields.class);
        RootAssemblyWithFields item = deserializer.deserialize(is, ObjectUtils.notNull(URI.create(".")));

        assertAll(
            "partial",
            () -> assertNotNull(item),
            () -> assertNull(
                ObjectUtils.requireNonNull(item).getField1(),
                "defaultField"),
            () -> {
              List<String> value = ObjectUtils.requireNonNull(item).getField2();
              assertAll(
                  "field2",
                  () -> assertNotNull(value, "field2"),
                  () -> assertTrue(value.isEmpty(), "not empty"));
            },
            () -> assertNull(
                ObjectUtils.requireNonNull(item).getField3(),
                "field-value-key"),
            () -> assertNull(
                ObjectUtils.requireNonNull(item).getField4(),
                "field-default-value-key"),
            () -> {
              RootAssemblyWithFields.FlagValueKeyField value = ObjectUtils.requireNonNull(item).getField5();
              assertAll(
                  "field5",
                  () -> assertNotNull(value, "field5"),
                  () -> assertEquals(
                      "flag-value3",
                      ObjectUtils.requireNonNull(value).getFlag(),
                      "flag value not equal"),
                  () -> assertEquals(
                      "value6",
                      ObjectUtils.requireNonNull(value).getValue(),
                      "field value list contents not equal"));
            },
            () -> {
              Map<String, RootAssemblyWithFields.JsonKeyField> value = ObjectUtils.requireNonNull(item).getField6();
              assertAll(
                  "field6",
                  () -> assertNotNull(value, "field6"),
                  () -> assertEquals(1, value.size(), "map containts 1 entry"),
                  () -> assertEquals(
                      "flag-value4",
                      ObjectUtils.requireNonNull(value).get("flag-value4").getKey(),
                      "flag value not equal"),
                  () -> assertEquals(
                      "flag-value5",
                      ObjectUtils.requireNonNull(value).get("flag-value4").getValueKey(),
                      "flag value not equal"),
                  () -> assertEquals(
                      "value7",
                      ObjectUtils.requireNonNull(value).get("flag-value4").getValue(),
                      "field value list contents not equal"));
            });
      }
    }

    @Test
    void parseJsonPartial() throws IOException {
      IBindingContext context = new DefaultBindingContext();

      String json = new StringBuilder()
          .append("{\n")
          .append("  \"root-assembly-with-fields\": {\n")
          .append("    \"defaultField\": \"value1\",\n")
          .append("    \"fields2\": [\"value2\", \"value3\" ],\n")
          .append("    \"field-value-key\": {\n")
          .append("      \"a-value\": \"value4\"\n")
          .append("    },\n")
          .append("    \"field-default-value-key\": {\n")
          .append("      \"STRVALUE\": \"value5\"\n")
          .append("    },\n")
          .append("    \"field-flag-value-key\": {\n")
          .append("      \"flag-value3\": \"value6\"\n")
          .append("    },\n")
          .append("    \"fields6\": {\n")
          .append("      \"flag-value4\": {\n")
          .append("        \"flag-value5\": \"value7\"\n")
          .append("      }\n")
          .append("    }\n")
          .append("  }\n")
          .append("}\n")
          .toString();

      try (InputStream is = new ByteArrayInputStream(json.getBytes())) {
        IDeserializer<RootAssemblyWithFields> deserializer
            = context.newDeserializer(Format.JSON, RootAssemblyWithFields.class);
        RootAssemblyWithFields item = deserializer.deserialize(is, ObjectUtils.notNull(URI.create(".")));

        assertAll(
            "partial",
            () -> assertNotNull(item),
            () -> assertEquals(
                "value1",
                ObjectUtils.requireNonNull(item).getField1(),
                "defaultField"),
            () -> {
              List<String> value = ObjectUtils.requireNonNull(item).getField2();
              assertAll(
                  "field2",
                  () -> assertNotNull(value, "field2"),
                  () -> assertEquals(
                      List.of("value2", "value3"),
                      value,
                      "list contents not equal"));
            },
            () -> {
              RootAssemblyWithFields.ValueKeyField value = ObjectUtils.requireNonNull(item).getField3();
              assertAll(
                  "field3",
                  () -> assertNotNull(value, "field3"),
                  () -> assertNull(ObjectUtils.requireNonNull(value).getFlag()),
                  () -> assertEquals(
                      "value4",
                      ObjectUtils.requireNonNull(value).getValue(),
                      "list contents not equal"));
            },
            () -> {
              RootAssemblyWithFields.DefaultValueKeyField value = ObjectUtils.requireNonNull(item).getField4();
              assertAll(
                  "field4",
                  () -> assertNotNull(value, "field4"),
                  () -> assertNull(ObjectUtils.requireNonNull(value).getFlag()),
                  () -> assertEquals(
                      "value5",
                      ObjectUtils.requireNonNull(value).getValue(),
                      "list contents not equal"));
            },
            () -> {
              RootAssemblyWithFields.FlagValueKeyField value = ObjectUtils.requireNonNull(item).getField5();
              assertAll(
                  "field5",
                  () -> assertNotNull(value, "field5"),
                  () -> assertEquals(
                      "flag-value3",
                      ObjectUtils.requireNonNull(value).getFlag(),
                      "flag value not equal"),
                  () -> assertEquals(
                      "value6",
                      ObjectUtils.requireNonNull(value).getValue(),
                      "list contents not equal"));
            },
            () -> {
              Map<String, RootAssemblyWithFields.JsonKeyField> value = ObjectUtils.requireNonNull(item).getField6();
              assertAll(
                  "field6",
                  () -> assertNotNull(value, "field6"),
                  () -> assertEquals(1, value.size(), "map containts 1 entry"),
                  () -> assertEquals(
                      "flag-value4",
                      ObjectUtils.requireNonNull(value).get("flag-value4").getKey(),
                      "flag value not equal"),
                  () -> assertEquals(
                      "flag-value5",
                      ObjectUtils.requireNonNull(value).get("flag-value4").getValueKey(),
                      "flag value not equal"),
                  () -> assertEquals(
                      "value7",
                      ObjectUtils.requireNonNull(value).get("flag-value4").getValue(),
                      "list contents not equal"));
            });
      }
    }

    @Test
    void parseJsonComplete() throws IOException {
      IBindingContext context = new DefaultBindingContext();

      String json = new StringBuilder()
          .append("{\n")
          .append("  \"root-assembly-with-fields\": {\n")
          .append("    \"defaultField\": \"value1\",\n")
          .append("    \"fields2\": [\"value2\", \"value3\" ],\n")
          .append("    \"field-value-key\": {\n")
          .append("      \"flag\": \"flag-value1\",\n")
          .append("      \"a-value\": \"value4\"\n")
          .append("    },\n")
          .append("    \"field-default-value-key\": {\n")
          .append("      \"flag\": \"flag-value2\",\n")
          .append("      \"STRVALUE\": \"value5\"\n")
          .append("    },\n")
          .append("    \"field-flag-value-key\": {\n")
          .append("      \"flag-value3\": \"value6\"\n")
          .append("    },\n")
          .append("    \"fields6\": {\n")
          .append("      \"flag-value4\": {\n")
          .append("        \"flag-value5\": \"value7\"\n")
          .append("      },\n")
          .append("      \"flag-value6\": {\n")
          .append("        \"flag-value7\": \"value8\"\n")
          .append("      }\n")
          .append("    }\n")
          .append("  }\n")
          .append("}\n")
          .toString();

      try (InputStream is = new ByteArrayInputStream(json.getBytes())) {
        IDeserializer<RootAssemblyWithFields> deserializer
            = context.newDeserializer(Format.JSON, RootAssemblyWithFields.class);
        RootAssemblyWithFields item = deserializer.deserialize(is, ObjectUtils.notNull(URI.create(".")));

        assertAll(
            "complete",
            () -> assertNotNull(item),
            () -> assertEquals(
                "value1",
                ObjectUtils.requireNonNull(item).getField1(),
                "defaultField"),
            () -> {
              List<String> value = ObjectUtils.requireNonNull(item).getField2();
              assertAll(
                  "field2",
                  () -> assertNotNull(value, "field2"),
                  () -> assertEquals(
                      List.of("value2", "value3"),
                      value,
                      "list contents not equal"));
            },
            () -> {
              RootAssemblyWithFields.ValueKeyField value = ObjectUtils.requireNonNull(item).getField3();
              assertAll(
                  "field3",
                  () -> assertNotNull(value, "field3"),
                  () -> assertEquals(
                      "flag-value1",
                      ObjectUtils.requireNonNull(value).getFlag(),
                      "flag value not equal"),
                  () -> assertEquals(
                      "value4",
                      ObjectUtils.requireNonNull(value).getValue(),
                      "list contents not equal"));
            },
            () -> {
              RootAssemblyWithFields.DefaultValueKeyField value = ObjectUtils.requireNonNull(item).getField4();
              assertAll(
                  "field4",
                  () -> assertNotNull(value, "field4"),
                  () -> assertEquals(
                      "flag-value2",
                      ObjectUtils.requireNonNull(value).getFlag(),
                      "flag value not equal"),
                  () -> assertEquals(
                      "value5",
                      ObjectUtils.requireNonNull(value).getValue(),
                      "list contents not equal"));
            },
            () -> {
              RootAssemblyWithFields.FlagValueKeyField value = ObjectUtils.requireNonNull(item).getField5();
              assertAll(
                  "field5",
                  () -> assertNotNull(value, "field5"),
                  () -> assertEquals(
                      "flag-value3",
                      ObjectUtils.requireNonNull(value).getFlag(),
                      "flag value not equal"),
                  () -> assertEquals(
                      "value6",
                      ObjectUtils.requireNonNull(value).getValue(),
                      "list contents not equal"));
            },
            () -> {
              Map<String, RootAssemblyWithFields.JsonKeyField> value = ObjectUtils.requireNonNull(item).getField6();
              assertAll(
                  "field6",
                  () -> assertNotNull(value, "field6"),
                  () -> assertEquals(2, value.size(), "map containts 2 entries"),
                  () -> assertEquals(
                      "flag-value4",
                      ObjectUtils.requireNonNull(value).get("flag-value4").getKey(),
                      "flag value not equal"),
                  () -> assertEquals(
                      "flag-value5",
                      ObjectUtils.requireNonNull(value).get("flag-value4").getValueKey(),
                      "flag value not equal"),
                  () -> assertEquals(
                      "value7",
                      ObjectUtils.requireNonNull(value).get("flag-value4").getValue(),
                      "list contents not equal"),
                  () -> assertEquals(
                      "flag-value6",
                      ObjectUtils.requireNonNull(value).get("flag-value6").getKey(),
                      "flag value not equal"),
                  () -> assertEquals(
                      "flag-value7",
                      ObjectUtils.requireNonNull(value).get("flag-value6").getValueKey(),
                      "flag value not equal"),
                  () -> assertEquals(
                      "value8",
                      ObjectUtils.requireNonNull(value).get("flag-value6").getValue(),
                      "list contents not equal"));
            });
      }
    }
  }
}
