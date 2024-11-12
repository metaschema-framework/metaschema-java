/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;

import java.time.temporal.TemporalAmount;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A Metapath atomic item representing a duration data value.
 */
public interface IDurationItem extends IAnyAtomicItem {
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
  static IDurationItem cast(@NonNull IAnyAtomicItem item) {
    IDurationItem retval;
    if (item instanceof IDurationItem) {
      retval = (IDurationItem) item;
    } else {
      String value;
      try {
        value = item.asString();
      } catch (IllegalStateException ex) {
        // asString can throw IllegalStateException exception
        throw new InvalidValueForCastFunctionException(ex);
      }

      try {
        retval = IDayTimeDurationItem.valueOf(value);
      } catch (IllegalStateException ex) {
        try {
          retval = IYearMonthDurationItem.valueOf(value);
        } catch (IllegalStateException ex2) {
          InvalidValueForCastFunctionException newEx = new InvalidValueForCastFunctionException(ex2);
          newEx.addSuppressed(ex);
          throw newEx; // NOPMD context as suppressed
        }
      }
    }
    return retval;
  }

  @Override
  TemporalAmount getValue();

  @Override
  default IDurationItem castAsType(IAnyAtomicItem item) {
    return cast(item);
  }
}
