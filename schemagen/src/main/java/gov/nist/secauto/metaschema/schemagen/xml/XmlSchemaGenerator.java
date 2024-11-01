/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml; // NOPMD

import com.ctc.wstx.stax.WstxOutputFactory;

import gov.nist.secauto.metaschema.core.configuration.IConfiguration;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.util.AutoCloser;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.schemagen.AbstractSchemaGenerator;
import gov.nist.secauto.metaschema.schemagen.SchemaGenerationException;
import gov.nist.secauto.metaschema.schemagen.SchemaGenerationFeature;
import gov.nist.secauto.metaschema.schemagen.xml.impl.XmlGenerationState;
import gov.nist.secauto.metaschema.schemagen.xml.impl.schematype.IXmlType;

import org.codehaus.stax2.XMLOutputFactory2;
import org.codehaus.stax2.XMLStreamWriter2;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class XmlSchemaGenerator
    extends AbstractSchemaGenerator<
        AutoCloser<XMLStreamWriter2, SchemaGenerationException>,
        XmlDatatypeManager,
        XmlGenerationState> {
  // private static final Logger LOGGER =
  // LogManager.getLogger(XmlSchemaGenerator.class);

  @NonNull
  public static final String PREFIX_XML_SCHEMA = "xs";
  @NonNull
  public static final String NS_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
  @NonNull
  private static final String PREFIX_XML_SCHEMA_VERSIONING = "vs";
  @NonNull
  private static final String NS_XML_SCHEMA_VERSIONING = "http://www.w3.org/2007/XMLSchema-versioning";
  @NonNull
  public static final String NS_XHTML = "http://www.w3.org/1999/xhtml";

  @NonNull
  private final XMLOutputFactory2 xmlOutputFactory;

  @NonNull
  private static XMLOutputFactory2 defaultXMLOutputFactory() {
    XMLOutputFactory2 xmlOutputFactory = (XMLOutputFactory2) XMLOutputFactory.newInstance();
    assert xmlOutputFactory instanceof WstxOutputFactory;
    xmlOutputFactory.configureForSpeed();
    xmlOutputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
    return xmlOutputFactory;
  }

  public XmlSchemaGenerator() {
    this(defaultXMLOutputFactory());
  }

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public XmlSchemaGenerator(@NonNull XMLOutputFactory2 xmlOutputFactory) {
    this.xmlOutputFactory = xmlOutputFactory;
  }

  protected XMLOutputFactory2 getXmlOutputFactory() {
    return xmlOutputFactory;
  }

  @Override
  protected AutoCloser<XMLStreamWriter2, SchemaGenerationException> newWriter(
      Writer out) {
    XMLStreamWriter2 writer;
    try {
      writer = ObjectUtils.notNull((XMLStreamWriter2) getXmlOutputFactory().createXMLStreamWriter(out));
    } catch (XMLStreamException ex) {
      throw new SchemaGenerationException(ex);
    }
    return AutoCloser.autoClose(writer, t -> {
      try {
        t.close();
      } catch (XMLStreamException ex) {
        throw new SchemaGenerationException(ex);
      }
    });
  }

  @Override
  protected XmlGenerationState newGenerationState(
      IModule module,
      AutoCloser<XMLStreamWriter2, SchemaGenerationException> schemaWriter,
      IConfiguration<SchemaGenerationFeature<?>> configuration) {
    return new XmlGenerationState(module, schemaWriter, configuration);
  }

  @Override
  public void generateFromModule(
      @NonNull IModule module,
      @NonNull Writer out,
      @NonNull IConfiguration<SchemaGenerationFeature<?>> configuration) {
    // super.generateFromModule(module, out, configuration);

    String generatedSchema;
    try (StringWriter stringWriter = new StringWriter()) {
      super.generateFromModule(module, stringWriter, configuration);
      generatedSchema = stringWriter.toString();
    } catch (IOException ex) {
      throw new SchemaGenerationException(ex);
    }

    try (InputStream is = getClass().getResourceAsStream("/identity.xsl")) {
      Source xsltSource = new StreamSource(is);

      // TransformerFactory transformerFactory = TransformerFactory.newInstance();
      TransformerFactory transformerFactory = new net.sf.saxon.TransformerFactoryImpl();
      Transformer transformer = transformerFactory.newTransformer(xsltSource);

      try (StringReader stringReader = new StringReader(generatedSchema)) {
        Source xmlSource = new StreamSource(stringReader);

        StreamResult result = new StreamResult(out);
        transformer.transform(xmlSource, result);
      } catch (TransformerException ex) {
        throw new SchemaGenerationException(ex);
      }
    } catch (IOException | TransformerConfigurationException ex) {
      throw new SchemaGenerationException(ex);
    }
  }

  @Override
  protected void generateSchema(XmlGenerationState state) {

    try {
      String targetNS = state.getDefaultNS();

      // analyze all definitions
      Map<String, String> prefixToNamespaceMap = new HashMap<>(); // NOPMD concurrency not needed
      final List<IAssemblyDefinition> rootAssemblyDefinitions = analyzeDefinitions(
          state,
          (entry, definition) -> {
            assert entry != null;
            assert definition != null;
            IXmlType type = state.getXmlForDefinition(definition);
            if (!entry.isInline()) {
              QName qname = type.getQName();
              String namespace = qname.getNamespaceURI();
              if (!targetNS.equals(namespace)) {
                // collect namespaces and prefixes for definitions with a different namespace
                prefixToNamespaceMap.computeIfAbsent(qname.getPrefix(), x -> namespace);
              }
            }
          });

      // write some root elements
      XMLStreamWriter2 writer = state.getXMLStreamWriter();
      writer.writeStartDocument("UTF-8", "1.0");
      writer.writeStartElement(PREFIX_XML_SCHEMA, "schema", NS_XML_SCHEMA);
      writer.writeDefaultNamespace(targetNS);
      writer.writeNamespace(PREFIX_XML_SCHEMA_VERSIONING, NS_XML_SCHEMA_VERSIONING);

      // write namespaces for all indexed definitions
      for (Map.Entry<String, String> entry : prefixToNamespaceMap.entrySet()) {
        state.writeNamespace(entry.getKey(), entry.getValue());
      }

      IModule module = state.getModule();

      // write remaining root attributes
      writer.writeAttribute("targetNamespace", targetNS);
      writer.writeAttribute("elementFormDefault", "qualified");
      writer.writeAttribute(NS_XML_SCHEMA_VERSIONING, "minVersion", "1.0");
      writer.writeAttribute(NS_XML_SCHEMA_VERSIONING, "maxVersion", "1.1");
      writer.writeAttribute("version", module.getVersion());

      generateSchemaMetadata(module, state);

      for (IAssemblyDefinition definition : rootAssemblyDefinitions) {
        QName xmlQName = definition.getRootXmlQName();
        if (xmlQName != null
            && (xmlQName.getNamespaceURI() == null || state.getDefaultNS().equals(xmlQName.getNamespaceURI()))) {
          generateRootElement(definition, state);
        }
      }

      state.generateXmlTypes();

      writer.writeEndElement(); // xs:schema
      writer.writeEndDocument();
      writer.flush();
    } catch (XMLStreamException ex) {
      throw new SchemaGenerationException(ex);
    }
  }

  protected static void generateSchemaMetadata(
      @NonNull IModule module,
      @NonNull XmlGenerationState state)
      throws XMLStreamException {
    String targetNS = ObjectUtils.notNull(module.getXmlNamespace().toASCIIString());
    state.writeStartElement(PREFIX_XML_SCHEMA, "annotation", NS_XML_SCHEMA);
    state.writeStartElement(PREFIX_XML_SCHEMA, "appinfo", NS_XML_SCHEMA);

    state.writeStartElement(targetNS, "schema-name");

    module.getName().writeXHtml(targetNS, state.getXMLStreamWriter());

    state.writeEndElement();

    state.writeStartElement(targetNS, "schema-version");
    state.writeCharacters(module.getVersion());
    state.writeEndElement();

    state.writeStartElement(targetNS, "short-name");
    state.writeCharacters(module.getShortName());
    state.writeEndElement();

    state.writeEndElement();

    MarkupMultiline remarks = module.getRemarks();
    if (remarks != null) {
      state.writeStartElement(PREFIX_XML_SCHEMA, "documentation", NS_XML_SCHEMA);

      remarks.writeXHtml(targetNS, state.getXMLStreamWriter());
      state.writeEndElement();
    }

    state.writeEndElement();
  }

  private static void generateRootElement(@NonNull IAssemblyDefinition definition, @NonNull XmlGenerationState state)
      throws XMLStreamException {
    assert definition.isRoot();

    XMLStreamWriter2 writer = state.getXMLStreamWriter();
    QName xmlQName = definition.getRootXmlQName();

    writer.writeStartElement(PREFIX_XML_SCHEMA, "element", NS_XML_SCHEMA);
    writer.writeAttribute("name", xmlQName.getLocalPart());
    writer.writeAttribute("type", state.getXmlForDefinition(definition).getTypeReference());

    writer.writeEndElement();
  }
}
