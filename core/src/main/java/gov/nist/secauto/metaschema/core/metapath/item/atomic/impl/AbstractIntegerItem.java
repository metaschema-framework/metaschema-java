/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic.impl;

import gov.nist.secauto.metaschema.core.metapath.item.atomic.IIntegerItem;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An abstract implementation of a Metapath atomic item containing an integer
 * data value.
 */
public abstract class AbstractIntegerItem
    extends AbstractDecimalItem<BigInteger>
    implements IIntegerItem {
  /**
   * Construct a new item with the provided {@code value}.
   *
   * @param value
   *          the value to wrap
   */
  protected AbstractIntegerItem(@NonNull BigInteger value) {
    super(value);
  }

  @Override
  public boolean toEffectiveBoolean() {
    return !BigInteger.ZERO.equals(asInteger());
  }

  @Override
  public BigDecimal asDecimal() {
    return new BigDecimal(getValue(), MathContext.DECIMAL64);
  }

  @Override
  public BigInteger asInteger() {
    return getValue();
  }

  @SuppressWarnings("null")
  @Override
  public IIntegerItem abs() {
    BigInteger value = asInteger();
    return value.signum() > -1 ? this : IIntegerItem.valueOf(value.abs());
  }

  @Override
  public int hashCode() {
    return asInteger().hashCode();
  }

  @SuppressWarnings("PMD.OnlyOneReturn")
  @Override
  public boolean equals(Object obj) {
    return this == obj
        || obj instanceof IIntegerItem && compareTo((IIntegerItem) obj) == 0;
  }
}
