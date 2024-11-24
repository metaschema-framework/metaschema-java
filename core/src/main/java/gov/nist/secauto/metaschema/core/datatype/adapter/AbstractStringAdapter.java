/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;

import gov.nist.secauto.metaschema.core.datatype.AbstractDataTypeAdapter;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides a common base class for string-based data types.
 *
 * @param <ITEM_TYPE>
 *          the Metapath item type supported by the adapter
 */
public abstract class AbstractStringAdapter<ITEM_TYPE extends IStringItem>
    extends AbstractDataTypeAdapter<String, ITEM_TYPE> {

  /**
   * Construct a new string-based adapter.
   *
   * @param itemClass
   *          the Java type of the Matepath item this adapter supports
   * @param castExecutor
   *          the method to call to cast an item to an item based on this type
   */
  protected AbstractStringAdapter(
      @NonNull Class<ITEM_TYPE> itemClass,
      @NonNull IAtomicOrUnionType.ICastExecutor<ITEM_TYPE> castExecutor) {
    super(String.class, itemClass, castExecutor);
  }

  @Override
  public JsonFormatTypes getJsonRawType() {
    return JsonFormatTypes.STRING;
  }

  @Override
  public String parse(String value) {
    return value;
  }

  @Override
  public String copy(Object obj) {
    // a Java string is immutable
    return (String) obj;
  }
}
