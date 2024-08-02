/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.datatype;

import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.schemagen.datatype.AbstractDatatypeContent;

import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.StAXStreamOutputter;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import edu.umd.cs.findbugs.annotations.NonNull;

public class JDom2DatatypeContent
    extends AbstractDatatypeContent {

  @NonNull
  private final List<Element> content;

  public JDom2DatatypeContent(
      @NonNull String typeName,
      @NonNull List<Element> content,
      @NonNull List<String> dependencies) {
    super(typeName, dependencies);
    this.content = CollectionUtil.unmodifiableList(new ArrayList<>(content));
  }

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
