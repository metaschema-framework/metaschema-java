/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.markup.flexmark;

import com.vladsch.flexmark.parser.ListOptions;

import gov.nist.secauto.metaschema.core.datatype.markup.flexmark.impl.AbstractMarkupWriter;

import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import edu.umd.cs.findbugs.annotations.NonNull;

// TODO: Is this orphaned code needed?
public class MarkupXmlStreamWriter
    extends AbstractMarkupWriter<XMLStreamWriter, XMLStreamException> {

  /**
   * Construct a new markup stream writer.
   *
   * @param namespace
   *          the XML namespace to use for markup elements
   * @param listOptions
   *          options controling markup list output
   * @param writer
   *          the writer used to serialize markup
   */
  public MarkupXmlStreamWriter(
      @NonNull String namespace,
      @NonNull ListOptions listOptions,
      @NonNull XMLStreamWriter writer) {
    super(namespace, listOptions, writer);
  }

  @Override
  public void writeEmptyElement(QName qname, Map<String, String> attributes) throws XMLStreamException {
    XMLStreamWriter stream = getStream();
    stream.writeEmptyElement(qname.getNamespaceURI(), qname.getLocalPart());

    for (Map.Entry<String, String> entry : attributes.entrySet()) {
      stream.writeAttribute(entry.getKey(), entry.getValue());
    }
  }

  @Override
  public void writeElementStart(QName qname, Map<String, String> attributes) throws XMLStreamException {
    XMLStreamWriter stream = getStream();
    stream.writeStartElement(qname.getNamespaceURI(), qname.getLocalPart());

    for (Map.Entry<String, String> entry : attributes.entrySet()) {
      stream.writeAttribute(entry.getKey(), entry.getValue());
    }
  }

  @Override
  public void writeElementEnd(QName qname) throws XMLStreamException {
    getStream().writeEndElement();
  }

  @Override
  public void writeText(CharSequence text) throws XMLStreamException {
    getStream().writeCharacters(text.toString());

  }

  @Override
  protected void writeHtmlEntityInternal(String entityText) throws XMLStreamException {
    getStream().writeEntityRef(entityText);
  }

  @Override
  protected void writeComment(CharSequence text) throws XMLStreamException {
    getStream().writeComment(text.toString());
  }
}
