/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;

import gov.nist.secauto.metaschema.core.datatype.AbstractDataTypeAdapter;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;

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
   */
  protected AbstractStringAdapter() {
    super(String.class);
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
