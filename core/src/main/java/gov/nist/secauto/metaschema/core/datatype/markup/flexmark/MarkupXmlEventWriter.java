/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.markup.flexmark;

import com.vladsch.flexmark.parser.ListOptions;

import gov.nist.secauto.metaschema.core.datatype.markup.flexmark.impl.AbstractMarkupWriter;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.codehaus.stax2.evt.XMLEventFactory2;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;

import edu.umd.cs.findbugs.annotations.NonNull;

public class MarkupXmlEventWriter
    extends AbstractMarkupWriter<XMLEventWriter, XMLStreamException> {

  @NonNull
  private final XMLEventFactory2 eventFactory;

  /**
   * Construct a new event writer.
   *
   * @param namespace
   *          the XML namespace to use for XMHTML content
   * @param listOptions
   *          list production options
   * @param writer
   *          the XML event stream to write to
   * @param eventFactory
   *          the XML event factory used to generate XML events
   */
  public MarkupXmlEventWriter(
      @NonNull String namespace,
      @NonNull ListOptions listOptions,
      @NonNull XMLEventWriter writer,
      @NonNull XMLEventFactory2 eventFactory) {
    super(namespace, listOptions, writer);
    this.eventFactory = Objects.requireNonNull(eventFactory, "eventFactory");
  }

  /**
   * Get the XML event factory used to generate XML events.
   *
   * @return the XML event factory
   */
  @NonNull
  protected XMLEventFactory2 getEventFactory() {
    return eventFactory;
  }

  /**
   * Get XML events for the provided attributes.
   *
   * @param attributes
   *          the mapping of attribute name to value
   * @return the list of attribute events
   */
  @NonNull
  protected List<Attribute> handleAttributes(@NonNull Map<String, String> attributes) {
    List<Attribute> attrs;
    if (attributes.isEmpty()) {
      attrs = CollectionUtil.emptyList();
    } else {
      attrs = ObjectUtils.notNull(attributes.entrySet().stream()
          .map(entry -> eventFactory.createAttribute(entry.getKey(), entry.getValue()))
          .collect(Collectors.toList()));
    }
    return attrs;
  }

  @Override
  public void writeEmptyElement(QName qname, Map<String, String> attributes) throws XMLStreamException {
    List<Attribute> attrs = handleAttributes(attributes);
    StartElement start = eventFactory.createStartElement(qname, attrs.isEmpty() ? null : attrs.iterator(), null);

    XMLEventWriter stream = getStream();
    stream.add(start);

    EndElement end = eventFactory.createEndElement(qname, null);
    stream.add(end);
  }

  @Override
  public void writeElementStart(QName qname, Map<String, String> attributes) throws XMLStreamException {
    List<Attribute> attrs = handleAttributes(attributes);
    StartElement start = eventFactory.createStartElement(qname, attrs.isEmpty() ? null : attrs.iterator(), null);
    getStream().add(start);
  }

  @Override
  public void writeElementEnd(QName qname) throws XMLStreamException {
    EndElement end = eventFactory.createEndElement(qname, null);
    getStream().add(end);
  }

  @Override
  public void writeText(CharSequence text) throws XMLStreamException {
    getStream().add(eventFactory.createCharacters(text.toString()));
  }

  @Override
  protected void writeHtmlEntityInternal(String entityText) throws XMLStreamException {
    getStream().add(eventFactory.createEntityReference(entityText, null));
  }

  @Override
  protected void writeComment(CharSequence text) throws XMLStreamException {
    getStream().add(eventFactory.createComment(text.toString()));
  }
}
