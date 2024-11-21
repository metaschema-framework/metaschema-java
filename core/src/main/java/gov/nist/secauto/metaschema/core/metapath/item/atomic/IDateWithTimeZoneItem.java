/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.impl.DateWithTimeZoneItemImpl;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An atomic Metapath item containing a date data value that has an explicit
 * timezone.
 */
public interface IDateWithTimeZoneItem extends IDateItem {

  @NonNull
  static IAtomicOrUnionType type() {
    return MetaschemaDataTypeProvider.DATE_WITH_TZ.getItemType();
  }

  /**
   * Construct a new date item using the provided string {@code value}.
   *
   * @param value
   *          a string representing a date
   * @return the new item
   */
  @NonNull
  static IDateWithTimeZoneItem valueOf(@NonNull String value) {
    try {
      return valueOf(MetaschemaDataTypeProvider.DATE_WITH_TZ.parse(value));
    } catch (IllegalArgumentException ex) {
      throw new InvalidTypeMetapathException(
          null,
          String.format("Invalid date value '%s'. %s",
              value,
              ex.getLocalizedMessage()),
          ex);
    }
  }

  /**
   * Construct a new date item using the provided {@code value}.
   *
   * @param value
   *          a date, with time zone information
   * @return the new item
   */
  @NonNull
  static IDateWithTimeZoneItem valueOf(@NonNull ZonedDateTime value) {
    return new DateWithTimeZoneItemImpl(
        // ignore time
        ObjectUtils.notNull(value.truncatedTo(ChronoUnit.DAYS)));
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
  static IDateWithTimeZoneItem cast(@NonNull IAnyAtomicItem item) {
    try {
      return item instanceof IDateWithTimeZoneItem
          ? (IDateWithTimeZoneItem) item
          : valueOf(item.asString());
    } catch (IllegalStateException | InvalidTypeMetapathException ex) {
      // asString can throw IllegalStateException exception
      throw new InvalidValueForCastFunctionException(ex);
    }
  }

  @Override
  default IDateWithTimeZoneItem castAsType(IAnyAtomicItem item) {
    return cast(item);
  }
}
