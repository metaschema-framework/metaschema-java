/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.model.util.JsonUtil;
import gov.nist.secauto.metaschema.core.model.util.XmlEventUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.codehaus.stax2.XMLEventReader2;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.evt.XMLEventFactory2;

import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides a basic Java type adapter implementation. This implementation should
 * be the parent class of all Java type adapter implementations.
 *
 * @param <TYPE>
 *          the raw Java type this adapter supports
 * @param <ITEM_TYPE>
 *          the metapath item type corresponding to the raw Java type supported
 *          by the adapter
 */
public abstract class AbstractDataTypeAdapter<TYPE, ITEM_TYPE extends IAnyAtomicItem>
    implements IDataTypeAdapter<TYPE> {
  /**
   * The default JSON property name for a Metaschema field value.
   */
  public static final String DEFAULT_JSON_FIELD_VALUE_NAME = "STRVALUE";

  @NonNull
  private final Class<TYPE> clazz;

  /**
   * Construct a new Java type adapter for a provided class.
   *
   * @param clazz
   *          the Java type this adapter supports
   */
  protected AbstractDataTypeAdapter(@NonNull Class<TYPE> clazz) {
    this.clazz = clazz;
  }

  @Override
  @SuppressWarnings("unchecked")
  public TYPE toValue(Object value) {
    return (TYPE) value;
  }

  @Override
  public Class<TYPE> getJavaClass() {
    return clazz;
  }

  @Override
  public boolean canHandleQName(QName nextQName) {
    return false;
  }

  @Override
  public String getDefaultJsonValueKey() {
    return DEFAULT_JSON_FIELD_VALUE_NAME;
  }

  @Override
  public boolean isUnrappedValueAllowedInXml() {
    return false;
  }

  @Override
  public boolean isXmlMixed() {
    return false;
  }

  @Override
  public TYPE parse(XMLEventReader2 eventReader) throws IOException {
    StringBuilder builder = new StringBuilder();
    XMLEvent nextEvent;
    try {
      while (!(nextEvent = eventReader.peek()).isEndElement()) {
        if (!nextEvent.isCharacters()) {
          throw new IOException(String.format("Invalid content '%s' at %s",
              XmlEventUtil.toString(nextEvent),
              XmlEventUtil.toString(nextEvent.getLocation())));
        }
        Characters characters = nextEvent.asCharacters();
        builder.append(characters.getData());
        // advance past current event
        eventReader.nextEvent();
      }

      // trim leading and trailing whitespace
      String value = ObjectUtils.notNull(builder.toString().trim());
      try {
        return parse(value);
      } catch (IllegalArgumentException ex) {
        throw new IOException(
            String.format("Malformed data '%s'%s. %s",
                value,
                XmlEventUtil.generateLocationMessage(nextEvent),
                ex.getLocalizedMessage()),
            ex);
      }
    } catch (XMLStreamException ex) {
      throw new IOException(ex);
    }
  }

  /**
   * This default implementation will parse the value as a string and delegate to
   * the string-based parsing method.
   */
  @Override
  public TYPE parse(JsonParser parser) throws IOException {
    String value = parser.getValueAsString();
    if (value == null) {
      throw new IOException(
          String.format("Unable to get null value as text%s",
              JsonUtil.generateLocationMessage(parser)));
    }
    // skip over value
    parser.nextToken();
    try {
      return parse(value);
    } catch (IllegalArgumentException ex) {
      throw new IOException(
          String.format("Malformed data '%s'%s. %s",
              value,
              JsonUtil.generateLocationMessage(parser),
              ex.getLocalizedMessage()),
          ex);
    }
  }

  @SuppressWarnings("null")
  @Override
  public String asString(Object value) {
    return value.toString();
  }

  @Override
  public void writeXmlValue(
      Object value,
      StartElement parent,
      XMLEventFactory2 eventFactory,
      XMLEventWriter eventWriter)
      throws IOException {
    try {
      String content = asString(value);
      Characters characters = eventFactory.createCharacters(content);
      eventWriter.add(characters);
    } catch (IllegalArgumentException | XMLStreamException ex) {
      throw new IOException(ex);
    }
  }

  @Override
  public void writeXmlValue(Object value, QName parentName, XMLStreamWriter2 writer) throws IOException {
    String content;
    try {
      content = asString(value);
      writer.writeCharacters(content);
    } catch (IllegalArgumentException | XMLStreamException ex) {
      throw new IOException(ex);
    }
  }

  @Override
  public void writeJsonValue(Object value, JsonGenerator generator) throws IOException {
    try {
      generator.writeString(asString(value));
    } catch (IllegalArgumentException ex) {
      throw new IOException(ex);
    }
  }

  @Override
  public abstract Class<ITEM_TYPE> getItemClass();

  @Override
  public abstract ITEM_TYPE newItem(Object value);

  @SuppressWarnings("unchecked")
  @Override
  public ITEM_TYPE cast(IAnyAtomicItem item) {
    if (item == null) {
      throw new InvalidValueForCastFunctionException("item is null");
    }
    return getItemClass().isAssignableFrom(item.getClass())
        ? (ITEM_TYPE) item
        : castInternal(item);
  }

  /**
   * Attempt to cast the provided item to this adapter's item type.
   * <p>
   * The default implementation of this will attempt to parse the provided item as
   * a string using the {@link #parse(String)} method. If this behavior is
   * undesirable, then a subclass should override this method.
   *
   * @param item
   *          the item to cast
   * @return the item casted to this adapter's item type
   * @throws InvalidValueForCastFunctionException
   *           if the casting of the item is not possible because the item
   *           represents an invalid value for this adapter's item type
   */
  @NonNull
  protected ITEM_TYPE castInternal(@NonNull IAnyAtomicItem item) {
    // try string based casting as a fallback
    String itemString = null;
    try {
      itemString = item.asString();
      TYPE value = parse(itemString);
      return newItem(value);
    } catch (IllegalArgumentException | IllegalStateException ex) {
      throw new InvalidValueForCastFunctionException(
          String.format("The value '%s' is not compatible with the type '%s'",
              item.getValue(),
              getItemClass().getName()),
          ex);
    }
  }
}
