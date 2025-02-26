/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic.impl;

import gov.nist.secauto.metaschema.core.metapath.item.atomic.AbstractAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDayTimeDurationItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDurationItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IYearMonthDurationItem;

import java.time.temporal.TemporalAmount;
import java.util.Comparator;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides a common implementation for all atomic types that have an underlying
 * value.
 *
 * @param <TYPE>
 *          the Java type associated with the atomic type.
 */
public abstract class AbstractDurationItem<TYPE extends TemporalAmount>
    extends AbstractAnyAtomicItem<TYPE>
    implements IDurationItem {

  private static final Comparator<IDurationItem> COMPARATOR
      = Comparator.<IDurationItem>comparingLong(item -> item instanceof IYearMonthDurationItem
          ? ((IYearMonthDurationItem) item).asPeriod().toTotalMonths()
          : 0)
          .thenComparingLong(item -> item instanceof IDayTimeDurationItem
              ? ((IDayTimeDurationItem) item).asDuration().toSeconds()
              : 0)
          .thenComparingInt(item -> item instanceof IDayTimeDurationItem
              ? ((IDayTimeDurationItem) item).asDuration().getNano()
              : 0);

  /**
   * Construct a new duration item using the provided value.
   *
   * @param value
   *          the wrapped duration value
   */
  protected AbstractDurationItem(@NonNull TYPE value) {
    super(value);
  }

  @Override
  protected String getValueSignature() {
    return "'" + asString() + "'";
  }

  @Override
  public int compareTo(@NonNull IDurationItem item) {
    return COMPARATOR.compare(this, item);
  }
}
