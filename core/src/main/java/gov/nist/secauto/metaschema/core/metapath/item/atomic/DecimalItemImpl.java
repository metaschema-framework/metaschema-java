/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.DecimalAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

import edu.umd.cs.findbugs.annotations.NonNull;

class DecimalItemImpl
    extends AbstractDecimalItem<BigDecimal> {
  public DecimalItemImpl(@NonNull BigDecimal value) {
    super(value);
  }

  @Override
  public DecimalAdapter getJavaTypeAdapter() {
    return MetaschemaDataTypeProvider.DECIMAL;
  }

  @Override
  public BigDecimal asDecimal() {
    return getValue();
  }

  @SuppressWarnings("null")
  @Override
  public String asString() {
    BigDecimal decimal = getValue();
    // if the fractional part is empty, render as an integer
    return decimal.scale() <= 0 ? decimal.toBigIntegerExact().toString() : decimal.toPlainString();
  }

  @SuppressWarnings("null")
  @Override
  public BigInteger asInteger() {
    return getValue().toBigInteger();
  }

  @Override
  public int hashCode() {
    return Objects.hash(asDecimal());
  }

  @SuppressWarnings("PMD.OnlyOneReturn")
  @Override
  public boolean equals(Object obj) {
    return this == obj
        || (obj instanceof IDecimalItem && compareTo((IDecimalItem) obj) == 0);
  }
}
