/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;

import gov.nist.secauto.metaschema.core.datatype.AbstractDataTypeAdapter;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IIntegerItem;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;

import java.io.IOException;
import java.math.BigInteger;

import edu.umd.cs.findbugs.annotations.NonNull;

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
   *
   * @param itemClass
   *          the Java type of the Metapath item this adapter supports
   * @param castExecutor
   *          the method to call to cast an item to an item based on this type
   */
  protected AbstractIntegerAdapter(
      @NonNull Class<ITEM_TYPE> itemClass,
      @NonNull IAtomicOrUnionType.ICastExecutor<ITEM_TYPE> castExecutor) {
    super(BigInteger.class, itemClass, castExecutor);
  }

  @Override
  public JsonFormatTypes getJsonRawType() {
    return JsonFormatTypes.INTEGER;
  }

  @Override
  public BigInteger parse(String value) {
    try {
      return new BigInteger(value);
    } catch (NumberFormatException ex) {
      throw new IllegalArgumentException(ex);
    }
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
