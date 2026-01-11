/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen.xml;

import com.ctc.wstx.stax.WstxOutputFactory;

import org.codehaus.stax2.XMLOutputFactory2;
import org.codehaus.stax2.XMLStreamWriter2;
import org.eclipse.jdt.annotation.Owning;

import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import dev.metaschema.core.configuration.IConfiguration;
import dev.metaschema.core.datatype.markup.MarkupMultiline;
import dev.metaschema.core.model.IAssemblyDefinition;
import dev.metaschema.core.model.IModule;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.AutoCloser;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.schemagen.AbstractSchemaGenerator;
import dev.metaschema.schemagen.SchemaGenerationException;
import dev.metaschema.schemagen.SchemaGenerationFeature;
import dev.metaschema.schemagen.xml.impl.IndentingXMLStreamWriter2;
import dev.metaschema.schemagen.xml.impl.XmlDatatypeManager;
import dev.metaschema.schemagen.xml.impl.XmlGenerationState;
import dev.metaschema.schemagen.xml.impl.schematype.IXmlType;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Generates XML Schema (XSD) documents from Metaschema modules.
 * <p>
 * This generator produces W3C XML Schema documents that validate XML instances
 * conforming to the Metaschema module definitions.
 */
public class XmlSchemaGenerator
    extends AbstractSchemaGenerator<
        AutoCloser<XMLStreamWriter2, SchemaGenerationException>,
        XmlDatatypeManager,
        XmlGenerationState> {
  // private static final Logger LOGGER =
  // LogManager.getLogger(XmlSchemaGenerator.class);

  /** The namespace prefix for XML Schema elements. */
  @NonNull
  public static final String PREFIX_XML_SCHEMA = XmlDatatypeManager.PREFIX_XML_SCHEMA;
  /** The XML Schema namespace URI. */
  @NonNull
  public static final String NS_XML_SCHEMA = XmlDatatypeManager.NS_XML_SCHEMA;
  @NonNull
  private static final String PREFIX_XML_SCHEMA_VERSIONING = "vs";
  @NonNull
  private static final String NS_XML_SCHEMA_VERSIONING = "http://www.w3.org/2007/XMLSchema-versioning";
  /** The XHTML namespace URI used for documentation content. */
  @NonNull
  public static final String NS_XHTML = XmlDatatypeManager.NS_XHTML;

  @NonNull
  private final XMLOutputFactory2 xmlOutputFactory;

  /**
   * Creates and configures a default XML output factory for schema generation.
   *
   * @return a configured XML output factory
   */
  @NonNull
  private static XMLOutputFactory2 defaultXMLOutputFactory() {
    WstxOutputFactory xmlOutputFactory = new WstxOutputFactory();
    xmlOutputFactory.configureForSpeed();
    xmlOutputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
    return xmlOutputFactory;
  }

  /**
   * Constructs a new XML schema generator using the default XML output factory.
   */
  public XmlSchemaGenerator() {
    this(defaultXMLOutputFactory());
  }

  /**
   * Constructs a new XML schema generator using the specified XML output factory.
   *
   * @param xmlOutputFactory
   *          the XML output factory to use for creating XML writers
   */
  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public XmlSchemaGenerator(@NonNull XMLOutputFactory2 xmlOutputFactory) {
    this.xmlOutputFactory = xmlOutputFactory;
  }

  /**
   * Retrieves the XML output factory used by this generator.
   *
   * @return the XML output factory
   */
  protected XMLOutputFactory2 getXmlOutputFactory() {
    return xmlOutputFactory;
  }

  @Override
  @Owning
  protected AutoCloser<XMLStreamWriter2, SchemaGenerationException> newWriter(
      Writer out) {
    XMLStreamWriter2 writer;
    try {
      XMLStreamWriter2 baseWriter
          = ObjectUtils.notNull((XMLStreamWriter2) getXmlOutputFactory().createXMLStreamWriter(out));
      writer = new IndentingXMLStreamWriter2(baseWriter);
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
        IEnhancedQName xmlQName = definition.getRootQName();
        if (xmlQName != null
            && state.getDefaultNS().equals(xmlQName.getNamespace())) {
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

  /**
   * Generates the schema metadata annotation containing module information.
   * <p>
   * This includes the schema name, version, short name, and optional remarks.
   *
   * @param module
   *          the Metaschema module to extract metadata from
   * @param state
   *          the XML generation state for writing output
   * @throws XMLStreamException
   *           if an error occurs while writing XML content
   */
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
    IEnhancedQName xmlQName = definition.getRootQName();

    writer.writeStartElement(PREFIX_XML_SCHEMA, "element", NS_XML_SCHEMA);
    writer.writeAttribute("name", xmlQName.getLocalName());
    writer.writeAttribute("type", state.getXmlForDefinition(definition).getTypeReference());

    writer.writeEndElement();
  }
}
