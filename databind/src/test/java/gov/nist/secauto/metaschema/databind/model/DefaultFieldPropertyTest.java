/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;

import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.io.json.MetaschemaJsonReader;
import gov.nist.secauto.metaschema.databind.model.test.MultiFieldAssembly;
import gov.nist.secauto.metaschema.databind.model.test.SimpleAssembly;

import org.jmock.auto.Mock;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;

class DefaultFieldPropertyTest {
  @RegisterExtension
  JUnit5Mockery context = new JUnit5Mockery();

  @Mock
  private IModule module; // NOPMD - it's injected
  @Mock
  private IBoundDefinitionModelAssembly classBinding; // NOPMD - it's injected
  @Mock
  private IBindingContext bindingContext; // NOPMD - it's injected

  @Test
  void testJsonReadFlag()
      throws JsonParseException, IOException {
    String json = "{ \"test\": { \"id\": \"theId\", \"number\": 1 } }";
    JsonFactory factory = new JsonFactory();
    try (JsonParser jsonParser = factory.createParser(json)) {
      assert jsonParser != null;

      IBindingContext bindingContext = IBindingContext.newInstance();
      IBoundDefinitionModelAssembly classBinding
          = (IBoundDefinitionModelAssembly) bindingContext.getBoundDefinitionForClass(SimpleAssembly.class);
      assert classBinding != null;

      MetaschemaJsonReader parser = new MetaschemaJsonReader(jsonParser);

      SimpleAssembly obj = parser.readObjectRoot(
          classBinding,
          ObjectUtils.requireNonNull(classBinding.getRootJsonName()));
      assert obj != null;

      assertAll(
          () -> assertEquals("theId", obj.getId()));
    }
  }

  @Test
  void testJsonReadField()
      throws JsonParseException, IOException {

    String json = "{ \"field1\": \"field1value\", \"fields2\": [ \"field2value\" ] }";
    JsonFactory factory = new JsonFactory();
    try (JsonParser jsonParser = factory.createParser(json)) {
      assert jsonParser != null;
      // get first token
      jsonParser.nextToken();

      IBindingContext bindingContext = IBindingContext.newInstance();
      IBoundDefinitionModelComplex classBinding = bindingContext.getBoundDefinitionForClass(MultiFieldAssembly.class);
      assert classBinding != null;

      MetaschemaJsonReader parser = new MetaschemaJsonReader(jsonParser);

      // read the top-level definition
      MultiFieldAssembly obj = (MultiFieldAssembly) parser.readObject(classBinding);

      assertAll(
          () -> assertEquals("field1value", obj.getField1()),
          () -> assertTrue(obj.getField2() instanceof LinkedList),
          () -> assertIterableEquals(Collections.singleton("field2value"), obj.getField2()));

      // assertEquals(JsonToken.START_OBJECT, jsonParser.nextToken());
      // assertEquals(JsonToken.FIELD_NAME, jsonParser.nextToken());
      // assertEquals("id", jsonParser.currentName());
    }
  }

  @Test
  void testJsonReadMissingFieldValue()
      throws JsonParseException, IOException {
    String json = "{ \"fields2\": [\n"
        + "    \"field2value\"\n"
        + "    ]\n"
        + "}\n";
    JsonFactory factory = new JsonFactory();
    try (JsonParser jsonParser = factory.createParser(json)) {
      assert jsonParser != null;
      // get first token
      jsonParser.nextToken();

      IBindingContext bindingContext = IBindingContext.newInstance();
      IBoundDefinitionModelComplex classBinding = bindingContext.getBoundDefinitionForClass(MultiFieldAssembly.class);
      assert classBinding != null;

      MetaschemaJsonReader parser = new MetaschemaJsonReader(jsonParser);

      // read the top-level definition
      MultiFieldAssembly obj = (MultiFieldAssembly) parser.readObject(classBinding);

      assertAll(
          () -> assertNull(obj.getField1()),
          () -> assertTrue(obj.getField2() instanceof LinkedList),
          () -> assertIterableEquals(Collections.singleton("field2value"), obj.getField2()));
    }
  }

  @Test
  void testJsonReadFieldValueKey()
      throws JsonParseException, IOException {
    String json = "{ \"field-value-key\": { \"a-value\": \"theValue\" } }";
    JsonFactory factory = new JsonFactory();
    try (JsonParser jsonParser = factory.createParser(json)) {
      assert jsonParser != null;
      // get first token
      jsonParser.nextToken();

      IBindingContext bindingContext = IBindingContext.newInstance();
      IBoundDefinitionModelComplex classBinding = bindingContext.getBoundDefinitionForClass(MultiFieldAssembly.class);
      assert classBinding != null;

      MetaschemaJsonReader parser = new MetaschemaJsonReader(jsonParser);

      // read the top-level definition
      MultiFieldAssembly obj = (MultiFieldAssembly) parser.readObject(classBinding);

      assertAll(
          () -> assertEquals("theValue", obj.getField3().getValue()));
    }
  }

  @Test
  void testJsonReadFieldDefaultValueKey()
      throws JsonParseException, IOException {
    String json = "{ \"field-default-value-key\": { \"STRVALUE\": \"theValue\" } }";
    JsonFactory factory = new JsonFactory();
    try (JsonParser jsonParser = factory.createParser(json)) {
      assert jsonParser != null;
      // get first token
      jsonParser.nextToken();

      IBindingContext bindingContext = IBindingContext.newInstance();
      IBoundDefinitionModelComplex classBinding = bindingContext.getBoundDefinitionForClass(MultiFieldAssembly.class);
      assert classBinding != null;

      MetaschemaJsonReader parser = new MetaschemaJsonReader(jsonParser);

      // read the top-level definition
      MultiFieldAssembly obj = (MultiFieldAssembly) parser.readObject(classBinding);

      assertAll(
          () -> assertEquals("theValue", obj.getField4().getValue()));
    }
  }
}
