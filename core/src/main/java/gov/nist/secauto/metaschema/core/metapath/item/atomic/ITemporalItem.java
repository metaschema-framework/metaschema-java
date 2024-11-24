/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.impl.TypeConstants;

import java.time.ZonedDateTime;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An atomic Metapath item containing a temporal data value.
 */
public interface ITemporalItem extends IAnyAtomicItem {
  /**
   * Get the type information for this item.
   *
   * @return the type information
   */
  @NonNull
  static IAtomicOrUnionType<ITemporalItem> type() {
    return TypeConstants.TEMPORAL_TYPE;
  }

  /**
   * Cast the provided type to this item type.
   *
   * @param item
   *          the item to cast
   * @return the original item if it is already this type, otherwise a new item
   *         cast to this type
   * @throws InvalidValueForCastFunctionException
   *           if the provided {@code item} cannot be cast to this type
   */
  @NonNull
  static ITemporalItem cast(@NonNull IAnyAtomicItem item) {
    ITemporalItem retval;
    if (item instanceof ITemporalItem) {
      retval = (ITemporalItem) item;
    } else {
      String value;
      try {
        value = item.asString();
      } catch (IllegalStateException ex) {
        // asString can throw IllegalStateException exception
        throw new InvalidValueForCastFunctionException(ex);
      }

      try {
        retval = IDateTimeItem.valueOf(value);
      } catch (IllegalStateException ex) {
        try {
          retval = IDateItem.valueOf(value);
        } catch (IllegalStateException ex2) {
          InvalidValueForCastFunctionException newEx = new InvalidValueForCastFunctionException(
              String.format("Value '%s' cannot be parsed as either a date or date/time value", value),
              ex2);
          newEx.addSuppressed(ex);
          throw newEx; // NOPMD context as suppressed
        }
      }
    }
    return retval;
  }

  /**
   * Determine if the temporal item has a timezone.
   *
   * @return {@code true} if the temporal item has a timezone or {@code false}
   *         otherwise
   */
  boolean hasTimezone();

  /**
   * Get the "wrapped" date/time value.
   *
   * @return the underlying date value
   */
  @NonNull
  ZonedDateTime asZonedDateTime();

  /**
   * Compares this value with the argument.
   *
   * @param item
   *          the item to compare with this value
   * @return a negative integer, zero, or a positive integer if this value is less
   *         than, equal to, or greater than the {@code item}.
   */
  default int compareTo(@NonNull ITemporalItem item) {
    return asZonedDateTime().compareTo(item.asZonedDateTime());
  }
}
