/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;

import java.time.Duration;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IDayTimeDurationItem extends IDurationItem {
  /**
   * Construct a new day time duration item using the provided string
   * {@code value}.
   *
   * @param value
   *          a string representing a day time duration
   * @return the new item
   */
  @NonNull
  static IDayTimeDurationItem valueOf(@NonNull String value) {
    try {
      return valueOf(MetaschemaDataTypeProvider.DAY_TIME_DURATION.parse(value));
    } catch (IllegalArgumentException ex) {
      throw new InvalidValueForCastFunctionException(String.format("Unable to parse string value '%s'", value),
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
    return MetaschemaDataTypeProvider.DAY_TIME_DURATION.cast(item);
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

  @Override
  default int compareTo(IAnyAtomicItem item) {
    return compareTo(cast(item));
  }
}
