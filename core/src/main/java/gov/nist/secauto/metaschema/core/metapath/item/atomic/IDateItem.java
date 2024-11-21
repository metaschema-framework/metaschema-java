/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;

import java.time.ZonedDateTime;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An atomic Metapath item containing a date data value.
 */
public interface IDateItem extends ITemporalItem {
  @NonNull
  static IAtomicOrUnionType type() {
    return MetaschemaDataTypeProvider.DATE_TYPE;
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
  static IDateItem cast(@NonNull IAnyAtomicItem item) {
    IDateItem retval;
    if (item instanceof IDateItem) {
      retval = (IDateItem) item;
    } else if (item instanceof IDateTimeItem) {
      ZonedDateTime value = ((IDateTimeItem) item).asZonedDateTime();
      retval = IDateWithTimeZoneItem.valueOf(value);
    } else if (item instanceof IStringItem || item instanceof IUntypedAtomicItem) {
      try {
        retval = IDateWithoutTimeZoneItem.valueOf(item.asString());
      } catch (IllegalStateException | InvalidTypeMetapathException ex) {
        // asString can throw IllegalStateException exception
        throw new InvalidValueForCastFunctionException(ex);
      }
    } else {
      throw new InvalidValueForCastFunctionException(
          String.format("unsupported item type '%s'", item.getClass().getName()));
    }
    return retval;
  }

  @Override
  IDateItem castAsType(IAnyAtomicItem item);

  @Override
  default int compareTo(IAnyAtomicItem item) {
    return compareTo(cast(item));
  }
}
