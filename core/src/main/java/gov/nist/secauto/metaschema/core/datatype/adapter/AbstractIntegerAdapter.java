/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;

import gov.nist.secauto.metaschema.core.datatype.AbstractDataTypeAdapter;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IIntegerItem;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Provides a common base class for integer-based data types.
 * <p>
 * An underlying {@link BigInteger} is used to support arbitrary sized integers.
 *
 * @param <ITEM_TYPE>
 *          the Metapath item type supported by the adapter
 */
public abstract class AbstractIntegerAdapter<ITEM_TYPE extends IIntegerItem>
    extends AbstractDataTypeAdapter<BigInteger, ITEM_TYPE> {

  /**
   * Construct a new integer-based adapter.
   */
  protected AbstractIntegerAdapter() {
    super(BigInteger.class);
  }

  @Override
  public JsonFormatTypes getJsonRawType() {
    return JsonFormatTypes.INTEGER;
  }

  @Override
  public BigInteger parse(String value) {
    return new BigInteger(value);
  }

  @Override
  public void writeJsonValue(Object value, JsonGenerator generator) throws IOException {
    try {
      generator.writeNumber((BigInteger) value);
    } catch (ClassCastException ex) {
      throw new IOException(ex);
    }
  }

  @Override
  public BigInteger copy(Object obj) {
    // a BigInteger is immutable
    return (BigInteger) obj;
  }
}
