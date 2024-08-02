/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public interface IDecimalItem extends INumericItem {
  @SuppressWarnings("null")
  @NonNull
  IDecimalItem ZERO = valueOf(BigDecimal.ZERO);

  /**
   * Construct a new decimal item using the provided string {@code value}.
   *
   * @param value
   *          a string representing a decimal value
   * @return the new item
   */
  @NonNull
  static IDecimalItem valueOf(@NonNull String value) {
    try {
      return valueOf(MetaschemaDataTypeProvider.DECIMAL.parse(value));
    } catch (IllegalArgumentException ex) {
      throw new InvalidValueForCastFunctionException(String.format("Unable to parse string value '%s'", value),
          ex);
    }
  }

  /**
   * Construct a new decimal item using the provided {@code value}.
   *
   * @param value
   *          a long value
   * @return the new item
   */
  @NonNull
  static IDecimalItem valueOf(long value) {
    return valueOf(ObjectUtils.notNull(BigDecimal.valueOf(value)));
  }

  /**
   * Construct a new decimal item using the provided {@code value}.
   *
   * @param value
   *          a double value
   * @return the new item
   */
  @NonNull
  static IDecimalItem valueOf(double value) {
    return valueOf(ObjectUtils.notNull(Double.toString(value)));
  }

  /**
   * Construct a new decimal item using the provided {@code value}.
   *
   * @param value
   *          a decimal value
   * @return the new item
   */
  @NonNull
  static IDecimalItem valueOf(@NonNull BigDecimal value) {
    return new DecimalItemImpl(value);
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
  static IDecimalItem cast(@Nullable IAnyAtomicItem item) {
    return MetaschemaDataTypeProvider.DECIMAL.cast(item);
  }

  @Override
  default IDecimalItem castAsType(IAnyAtomicItem item) {
    return valueOf(cast(item).asDecimal());
  }

  @Override
  default boolean toEffectiveBoolean() {
    return !BigDecimal.ZERO.equals(asDecimal());
  }

  @SuppressWarnings("null")
  @Override
  default INumericItem abs() {
    return new DecimalItemImpl(asDecimal().abs());
  }

  @SuppressWarnings("null")
  @Override
  default IIntegerItem ceiling() {
    return IIntegerItem.valueOf(asDecimal().setScale(0, RoundingMode.CEILING).toBigIntegerExact());
  }

  @SuppressWarnings("null")
  @Override
  default IIntegerItem floor() {
    return IIntegerItem.valueOf(asDecimal().setScale(0, RoundingMode.FLOOR).toBigIntegerExact());
  }

  /**
   * Compares this value with the argument.
   *
   * @param item
   *          the item to compare with this value
   * @return a negative integer, zero, or a positive integer if this value is less
   *         than, equal to, or greater than the {@code item}.
   */
  default int compareTo(@NonNull IDecimalItem item) {
    return asDecimal().compareTo(item.asDecimal());
  }

  @Override
  default int compareTo(IAnyAtomicItem item) {
    return compareTo(cast(item));
  }
}
