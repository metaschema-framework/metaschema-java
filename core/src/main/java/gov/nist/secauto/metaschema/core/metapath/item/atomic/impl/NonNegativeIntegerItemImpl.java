/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic.impl;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.datatype.adapter.NonNegativeIntegerAdapter;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.INonNegativeIntegerItem;

import java.math.BigInteger;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of a Metapath atomic item containing a non-negative integer
 * data value.
 */
public class NonNegativeIntegerItemImpl
    extends AbstractIntegerItem
    implements INonNegativeIntegerItem {

  /**
   * Construct a new item with the provided {@code value}.
   *
   * @param value
   *          the value to wrap
   */
  public NonNegativeIntegerItemImpl(@NonNull BigInteger value) {
    super(value);
  }

  @Override
  public NonNegativeIntegerAdapter getJavaTypeAdapter() {
    return MetaschemaDataTypeProvider.NON_NEGATIVE_INTEGER;
  }

}
