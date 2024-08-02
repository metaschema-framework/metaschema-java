/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.markup;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;

import gov.nist.secauto.metaschema.core.datatype.AbstractCustomJavaDataTypeAdapter;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IMarkupItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.evt.XMLEventFactory2;

import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractMarkupAdapter<TYPE extends IMarkupString<TYPE>>
    extends AbstractCustomJavaDataTypeAdapter<TYPE, IMarkupItem> {

  /**
   * Construct a new adapter.
   *
   * @param clazz
   *          the markup type class
   */
  protected AbstractMarkupAdapter(@NonNull Class<TYPE> clazz) {
    super(clazz);
  }

  @Override
  public JsonFormatTypes getJsonRawType() {
    return JsonFormatTypes.STRING;
  }

  @Override
  public boolean isXmlMixed() {
    return true;
  }

  // TODO: verify that read/write methods cannot be generalized in the base class
  @Override
  public void writeXmlValue(
      Object value,
      StartElement parent,
      XMLEventFactory2 eventFactory,
      XMLEventWriter eventWriter)
      throws XMLStreamException {

    IMarkupString<?> markupString = (IMarkupString<?>) value;

    markupString.writeXHtml(
        ObjectUtils.notNull(parent.getName().getNamespaceURI()),
        eventFactory,
        eventWriter);
  }

  @Override
  public void writeXmlValue(Object value, QName parentName, XMLStreamWriter2 streamWriter)
      throws XMLStreamException {
    IMarkupString<?> markupString = (IMarkupString<?>) value;

    markupString.writeXHtml(
        ObjectUtils.notNull(parentName.getNamespaceURI()),
        streamWriter);
  }

  @Override
  public void writeJsonValue(Object value, JsonGenerator generator) throws IOException {

    IMarkupString<?> markupString;
    try {
      markupString = (IMarkupString<?>) value;
    } catch (ClassCastException ex) {
      throw new IOException(ex);
    }

    generator.writeString(markupString.toMarkdown().trim());
  }
}
