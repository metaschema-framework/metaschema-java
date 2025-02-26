/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.function.DateTimeFunctionException;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.impl.DayTimeDurationItemImpl;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.time.Duration;
import java.time.ZoneOffset;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An atomic Metapath item containing a duration data value in days, hours, and
 * seconds.
 */
public interface IDayTimeDurationItem extends IDurationItem {
  /**
   * Get the type information for this item.
   *
   * @return the type information
   */
  @NonNull
  static IAtomicOrUnionType<IDayTimeDurationItem> type() {
    return MetaschemaDataTypeProvider.DAY_TIME_DURATION.getItemType();
  }

  @Override
  default IAtomicOrUnionType<IDayTimeDurationItem> getType() {
    return type();
  }

  /**
   * Construct a new day time duration item using the provided string
   * {@code value}.
   *
   * @param value
   *          a string representing a day time duration
   * @return the new item
   * @throws InvalidTypeMetapathException
   *           if the provided string value is not a day/time duration value
   *           according to ISO 8601
   */
  @NonNull
  static IDayTimeDurationItem valueOf(@NonNull String value) {
    try {
      return valueOf(MetaschemaDataTypeProvider.DAY_TIME_DURATION.parse(value));
    } catch (IllegalArgumentException ex) {
      throw new InvalidTypeMetapathException(
          null,
          String.format("Invalid day/time value '%s',",
              value),
          ex);
    }
  }

  /**
   * Construct a new day time duration item using the provided {@code value}.
   *
   * @param value
   *          a duration
   * @return the new item
   */
  @NonNull
  static IDayTimeDurationItem valueOf(@NonNull Duration value) {
    return new DayTimeDurationItemImpl(value);
  }

  /**
   * Get the items wrapped value as a duration.
   *
   * @return the wrapped value as a duration
   */
  @NonNull
  Duration asDuration();

  /**
   * Get the "wrapped" duration value in seconds.
   *
   * @return the underlying duration in seconds
   */
  default long asSeconds() {
    return asDuration().toSeconds();
  }

  /**
   * Returns a copy of this duration with the amount negated.
   *
   * @return this duration with the amount negated
   */
  @NonNull
  default IDayTimeDurationItem negate() {
    return valueOf(ObjectUtils.notNull(asDuration().negated()));
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
  static IDayTimeDurationItem cast(@NonNull IAnyAtomicItem item) {
    try {
      return item instanceof IDayTimeDurationItem
          ? (IDayTimeDurationItem) item
          : valueOf(item.asString());
    } catch (IllegalStateException | InvalidTypeMetapathException ex) {
      // asString can throw IllegalStateException exception
      throw new InvalidValueForCastFunctionException(ex);
    }
  }

  @Override
  default IDayTimeDurationItem castAsType(IAnyAtomicItem item) {
    return cast(item);
  }

  /**
   * Compares this value with the argument.
   *
   * @param item
   *          the item to compare with this value
   * @return a negative integer, zero, or a positive integer if this value is less
   *         than, equal to, or greater than the {@code item}.
   */
  default int compareTo(@NonNull IDayTimeDurationItem item) {
    return asDuration().compareTo(item.asDuration());

  }

  /**
   * Get a zone offset for this duration.
   *
   * @return the offset
   * @throws DateTimeFunctionException
   *           with code
   *           {@link DateTimeFunctionException#INVALID_TIME_ZONE_VALUE_ERROR} if
   *           the offset is < -PT14H or > PT14H
   */
  @NonNull
  ZoneOffset asZoneOffset();
}
