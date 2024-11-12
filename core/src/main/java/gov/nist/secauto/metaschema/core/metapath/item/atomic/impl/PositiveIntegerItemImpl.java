/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic.impl;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.datatype.adapter.PositiveIntegerAdapter;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IPositiveIntegerItem;

import java.math.BigInteger;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of a Metapath atomic item containing a positive integer
 * data value.
 */
public class PositiveIntegerItemImpl
    extends AbstractIntegerItem
    implements IPositiveIntegerItem {

  /**
   * Construct a new item with the provided {@code value}.
   *
   * @param value
   *          the value to wrap
   */
  public PositiveIntegerItemImpl(@NonNull BigInteger value) {
    super(value);
  }

  @Override
  public PositiveIntegerAdapter getJavaTypeAdapter() {
    return MetaschemaDataTypeProvider.POSITIVE_INTEGER;
  }

}
