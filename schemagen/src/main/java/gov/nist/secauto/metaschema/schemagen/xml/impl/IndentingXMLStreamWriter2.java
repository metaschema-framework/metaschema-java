/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.impl;

import org.codehaus.stax2.XMLStreamLocation2;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.validation.ValidationProblemHandler;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidator;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.xml.stream.XMLStreamException;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An XMLStreamWriter2 wrapper that adds indentation to the output.
 * <p>
 * This wrapper extends {@link IndentingXMLStreamWriter} and implements
 * {@link XMLStreamWriter2} by delegating the Stax2-specific methods to the
 * underlying writer.
 */
public class IndentingXMLStreamWriter2
    extends IndentingXMLStreamWriter
    implements XMLStreamWriter2 {

  @NonNull
  private final XMLStreamWriter2 delegate2;

  /**
   * Constructs a new indenting XML stream writer for XMLStreamWriter2.
   *
   * @param delegate
   *          the underlying XMLStreamWriter2 to delegate to
   */
  public IndentingXMLStreamWriter2(@NonNull XMLStreamWriter2 delegate) {
    super(delegate);
    this.delegate2 = delegate;
  }

  /**
   * Gets the underlying XMLStreamWriter2.
   *
   * @return the delegate writer
   */
  protected XMLStreamWriter2 getDelegate2() {
    return delegate2;
  }

  // ============================================================
  // XMLStreamWriter2-specific methods - delegate to underlying writer
  // ============================================================

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isPropertySupported(String name) {
    return delegate2.isPropertySupported(name);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean setProperty(String name, Object value) {
    return delegate2.setProperty(name, value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public XMLStreamLocation2 getLocation() {
    return delegate2.getLocation();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getEncoding() {
    return delegate2.getEncoding();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeSpace(String text) throws XMLStreamException {
    delegate2.writeSpace(text);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeSpace(char[] text, int offset, int length) throws XMLStreamException {
    delegate2.writeSpace(text, offset, length);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeRaw(String text) throws XMLStreamException {
    delegate2.writeRaw(text);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeRaw(String text, int offset, int length) throws XMLStreamException {
    delegate2.writeRaw(text, offset, length);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeRaw(char[] text, int offset, int length) throws XMLStreamException {
    delegate2.writeRaw(text, offset, length);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void copyEventFromReader(XMLStreamReader2 reader, boolean preserveEventData) throws XMLStreamException {
    delegate2.copyEventFromReader(reader, preserveEventData);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void closeCompletely() throws XMLStreamException {
    delegate2.closeCompletely();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeDTD(String rootName, String systemId, String publicId, String internalSubset)
      throws XMLStreamException {
    delegate2.writeDTD(rootName, systemId, publicId, internalSubset);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeFullEndElement() throws XMLStreamException {
    delegate2.writeFullEndElement();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeStartDocument(String version, String encoding, boolean standAlone) throws XMLStreamException {
    delegate2.writeStartDocument(version, encoding, standAlone);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeCData(char[] text, int start, int len) throws XMLStreamException {
    delegate2.writeCData(text, start, len);
  }

  // ============================================================
  // TypedXMLStreamWriter methods - delegate to underlying writer
  // ============================================================

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeBoolean(boolean value) throws XMLStreamException {
    delegate2.writeBoolean(value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeInt(int value) throws XMLStreamException {
    delegate2.writeInt(value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeLong(long value) throws XMLStreamException {
    delegate2.writeLong(value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeFloat(float value) throws XMLStreamException {
    delegate2.writeFloat(value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeDouble(double value) throws XMLStreamException {
    delegate2.writeDouble(value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeInteger(BigInteger value) throws XMLStreamException {
    delegate2.writeInteger(value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeDecimal(BigDecimal value) throws XMLStreamException {
    delegate2.writeDecimal(value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeQName(javax.xml.namespace.QName name) throws XMLStreamException {
    delegate2.writeQName(name);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeBinary(byte[] value, int from, int length) throws XMLStreamException {
    delegate2.writeBinary(value, from, length);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeBinary(org.codehaus.stax2.typed.Base64Variant variant, byte[] value, int from, int length)
      throws XMLStreamException {
    delegate2.writeBinary(variant, value, from, length);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeIntArray(int[] value, int from, int length) throws XMLStreamException {
    delegate2.writeIntArray(value, from, length);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeLongArray(long[] value, int from, int length) throws XMLStreamException {
    delegate2.writeLongArray(value, from, length);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeFloatArray(float[] value, int from, int length) throws XMLStreamException {
    delegate2.writeFloatArray(value, from, length);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeDoubleArray(double[] value, int from, int length) throws XMLStreamException {
    delegate2.writeDoubleArray(value, from, length);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeBooleanAttribute(String prefix, String namespaceURI, String localName, boolean value)
      throws XMLStreamException {
    delegate2.writeBooleanAttribute(prefix, namespaceURI, localName, value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeIntAttribute(String prefix, String namespaceURI, String localName, int value)
      throws XMLStreamException {
    delegate2.writeIntAttribute(prefix, namespaceURI, localName, value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeLongAttribute(String prefix, String namespaceURI, String localName, long value)
      throws XMLStreamException {
    delegate2.writeLongAttribute(prefix, namespaceURI, localName, value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeFloatAttribute(String prefix, String namespaceURI, String localName, float value)
      throws XMLStreamException {
    delegate2.writeFloatAttribute(prefix, namespaceURI, localName, value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeDoubleAttribute(String prefix, String namespaceURI, String localName, double value)
      throws XMLStreamException {
    delegate2.writeDoubleAttribute(prefix, namespaceURI, localName, value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeIntegerAttribute(String prefix, String namespaceURI, String localName, BigInteger value)
      throws XMLStreamException {
    delegate2.writeIntegerAttribute(prefix, namespaceURI, localName, value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeDecimalAttribute(String prefix, String namespaceURI, String localName, BigDecimal value)
      throws XMLStreamException {
    delegate2.writeDecimalAttribute(prefix, namespaceURI, localName, value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeQNameAttribute(String prefix, String namespaceURI, String localName, javax.xml.namespace.QName name)
      throws XMLStreamException {
    delegate2.writeQNameAttribute(prefix, namespaceURI, localName, name);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeBinaryAttribute(String prefix, String namespaceURI, String localName, byte[] value)
      throws XMLStreamException {
    delegate2.writeBinaryAttribute(prefix, namespaceURI, localName, value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeBinaryAttribute(org.codehaus.stax2.typed.Base64Variant variant, String prefix, String namespaceURI,
      String localName, byte[] value) throws XMLStreamException {
    delegate2.writeBinaryAttribute(variant, prefix, namespaceURI, localName, value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeIntArrayAttribute(String prefix, String namespaceURI, String localName, int[] value)
      throws XMLStreamException {
    delegate2.writeIntArrayAttribute(prefix, namespaceURI, localName, value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeLongArrayAttribute(String prefix, String namespaceURI, String localName, long[] value)
      throws XMLStreamException {
    delegate2.writeLongArrayAttribute(prefix, namespaceURI, localName, value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeFloatArrayAttribute(String prefix, String namespaceURI, String localName, float[] value)
      throws XMLStreamException {
    delegate2.writeFloatArrayAttribute(prefix, namespaceURI, localName, value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeDoubleArrayAttribute(String prefix, String namespaceURI, String localName, double[] value)
      throws XMLStreamException {
    delegate2.writeDoubleArrayAttribute(prefix, namespaceURI, localName, value);
  }

  // ============================================================
  // Validatable methods - delegate to underlying writer
  // ============================================================

  /**
   * {@inheritDoc}
   */
  @Override
  public XMLValidator validateAgainst(XMLValidationSchema schema) throws XMLStreamException {
    return delegate2.validateAgainst(schema);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public XMLValidator stopValidatingAgainst(XMLValidationSchema schema) throws XMLStreamException {
    return delegate2.stopValidatingAgainst(schema);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public XMLValidator stopValidatingAgainst(XMLValidator validator) throws XMLStreamException {
    return delegate2.stopValidatingAgainst(validator);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ValidationProblemHandler setValidationProblemHandler(ValidationProblemHandler handler) {
    return delegate2.setValidationProblemHandler(handler);
  }
}
