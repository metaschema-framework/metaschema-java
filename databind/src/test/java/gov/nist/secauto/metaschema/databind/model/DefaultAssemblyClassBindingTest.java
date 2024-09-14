/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;

import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.io.json.MetaschemaJsonReader;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class DefaultAssemblyClassBindingTest
    extends AbstractBoundModelTestSupport {
  @Test
  void testMinimalJsonParse() throws JsonParseException, IOException {
    Path testContent = Paths.get("src/test/resources/content/minimal.json");
    try (BufferedReader reader = Files.newBufferedReader(testContent)) {
      assert reader != null;

      IBoundDefinitionModelAssembly classBinding = getRootAssemblyClassBinding();

      try (JsonParser parser = newJsonParser(reader)) {
        Object value = new MetaschemaJsonReader(parser).readObjectRoot(
            classBinding,
            ObjectUtils.requireNonNull(classBinding.getRootJsonName()));
        assertNotNull(value, "root was null");
      }
    }
  }

  @Test
  void testModule() {
    IBoundDefinitionModelAssembly definition = getRootAssemblyClassBinding();
    IModule module = definition.getContainingModule();
    assertNotNull(module, "metaschema was null");
  }

  // @Test
  // void testSimpleJson() throws JsonParseException, IOException,
  // BindingException {
  // File testContent
  // = new
  // File(getClass().getClassLoader().getResource("test-content/bound-class-simple.json").getFile());
  // try (BufferedReader reader = Files.newBufferedReader(testContent.toPath())) {
  // JsonParser jsonParser = newJsonParser(reader);
  //
  // assertEquals(JsonToken.START_OBJECT, jsonParser.nextToken());
  // assertEquals(JsonToken.FIELD_NAME, jsonParser.nextToken());
  //
  // IAssemblyClassBinding classBinding = getAssemblyClassBinding();
  // IBoundAssemblyInstance root = new RootAssemblyDefinition(classBinding);
  // BoundClass obj = (BoundClass) root.read(jsonParsingContext);
  //
  // assertEquals(JsonToken.END_OBJECT, jsonParser.currentToken());
  // assertSimple(obj);
  // }
  // }
  //
  // @Test
  // void testSimpleXml() throws BindingException, XMLStreamException, IOException
  // {
  // File testContent
  // = new
  // File(getClass().getClassLoader().getResource("test-content/bound-class-simple.xml").getFile());
  // try (BufferedReader reader = Files.newBufferedReader(testContent.toPath())) {
  // XMLEventReader eventReader = newXmlParser(reader);
  //
  // IAssemblyClassBinding classBinding = getAssemblyClassBinding();
  //
  // // assertEquals(XMLEvent.START_DOCUMENT, parser.nextEvent().getEventType());
  //
  // BoundClass obj = (BoundClass) classBinding.readRoot(xmlParsingContext);
  //
  // assertEquals(XMLEvent.END_DOCUMENT, eventReader.peek().getEventType());
  // assertSimple(obj);
  // }
  // }
  //
  // private void assertSimple(BoundClass obj) {
  // assertNotNull(obj);
  // assertEquals("idvalue", obj.getId());
  // assertNull(obj.getSingleSimpleField());
  // assertNotNull(obj.getGroupedListSimpleField());
  // assertTrue(obj.getGroupedListSimpleField().isEmpty());
  // assertNull(obj.getSingleFlaggedField());
  // assertNotNull(obj.getGroupedListField());
  // assertTrue(obj.getGroupedListField().isEmpty());
  // assertNotNull(obj.getUngroupedListField());
  // assertTrue(obj.getUngroupedListField().isEmpty());
  // assertNotNull(obj.getMappedField());
  // assertTrue(obj.getMappedField().isEmpty());
  //
  // context.assertIsSatisfied();
  // }
  //
  // @Test
  // void testComplexJson() throws BindingException, IOException {
  // File testContent
  // = new
  // File(getClass().getClassLoader().getResource("test-content/bound-class-complex.json").getFile());
  // try (BufferedReader reader = Files.newBufferedReader(testContent.toPath())) {
  // JsonParser jsonParser = newJsonParser(reader);
  //
  // assertEquals(JsonToken.START_OBJECT, jsonParser.nextToken());
  // assertEquals(JsonToken.FIELD_NAME, jsonParser.nextToken());
  //
  // IAssemblyClassBinding classBinding = getAssemblyClassBinding();
  // IBoundAssemblyInstance root = new RootAssemblyDefinition(classBinding);
  // BoundClass obj = (BoundClass) root.read(jsonParsingContext);
  //
  // assertEquals(JsonToken.END_OBJECT, jsonParser.currentToken());
  // assertComplex(obj);
  // }
  // }
  //
  // @Test
  // void testComplexXml() throws BindingException, XMLStreamException,
  // IOException {
  // File testContent
  // = new
  // File(getClass().getClassLoader().getResource("test-content/bound-class-complex.xml").getFile());
  // try (BufferedReader reader = Files.newBufferedReader(testContent.toPath())) {
  // XMLEventReader eventReader = newXmlParser(reader);
  //
  // IAssemblyClassBinding classBinding = getAssemblyClassBinding();
  //
  // BoundClass obj = (BoundClass) classBinding.readRoot(xmlParsingContext);
  //
  // assertEquals(XMLEvent.END_DOCUMENT, eventReader.peek().getEventType());
  // assertComplex(obj);
  // }
  // }
  //
  // private void assertComplex(BoundClass obj) {
  // assertNotNull(obj);
  // assertEquals("idvalue", obj.getId());
  // assertEquals("single-simple-value", obj.getSingleSimpleField());
  // assertNotNull(obj.getGroupedListSimpleField());
  // assertIterableEquals(List.of("grouped-list-simple-item-value-1",
  // "grouped-list-simple-item-value-2"),
  // obj.getGroupedListSimpleField());
  //
  // assertNotNull(obj.getSingleFlaggedField());
  // FlaggedField flaggedField = obj.getSingleFlaggedField();
  // assertEquals("single-flagged-id", flaggedField.getId());
  // assertEquals("single-flagged-value", flaggedField.getValue());
  // //
  // //
  // // assertNotNull(obj.getGroupedListField());
  // // assertTrue(obj.getGroupedListField().isEmpty());
  // // assertNotNull(obj.getUngroupedListField());
  // // assertTrue(obj.getUngroupedListField().isEmpty());
  // // assertNotNull(obj.getMappedField());
  // // assertTrue(obj.getMappedField().isEmpty());
  //
  // context.assertIsSatisfied();
  // }

}
