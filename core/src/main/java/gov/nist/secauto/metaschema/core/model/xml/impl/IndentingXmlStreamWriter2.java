/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml.impl;

/*
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.util.StreamWriter2Delegate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.xml.stream.XMLStreamException;

public class IndentingXmlStreamWriter2
    extends StreamWriter2Delegate {
  private String indentText = DEFAULT_INDENT_TEXT;
  private String lineEndText = DEFAULT_LINE_END_TEXT;
  private int depth; // = 0;
  private final Map<Integer, Boolean> depthWithChildMap = new HashMap<>(); // NOPMD - synchronization not needed
  private static final String DEFAULT_INDENT_TEXT = "  ";
  private static final String DEFAULT_LINE_END_TEXT = "\n";

  public IndentingXmlStreamWriter2(XMLStreamWriter2 parent) {
    super(parent);
  }

  protected String getIndentText() {
    return indentText;
  }

  protected void setIndentText(String indentText) {
    Objects.requireNonNull(indentText, "indentText");
    this.indentText = indentText;
  }

  protected String getLineEndText() {
    return lineEndText;
  }

  protected void setLineEndText(String lineEndText) {
    Objects.requireNonNull(lineEndText, "lineEndText");
    this.lineEndText = lineEndText;
  }

  protected void handleStartElement() throws XMLStreamException {
    // update state of parent node
    if (depth > 0) {
      depthWithChildMap.put(depth - 1, true);
    }
    // reset state of current node
    depthWithChildMap.put(depth, false);
    // indent for current depth
    getParent().writeCharacters(getLineEndText());
    getParent().writeCharacters(getIndentText().repeat(depth));
    depth++;
  }

  @Override
  public void writeStartElement(String localName) throws XMLStreamException {
    handleStartElement();
    super.writeStartElement(localName);
  }

  @Override
  public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
    handleStartElement();
    super.writeStartElement(namespaceURI, localName);
  }

  @Override
  public void writeStartElement(String prefix,
      String localName,
      String namespaceURI) throws XMLStreamException {
    handleStartElement();
    super.writeStartElement(prefix, localName, namespaceURI);
  }

  protected void handleEndElement() throws XMLStreamException {
    depth--;
    if (depthWithChildMap.get(depth)) {
      getParent().writeCharacters(getLineEndText());
      getParent().writeCharacters(getIndentText().repeat(depth));
    }
  }

  @Override
  public void writeEndElement() throws XMLStreamException {
    handleEndElement();
    super.writeEndElement();
  }

  @Override
  public void writeFullEndElement() throws XMLStreamException {
    handleEndElement();
    super.writeFullEndElement();
  }

  protected void handleEmptyElement() throws XMLStreamException {
    // update state of parent node
    if (depth > 0) {
      depthWithChildMap.put(depth - 1, true);
    }
    // indent for current depth
    getParent().writeCharacters(getLineEndText());
    getParent().writeCharacters(getIndentText().repeat(depth));
  }

  @Override
  public void writeEmptyElement(String localName) throws XMLStreamException {
    handleEmptyElement();
    super.writeEmptyElement(localName);
  }

  @Override
  public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
    handleEmptyElement();
    super.writeEmptyElement(namespaceURI, localName);
  }

  @Override
  public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
    handleEmptyElement();
    super.writeEmptyElement(prefix, localName, namespaceURI);
  }
}
*/
