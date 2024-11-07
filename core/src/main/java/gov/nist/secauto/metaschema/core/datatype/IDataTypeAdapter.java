/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;

import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.codehaus.stax2.XMLEventReader2;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.evt.XMLEventFactory2;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Supplier;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a data type implementation.
 *
 * @param <TYPE>
 *          the Java type of the underlying data value
 */
public interface IDataTypeAdapter<TYPE> {

  /**
   * Get the metaschema type names associated with this adapter. This name must be
   * unique with respect to all other metaschema types.
   * <p>
   * At least one name must be provided, with the first name being the most
   * preferred name.
   *
   * @return the name
   */
  @NonNull
  List<QName> getNames();

  /**
   * Get the most preferred name for this data type.
   *
   * @return the name
   */
  @NonNull
  default QName getPreferredName() {
    return ObjectUtils.notNull(getNames().iterator().next());
  }

  /**
   * The JSON primative type of the data type.
   *
   * @return the JSON data type
   */
  JsonFormatTypes getJsonRawType();

  /**
   * Get the Java class supported by this adapter.
   *
   * @return the Java class
   */
  @NonNull
  Class<TYPE> getJavaClass();

  /**
   * Casts the provided value to the type associated with this adapter.
   *
   * @param value
   *          a value of the provided type
   * @return the typed value
   */
  @NonNull
  TYPE toValue(@NonNull Object value);

  /**
   * Gets the value as a string suitable for writing as text. This is intended for
   * data types that have a simple string-based structure in XML and JSON, such as
   * for XML attributes or JSON keys. An adapter for a complex data structures
   * that consist of XML elements will throw an
   * {@link UnsupportedOperationException} when this is called.
   *
   * @param value
   *          the data to formatted as a string
   * @return a string
   * @throws IllegalArgumentException
   *           if the data type cannot be represented as a string
   */
  @NonNull
  String asString(@NonNull Object value);

  /**
   * Create a copy of the provided value.
   *
   * @param obj
   *          the value to copy
   * @return the copy
   */
  @NonNull
  TYPE copy(@NonNull Object obj);

  /**
   * Determines if the data type is an atomic, scalar value. Complex structures
   * such as Markup are not considered atomic.
   *
   * @return {@code true} if the data type is an atomic scalar value, or
   *         {@code false} otherwise
   */
  default boolean isAtomic() {
    return true;
  }

  /**
   * Get the java type of the associated item.
   *
   * @return the java associated item type
   */
  // TODO: move to IAnyAtomicItem
  @NonNull
  Class<? extends IAnyAtomicItem> getItemClass();

  /**
   * Construct a new item of this type using the provided value.
   *
   * @param value
   *          the item's value
   * @return a new item
   */
  // TODO: markup types are not atomic values.
  // Figure out a better base type (i.e., IValuedItem)
  // TODO: move to IAnyAtomicItem
  @NonNull
  IAnyAtomicItem newItem(@NonNull Object value);

  /**
   * Cast the provided item to an item of this type, if possible.
   *
   * @param item
   *          the atomic item to cast
   * @return an atomic item of this type
   * @throws InvalidValueForCastFunctionException
   *           if the provided item type cannot be cast to this item type
   */
  // TODO: move to IAnyAtomicItem
  @NonNull
  IAnyAtomicItem cast(IAnyAtomicItem item);

  /**
   * Determines if adapter can parse the next element. The next element's
   * {@link QName} is provided for this purpose.
   * <p>
   * This will be called when the parser encounter's an element it does not
   * recognize. This gives the adapter a chance to request parsing of the data.
   *
   * @param nextElementQName
   *          the next element's namespace-qualified name
   * @return {@code true} if the adapter will parse the element, or {@code false}
   *         otherwise
   */
  boolean canHandleQName(@NonNull QName nextElementQName);

  /**
   * Parses a provided string. Used to parse XML attributes, simple XML character
   * data, and JSON/YAML property values.
   *
   * @param value
   *          the string value to parse
   * @return the parsed data as the adapter's type
   * @throws IllegalArgumentException
   *           if the data is not valid to the data type
   */
  @NonNull
  TYPE parse(@NonNull String value);

  /**
   * This method is expected to parse content starting at the next event. Parsing
   * will continue until the next event represents content that is not handled by
   * this adapter. This means the event stream should be positioned after any
   * {@link XMLEvent#END_ELEMENT} that corresponds to an
   * {@link XMLEvent#START_ELEMENT} parsed by this adapter.
   * <p>
   * If this method parses the {@link XMLEvent#START_ELEMENT} for the element that
   * contains the value data, then this method must also parse the corresponding
   * {@link XMLEvent#END_ELEMENT}. Otherwise, the first event to parse will be the
   * value data.
   * <p>
   * The value data is expected to be parsed completely, leaving the event stream
   * on a peeked event corresponding to content that is not handled by this
   * method.
   *
   * @param eventReader
   *          the XML parser used to read the parsed value
   * @return the parsed value
   * @throws IOException
   *           if a parsing error occurs
   */
  // TODO: migrate code to XML parser implementation.
  @NonNull
  TYPE parse(@NonNull XMLEventReader2 eventReader) throws IOException;

  /**
   * Parses a JSON property value.
   *
   * @param parser
   *          the JSON parser used to read the parsed value
   * @return the parsed value
   * @throws IOException
   *           if a parsing error occurs
   */
  // TODO: migrate code to JSON parser implementation.
  @NonNull
  TYPE parse(@NonNull JsonParser parser) throws IOException;

  /**
   * Parses a provided string using {@link #parse(String)}.
   * <p>
   * This method may pre-parse the data and then return copies, since the data can
   * only be parsed once, but the supplier might be called multiple times.
   *
   * @param value
   *          the string value to parse
   * @return a supplier that will provide new instances of the parsed data
   * @throws IOException
   *           if an error occurs while parsing
   * @throws IllegalArgumentException
   *           if the provided value is invalid based on the data type
   * @see #parse(String)
   */
  @NonNull
  default Supplier<TYPE> parseAndSupply(@NonNull String value) throws IOException {
    TYPE retval = parse(value);
    return () -> copy(retval);
  }

  /**
   * Parses a provided string using
   * {@link IDataTypeAdapter#parse(XMLEventReader2)}.
   * <p>
   * This method may pre-parse the data and then return copies, since the data can
   * only be parsed once, but the supplier might be called multiple times.
   *
   * @param eventReader
   *          the XML parser used to read the parsed value
   * @return a supplier that will provide new instances of the parsed data
   * @throws IOException
   *           if an error occurs while parsing
   * @see #parse(String)
   * @see #parse(XMLEventReader2)
   */
  // TODO: migrate code to XML parser implementation.
  @NonNull
  default Supplier<TYPE> parseAndSupply(@NonNull XMLEventReader2 eventReader) throws IOException {
    TYPE retval = parse(eventReader);
    return () -> copy(retval);
  }

  /**
   * Parses a provided string using {@link #parse(JsonParser)}.
   * <p>
   * This method may pre-parse the data and then return copies, since the data can
   * only be parsed once, but the supplier might be called multiple times.
   *
   * @param parser
   *          the JSON parser used to read the parsed value
   * @return a supplier that will provide new instances of the parsed data
   * @throws IOException
   *           if an error occurs while parsing
   * @see #parse(String)
   * @see #parse(JsonParser)
   */
  // TODO: migrate code to JSON parser implementation.
  @NonNull
  default Supplier<TYPE> parseAndSupply(@NonNull JsonParser parser) throws IOException {
    TYPE retval = parse(parser);
    return () -> copy(retval);
  }

  /**
   * Writes the provided Java class instance data as XML. The parent element
   * information is provided as a {@link StartElement} event, which allows
   * namespace information to be obtained from the parent element using the
   * {@link StartElement#getName()} and {@link StartElement#getNamespaceContext()}
   * methods, which can be used when writing the provided instance value.
   *
   * @param instance
   *          the {@link Field} instance value to write
   * @param parent
   *          the {@link StartElement} XML event that is the parent of the data to
   *          write
   * @param eventFactory
   *          the XML event factory used to generate XML writing events
   * @param eventWriter
   *          the XML writer used to output XML as events
   * @throws IOException
   *           if an unexpected error occurred while writing to the output stream
   */
  // TODO: migrate code to XML writer implementation.
  void writeXmlValue(@NonNull Object instance, @NonNull StartElement parent, @NonNull XMLEventFactory2 eventFactory,
      @NonNull XMLEventWriter eventWriter)
      throws IOException;

  /**
   * Writes the provided Java class instance data as XML. The parent element
   * information is provided as an XML {@link QName}, which allows namespace
   * information to be obtained from the parent element. Additional namespace
   * information can be gathered using the
   * {@link XMLStreamWriter2#getNamespaceContext()} method, which can be used when
   * writing the provided instance value.
   *
   * @param instance
   *          the {@link Field} instance value to write
   * @param parentName
   *          the qualified name of the XML data's parent element
   * @param writer
   *          the XML writer used to output the XML data
   * @throws IOException
   *           if an unexpected error occurred while processing the XML output
   */
  // TODO: migrate code to XML writer implementation.
  void writeXmlValue(@NonNull Object instance, @NonNull QName parentName, @NonNull XMLStreamWriter2 writer)
      throws IOException;

  /**
   * Writes the provided Java class instance as a JSON/YAML field value.
   *
   * @param instance
   *          the {@link Field} instance value to write
   * @param writer
   *          the JSON/YAML writer used to output the JSON/YAML data
   * @throws IOException
   *           if an unexpected error occurred while writing the JSON/YAML output
   */
  // TODO: migrate code to JSON writer implementation.
  void writeJsonValue(@NonNull Object instance, @NonNull JsonGenerator writer) throws IOException;

  /**
   * Gets the default value to use as the JSON/YAML field name for a Metaschema
   * field value if no JSON value key flag or name is configured.
   *
   * @return the default field name to use
   */
  // TODO: migrate code to JSON implementations.
  @NonNull
  String getDefaultJsonValueKey();

  /**
   * Determines if the data type's value is allowed to be unwrapped in XML when
   * the value is a field value.
   *
   * @return {@code true} if allowed, or {@code false} otherwise.
   */
  // TODO: migrate code to XML implementations.
  boolean isUnrappedValueAllowedInXml();

  /**
   * Determines if the datatype uses mixed text and element content in XML.
   *
   * @return {@code true} if the datatype uses mixed text and element content in
   *         XML, or {@code false} otherwise
   */
  // TODO: migrate code to XML implementations.
  boolean isXmlMixed();
}
