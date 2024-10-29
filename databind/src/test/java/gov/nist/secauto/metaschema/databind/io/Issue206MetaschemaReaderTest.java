/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.core.JsonParser;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IMetaschemaData;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.io.json.JsonFactoryFactory;
import gov.nist.secauto.metaschema.databind.io.json.MetaschemaJsonReader;
import gov.nist.secauto.metaschema.databind.io.xml.MetaschemaXmlReader;
import gov.nist.secauto.metaschema.databind.model.AbstractBoundModule;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelFieldComplex;
import gov.nist.secauto.metaschema.databind.model.IBoundModule;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundFieldValue;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundFlag;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaField;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaModule;

import org.codehaus.stax2.XMLEventReader2;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import edu.umd.cs.findbugs.annotations.NonNull;

class Issue206MetaschemaReaderTest {
  @RegisterExtension
  JUnit5Mockery context = new JUnit5Mockery();

  @Test
  void testIssue205Json() throws IOException {
    String json = "{" +
        "   \"flag\": \"flag-value\"" +
        "}";

    IBindingContext bindingContext = IBindingContext.newInstance();
    bindingContext.registerModule(TestModule.class);

    IBoundDefinitionModelFieldComplex definition = ObjectUtils.notNull(
        (IBoundDefinitionModelFieldComplex) bindingContext.getBoundDefinitionForClass(TestField.class));

    try (InputStream is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
      try (JsonParser parser = JsonFactoryFactory.instance().createParser(is)) {
        assert parser != null;
        MetaschemaJsonReader reader = new MetaschemaJsonReader(parser);

        // assertThrows(IOException.class, () -> {
        // reader.readItemField(null, definition);
        // });
        TestField field = (TestField) reader.readItemField(null, definition);
        assertNull(field.value);
      }
    }
  }

  @Test
  void testIssue205XmlNoValue() throws IOException, XMLStreamException {
    String xml = "<test-field xmlns=\"http://example.com/\" flag=\"flag-value\"/>";

    IBindingContext bindingContext = IBindingContext.newInstance();
    bindingContext.registerModule(TestModule.class);

    IBoundDefinitionModelFieldComplex definition = ObjectUtils.notNull(
        (IBoundDefinitionModelFieldComplex) bindingContext.getBoundDefinitionForClass(TestField.class));

    XMLInputFactory factory = XMLInputFactory.newInstance();
    try (InputStream is = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))) {
      XMLEventReader2 eventReader = ObjectUtils.notNull((XMLEventReader2) factory.createXMLEventReader(is));
      MetaschemaXmlReader reader = new MetaschemaXmlReader(eventReader);

      // assertThrows(IOException.class, () -> {
      // reader.read(definition);
      // });
      TestField field = (TestField) reader.read(definition);
      assertEquals("", field.value);
    }
  }

  @Test
  void testIssue205XmlEmptyValue() throws IOException, XMLStreamException {
    String xml = "<test-field xmlns=\"http://example.com/\" flag=\"flag-value\"></test-field>";

    IBindingContext bindingContext = IBindingContext.newInstance();
    bindingContext.registerModule(TestModule.class);

    IBoundDefinitionModelFieldComplex definition = ObjectUtils.notNull(
        (IBoundDefinitionModelFieldComplex) bindingContext.getBoundDefinitionForClass(TestField.class));

    XMLInputFactory factory = XMLInputFactory.newInstance();
    try (InputStream is = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))) {
      XMLEventReader2 eventReader = ObjectUtils.requireNonNull((XMLEventReader2) factory.createXMLEventReader(is));
      MetaschemaXmlReader reader = new MetaschemaXmlReader(eventReader);

      TestField field = (TestField) reader.read(definition);
      assertEquals("", field.value);
    }
  }

  @MetaschemaModule(fields = { TestField.class })
  public static class TestModule
      extends AbstractBoundModule {
    @NonNull
    private static final URI NAMESPACE = ObjectUtils.notNull(URI.create("http://example.com/"));

    public TestModule(
        @NonNull List<? extends IBoundModule> importedModules,
        @NonNull IBindingContext bindingContext) {
      super(importedModules, bindingContext);
    }

    @Override
    public MarkupLine getName() {
      return MarkupLine.fromMarkdown("test-module");
    }

    @Override
    public String getVersion() {
      return "0.0.0";
    }

    @Override
    public MarkupMultiline getRemarks() {
      return null;
    }

    @Override
    public String getShortName() {
      return "test-module";
    }

    @Override
    public URI getXmlNamespace() {
      return NAMESPACE;
    }

    @Override
    public URI getJsonBaseUri() {
      return NAMESPACE;
    }
  }

  @MetaschemaField(name = "test-field", moduleClass = TestModule.class)
  public static class TestField implements IBoundObject {
    @SuppressWarnings("unused")
    public TestField(IMetaschemaData data) {
      // do nothing
    }

    @Override
    public IMetaschemaData getMetaschemaData() {
      return null;
    }

    @BoundFlag
    String flag;

    @BoundFieldValue
    String value;
  }
}
