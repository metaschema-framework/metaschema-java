/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.item.atomic.impl;

import java.math.BigInteger;

import dev.metaschema.core.datatype.adapter.IntegerAdapter;
import dev.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of a Metapath atomic item containing an integer data value.
 */
public class IntegerItemImpl
    extends AbstractIntegerItem {

  /**
   * Construct a new item with the provided {@code value}.
   *
   * @param value
   *          the value to wrap
   */
  public IntegerItemImpl(@NonNull BigInteger value) {
    super(value);
  }

  @Override
  public IntegerAdapter getJavaTypeAdapter() {
    return MetaschemaDataTypeProvider.INTEGER;
  }
}
