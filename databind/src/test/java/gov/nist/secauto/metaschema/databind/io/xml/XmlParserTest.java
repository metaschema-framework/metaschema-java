/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io.xml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ctc.wstx.stax.WstxInputFactory;
import com.fasterxml.jackson.core.JsonParseException;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.model.AbstractBoundModelTestSupport;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelAssembly;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceFlag;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelField;
import gov.nist.secauto.metaschema.databind.model.test.FlaggedAssembly;
import gov.nist.secauto.metaschema.databind.model.test.MultiFieldAssembly;

import org.codehaus.stax2.XMLEventReader2;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

class XmlParserTest
    extends AbstractBoundModelTestSupport {
  private static final String NS = "https://csrc.nist.gov/ns/test/xml";

  @Test
  void testXmlRead() throws IOException, XMLStreamException {
    String xml = "<test xmlns='https://csrc.nist.gov/ns/test/xml'>"
        + "  <field1>field1value</field1>" + "</test>";
    XMLInputFactory factory = XMLInputFactory.newInstance();
    assert factory instanceof WstxInputFactory;
    XMLEventReader2 eventReader = (XMLEventReader2) factory.createXMLEventReader(new StringReader(xml));

    assertEquals(XMLStreamConstants.START_DOCUMENT, eventReader.nextEvent().getEventType());
    XMLEvent event = eventReader.nextEvent();
    assertEquals(XMLStreamConstants.START_ELEMENT, event.getEventType());
    StartElement start = event.asStartElement();
    // assertEquals("test", jsonParser.nextFieldName());
    // assertEquals(JsonToken.START_OBJECT, jsonParser.nextToken());
    // assertEquals(JsonToken.FIELD_NAME, jsonParser.nextToken());

    assert start != null;

    MetaschemaXmlReader parser = new MetaschemaXmlReader(eventReader);

    IBindingContext bindingContext = getBindingContext();

    IBoundDefinitionModelAssembly assembly
        = ObjectUtils.requireNonNull(
            (IBoundDefinitionModelAssembly) bindingContext.getBoundDefinitionForClass(MultiFieldAssembly.class));

    IBoundInstanceModelField<?> field1Instance = ObjectUtils.requireNonNull(assembly.getFieldInstanceByName(
        new QName(NS, "field1")));

    IBoundInstanceModelField<?> field2Instance = ObjectUtils.requireNonNull(assembly.getFieldInstanceByName(
        new QName(NS, "field2")));

    MultiFieldAssembly obj = new MultiFieldAssembly();

    assertTrue(parser.readItems(field1Instance, obj, true));
    assertFalse(parser.readItems(field2Instance, obj, true));

    assertEquals("field1value", obj.getField1());
    assertEquals(null, obj.getField2());
  }

  @Test
  @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
  void testXmlReadFlagProperty() throws JsonParseException, IOException,
      XMLStreamException {
    String xml = "<flagged-assembly xmlns='https://csrc.nist.gov/ns/test/xml' id='theId' number='1'/>";
    XMLInputFactory factory = XMLInputFactory.newInstance();
    assert factory instanceof WstxInputFactory;
    XMLEventReader2 eventReader = (XMLEventReader2) factory.createXMLEventReader(new StringReader(xml));

    IBindingContext bindingContext = getBindingContext();
    IBoundDefinitionModelAssembly assembly
        = ObjectUtils
            .requireNonNull(
                (IBoundDefinitionModelAssembly) bindingContext.getBoundDefinitionForClass(FlaggedAssembly.class));

    IBoundInstanceFlag idProperty = assembly.getFlagInstanceByName(new QName("id"));
    assert idProperty != null;

    assertEquals(XMLStreamConstants.START_DOCUMENT, eventReader.nextEvent().getEventType());

    MetaschemaXmlReader parser = new MetaschemaXmlReader(eventReader);
    FlaggedAssembly obj = parser.read(assembly);

    assertEquals("theId", obj.getId());
    assertEquals(XMLStreamConstants.END_DOCUMENT, eventReader.nextEvent().getEventType());
  }

  @Test
  void testXmlReadGroupedField() throws JsonParseException, IOException, XMLStreamException {
    String xml = new StringBuilder()
        .append("<test xmlns='https://csrc.nist.gov/ns/test/xml'>\n")
        .append(" <fields2>\n")
        .append("   <field2>field2value</field2>\n")
        .append(" </fields2>\n")
        .append("</test>")
        .toString();
    XMLInputFactory factory = XMLInputFactory.newInstance();
    assert factory instanceof WstxInputFactory;
    XMLEventReader2 eventReader = (XMLEventReader2) factory.createXMLEventReader(new StringReader(xml));

    assertEquals(XMLStreamConstants.START_DOCUMENT, eventReader.nextEvent().getEventType());
    XMLEvent event = eventReader.nextEvent();
    assertEquals(XMLStreamConstants.START_ELEMENT,
        event.getEventType());
    StartElement start = event.asStartElement();
    // assertEquals("test", jsonParser.nextFieldName());
    // assertEquals(JsonToken.START_OBJECT, jsonParser.nextToken());
    // assertEquals(JsonToken.FIELD_NAME, jsonParser.nextToken());

    assert start != null;

    MetaschemaXmlReader parser = new MetaschemaXmlReader(eventReader);

    IBindingContext bindingContext = getBindingContext();

    IBoundDefinitionModelAssembly assembly
        = ObjectUtils.requireNonNull(
            (IBoundDefinitionModelAssembly) bindingContext.getBoundDefinitionForClass(MultiFieldAssembly.class));

    IBoundInstanceModelField<?> field1Instance
        = ObjectUtils.requireNonNull(assembly.getFieldInstanceByName(new QName(NS, "field1")));

    IBoundInstanceModelField<?> field2Instance
        = ObjectUtils.requireNonNull(assembly.getFieldInstanceByName(new QName(NS, "field2")));

    MultiFieldAssembly obj = new MultiFieldAssembly();

    assertFalse(parser.readItems(field1Instance, obj, true));
    assertTrue(parser.readItems(field2Instance, obj, true));

    assertEquals(null, obj.getField1());
    assertIterableEquals(Collections.singleton("field2value"),
        obj.getField2());
  }
}
