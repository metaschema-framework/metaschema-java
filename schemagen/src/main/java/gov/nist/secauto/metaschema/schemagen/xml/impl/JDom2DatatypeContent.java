/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.impl;

import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.StAXStreamOutputter;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents datatype content backed by JDOM2 elements.
 * <p>
 * This class stores XML Schema datatype definitions as JDOM2 elements and
 * provides the capability to write them to an XML stream.
 */
public class JDom2DatatypeContent
    extends AbstractDatatypeContent {

  @NonNull
  private final List<Element> content;

  /**
   * Constructs a new JDOM2-backed datatype content instance.
   *
   * @param typeName
   *          the name of the datatype
   * @param content
   *          the list of JDOM2 elements representing the datatype definition
   * @param dependencies
   *          the list of datatype names that this datatype depends on
   */
  public JDom2DatatypeContent(
      @NonNull String typeName,
      @NonNull List<Element> content,
      @NonNull List<String> dependencies) {
    super(typeName, dependencies);
    this.content = CollectionUtil.unmodifiableList(new ArrayList<>(content));
  }

  /**
   * Retrieves the JDOM2 elements representing the datatype content.
   *
   * @return an unmodifiable list of JDOM2 elements
   */
  protected List<Element> getContent() {
    return content;
  }

  @Override
  public void write(@NonNull XMLStreamWriter writer) throws XMLStreamException {
    Format format = Format.getRawFormat();
    format.setOmitDeclaration(true);

    StAXStreamOutputter out = new StAXStreamOutputter(format);

    for (Element content : getContent()) {
      out.output(content, writer);
    }
  }
}
