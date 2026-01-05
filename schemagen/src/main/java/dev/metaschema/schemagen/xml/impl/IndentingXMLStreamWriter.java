/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen.xml.impl;

import java.util.ArrayDeque;
import java.util.Deque;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An XMLStreamWriter wrapper that adds indentation to the output.
 * <p>
 * This wrapper handles mixed content correctly by tracking when text has been
 * written to an element. When an element contains text (mixed content), no
 * indentation is added to preserve the text formatting.
 * <p>
 * This class is used to replace Saxon XSLT post-processing for schema
 * indentation, providing streaming indentation without buffering the entire
 * document.
 */
public class IndentingXMLStreamWriter implements XMLStreamWriter, AutoCloseable {

  private static final String NEWLINE = "\n";
  private static final String INDENT = "  ";

  @NonNull
  private final XMLStreamWriter delegate;

  private int depth;
  private final Deque<Boolean> hasTextStack = new ArrayDeque<>();
  private boolean hasText;
  private boolean lastWasStart;

  /**
   * Constructs a new indenting XML stream writer.
   *
   * @param delegate
   *          the underlying writer to delegate to
   */
  public IndentingXMLStreamWriter(@NonNull XMLStreamWriter delegate) {
    this.delegate = delegate;
    this.depth = 0;
    this.hasText = false;
    this.lastWasStart = false;
  }

  /**
   * Writes indentation at the current depth level.
   *
   * @throws XMLStreamException
   *           if an error occurs writing
   */
  private void writeIndent() throws XMLStreamException {
    delegate.writeCharacters(NEWLINE);
    for (int i = 0; i < depth; i++) {
      delegate.writeCharacters(INDENT);
    }
  }

  @Override
  public void writeStartElement(String localName) throws XMLStreamException {
    prepareStartElement();
    delegate.writeStartElement(localName);
    afterStartElement();
  }

  @Override
  public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
    prepareStartElement();
    delegate.writeStartElement(namespaceURI, localName);
    afterStartElement();
  }

  @Override
  public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
    prepareStartElement();
    delegate.writeStartElement(prefix, localName, namespaceURI);
    afterStartElement();
  }

  /**
   * Prepares for writing a start element by adding indentation if appropriate.
   *
   * @throws XMLStreamException
   *           if an error occurs writing
   */
  private void prepareStartElement() throws XMLStreamException {
    if (!hasText) {
      writeIndent();
    }
    hasTextStack.push(hasText);
  }

  /**
   * Updates state after writing a start element.
   */
  private void afterStartElement() {
    depth++;
    hasText = false;
    lastWasStart = true;
  }

  @Override
  public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
    if (!hasText) {
      writeIndent();
    }
    delegate.writeEmptyElement(namespaceURI, localName);
    lastWasStart = false;
  }

  @Override
  public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
    if (!hasText) {
      writeIndent();
    }
    delegate.writeEmptyElement(prefix, localName, namespaceURI);
    lastWasStart = false;
  }

  @Override
  public void writeEmptyElement(String localName) throws XMLStreamException {
    if (!hasText) {
      writeIndent();
    }
    delegate.writeEmptyElement(localName);
    lastWasStart = false;
  }

  @Override
  public void writeEndElement() throws XMLStreamException {
    depth--;
    boolean parentHasText = hasTextStack.isEmpty() ? false : hasTextStack.pop();

    if (!hasText && !lastWasStart) {
      writeIndent();
    }
    delegate.writeEndElement();
    hasText = parentHasText;
    lastWasStart = false;
  }

  @Override
  public void writeEndDocument() throws XMLStreamException {
    delegate.writeEndDocument();
  }

  @Override
  public void close() throws XMLStreamException {
    delegate.close();
  }

  @Override
  public void flush() throws XMLStreamException {
    delegate.flush();
  }

  @Override
  public void writeAttribute(String localName, String value) throws XMLStreamException {
    delegate.writeAttribute(localName, value);
  }

  @Override
  public void writeAttribute(String prefix, String namespaceURI, String localName, String value)
      throws XMLStreamException {
    delegate.writeAttribute(prefix, namespaceURI, localName, value);
  }

  @Override
  public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
    delegate.writeAttribute(namespaceURI, localName, value);
  }

  @Override
  public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
    delegate.writeNamespace(prefix, namespaceURI);
  }

  @Override
  public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
    delegate.writeDefaultNamespace(namespaceURI);
  }

  @Override
  public void writeComment(String data) throws XMLStreamException {
    if (!hasText) {
      writeIndent();
    }
    delegate.writeComment(data);
    lastWasStart = false;
  }

  @Override
  public void writeProcessingInstruction(String target) throws XMLStreamException {
    if (!hasText) {
      writeIndent();
    }
    delegate.writeProcessingInstruction(target);
    lastWasStart = false;
  }

  @Override
  public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
    if (!hasText) {
      writeIndent();
    }
    delegate.writeProcessingInstruction(target, data);
    lastWasStart = false;
  }

  @Override
  public void writeCData(String data) throws XMLStreamException {
    delegate.writeCData(data);
    hasText = true;
    lastWasStart = false;
  }

  @Override
  public void writeDTD(String dtd) throws XMLStreamException {
    delegate.writeDTD(dtd);
  }

  @Override
  public void writeEntityRef(String name) throws XMLStreamException {
    delegate.writeEntityRef(name);
    hasText = true;
    lastWasStart = false;
  }

  @Override
  public void writeStartDocument() throws XMLStreamException {
    delegate.writeStartDocument();
  }

  @Override
  public void writeStartDocument(String version) throws XMLStreamException {
    delegate.writeStartDocument(version);
  }

  @Override
  public void writeStartDocument(String encoding, String version) throws XMLStreamException {
    delegate.writeStartDocument(encoding, version);
  }

  @Override
  public void writeCharacters(String text) throws XMLStreamException {
    delegate.writeCharacters(text);
    hasText = true;
    lastWasStart = false;
  }

  @Override
  public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
    delegate.writeCharacters(text, start, len);
    hasText = true;
    lastWasStart = false;
  }

  @Override
  public String getPrefix(String uri) throws XMLStreamException {
    return delegate.getPrefix(uri);
  }

  @Override
  public void setPrefix(String prefix, String uri) throws XMLStreamException {
    delegate.setPrefix(prefix, uri);
  }

  @Override
  public void setDefaultNamespace(String uri) throws XMLStreamException {
    delegate.setDefaultNamespace(uri);
  }

  @Override
  public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
    delegate.setNamespaceContext(context);
  }

  @Override
  public NamespaceContext getNamespaceContext() {
    return delegate.getNamespaceContext();
  }

  @Override
  public Object getProperty(String name) {
    return delegate.getProperty(name);
  }
}
