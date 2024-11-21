/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.datatype.object.AmbiguousDateTime;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An atomic Metapath item representing a date/time value in the Metapath
 * system.
 * <p>
 * This interface provides functionality for handling date/time values with and
 * without time zone information, supporting parsing, casting, and comparison
 * operations. It works in conjunction with {@link AmbiguousDateTime} to
 * properly handle time zone ambiguity.
 */
public interface IDateTimeItem extends ITemporalItem {
  @NonNull
  static IAtomicOrUnionType type() {
    return MetaschemaDataTypeProvider.DATE_TIME_TYPE;
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
  static IDateTimeItem cast(@NonNull IAnyAtomicItem item) {
    IDateTimeItem retval;
    if (item instanceof IDateTimeItem) {
      retval = (IDateTimeItem) item;
    } else {
      try {
        retval = IDateTimeWithoutTimeZoneItem.valueOf(item.asString());
      } catch (IllegalStateException | InvalidTypeMetapathException ex) {
        // asString can throw IllegalStateException exception
        throw new InvalidValueForCastFunctionException(
            String.format("The value '%s' is not compatible with the type '%s'",
                item.getValue(),
                item.getClass().getName()),
            ex);
      }
    }
    return retval;
  }

  @Override
  default IDateTimeItem castAsType(IAnyAtomicItem item) {
    return cast(item);
  }

  @Override
  default int compareTo(IAnyAtomicItem item) {
    return compareTo(cast(item));
  }
}
