/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.markup;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;

import gov.nist.secauto.metaschema.core.datatype.AbstractCustomJavaDataTypeAdapter;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IMarkupItem;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.evt.XMLEventFactory2;

import java.io.IOException;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides base support for the Metaschema <a href=
 * "https://pages.nist.gov/metaschema/specification/datatypes/#markup-data-types">markup</a>
 * data types.
 */
abstract class AbstractMarkupAdapter<
    TYPE extends IMarkupString<TYPE>,
    ITEM_TYPE extends IMarkupItem>
    extends AbstractCustomJavaDataTypeAdapter<TYPE, ITEM_TYPE> {

  /**
   * Construct a new adapter.
   *
   * @param valueClass
   *          the Java value object type this adapter supports
   * @param itemClass
   *          the Java type of the Metapath item this adapter supports
   * @param castExecutor
   *          the method to call to cast an item to an item based on this type
   */
  protected AbstractMarkupAdapter(
      @NonNull Class<TYPE> valueClass,
      @NonNull Class<ITEM_TYPE> itemClass,
      @NonNull IAtomicOrUnionType.ICastExecutor<ITEM_TYPE> castExecutor) {
    super(valueClass, itemClass, castExecutor);
  }

  @Override
  public JsonFormatTypes getJsonRawType() {
    return JsonFormatTypes.STRING;
  }

  @Override
  public boolean isXmlMixed() {
    return true;
  }

  @Override
  public void writeXmlValue(
      Object value,
      StartElement parent,
      XMLEventFactory2 eventFactory,
      XMLEventWriter eventWriter)
      throws IOException {

    IMarkupString<?> markupString = (IMarkupString<?>) value;

    try {
      markupString.writeXHtml(
          ObjectUtils.notNull(parent.getName().getNamespaceURI()),
          eventFactory,
          eventWriter);
    } catch (XMLStreamException ex) {
      throw new IOException(ex);
    }
  }

  @Override
  public void writeXmlValue(Object value, IEnhancedQName parentName, XMLStreamWriter2 streamWriter)
      throws IOException {
    IMarkupString<?> markupString = (IMarkupString<?>) value;

    try {
      markupString.writeXHtml(
          ObjectUtils.notNull(parentName.getNamespace()),
          streamWriter);
    } catch (XMLStreamException ex) {
      throw new IOException(ex);
    }
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
