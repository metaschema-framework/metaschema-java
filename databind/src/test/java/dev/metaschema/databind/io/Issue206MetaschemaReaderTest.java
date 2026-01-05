/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.core.JsonParser;

import dev.metaschema.core.datatype.markup.MarkupLine;
import dev.metaschema.core.datatype.markup.MarkupMultiline;
import dev.metaschema.core.model.IBoundObject;
import dev.metaschema.core.model.IMetaschemaData;
import dev.metaschema.core.model.MetaschemaException;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.IBindingContext;
import dev.metaschema.databind.io.json.JsonFactoryFactory;
import dev.metaschema.databind.io.json.MetaschemaJsonReader;
import dev.metaschema.databind.io.xml.MetaschemaXmlReader;
import dev.metaschema.databind.model.AbstractBoundModule;
import dev.metaschema.databind.model.IBoundDefinitionModelFieldComplex;
import dev.metaschema.databind.model.IBoundModule;
import dev.metaschema.databind.model.annotations.BoundFieldValue;
import dev.metaschema.databind.model.annotations.BoundFlag;
import dev.metaschema.databind.model.annotations.MetaschemaField;
import dev.metaschema.databind.model.annotations.MetaschemaModule;

import org.codehaus.stax2.XMLEventReader2;
import org.jmock.junit5.JUnit5Mockery;
import org.jmock.lib.concurrent.Synchroniser;
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
  JUnit5Mockery context = new JUnit5Mockery() {
    {
      setThreadingPolicy(new Synchroniser());
    }
  };

  @Test
  void testIssue205Json() throws IOException, MetaschemaException {
    String json = "{" +
        "   \"flag\": \"flag-value\"" +
        "}";
    URI source = ObjectUtils.notNull(URI.create("https://example.com/not-a-resource"));

    IBindingContext bindingContext = IBindingContext.newInstance();
    bindingContext.registerModule(TestModule.class);

    IBoundDefinitionModelFieldComplex definition = ObjectUtils.notNull(
        (IBoundDefinitionModelFieldComplex) bindingContext.getBoundDefinitionForClass(TestField.class));

    try (InputStream is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
      try (JsonParser parser = JsonFactoryFactory.instance().createParser(is)) {
        assert parser != null;
        MetaschemaJsonReader reader = new MetaschemaJsonReader(parser, source);

        // assertThrows(IOException.class, () -> {
        // reader.readItemField(null, definition);
        // });
        TestField field = (TestField) reader.readItemField(null, definition);
        assertNull(field.value);
      }
    }
  }

  @Test
  void testIssue205XmlNoValue() throws IOException, XMLStreamException, MetaschemaException {
    String xml = "<test-field xmlns=\"http://example.com/\" flag=\"flag-value\"/>";
    URI source = ObjectUtils.notNull(URI.create("https://example.com/not-a-resource"));

    IBindingContext bindingContext = IBindingContext.newInstance();
    bindingContext.registerModule(TestModule.class);

    IBoundDefinitionModelFieldComplex definition = ObjectUtils.notNull(
        (IBoundDefinitionModelFieldComplex) bindingContext.getBoundDefinitionForClass(TestField.class));

    XMLInputFactory factory = XMLInputFactory.newInstance();
    try (InputStream is = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))) {
      XMLEventReader2 eventReader = ObjectUtils.notNull((XMLEventReader2) factory.createXMLEventReader(is));
      MetaschemaXmlReader reader = new MetaschemaXmlReader(eventReader, source);

      // assertThrows(IOException.class, () -> {
      // reader.read(definition);
      // });
      TestField field = (TestField) reader.read(definition);
      assertEquals("", field.value);
    }
  }

  @Test
  void testIssue205XmlEmptyValue() throws IOException, XMLStreamException, MetaschemaException {
    String xml = "<test-field xmlns=\"http://example.com/\" flag=\"flag-value\"></test-field>";
    URI source = ObjectUtils.notNull(URI.create("https://example.com/not-a-resource"));

    IBindingContext bindingContext = IBindingContext.newInstance();
    bindingContext.registerModule(TestModule.class);

    IBoundDefinitionModelFieldComplex definition = ObjectUtils.notNull(
        (IBoundDefinitionModelFieldComplex) bindingContext.getBoundDefinitionForClass(TestField.class));

    XMLInputFactory factory = XMLInputFactory.newInstance();
    try (InputStream is = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))) {
      XMLEventReader2 eventReader = ObjectUtils.requireNonNull((XMLEventReader2) factory.createXMLEventReader(is));
      MetaschemaXmlReader reader = new MetaschemaXmlReader(eventReader, source);

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
