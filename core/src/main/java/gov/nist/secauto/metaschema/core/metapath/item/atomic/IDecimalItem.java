/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.impl.DecimalItemImpl;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An atomic Metapath item containing a decimal data value.
 */
public interface IDecimalItem extends INumericItem {
  /**
   * The decimal item with the value "0".
   */
  @NonNull
  IDecimalItem ZERO = valueOf(ObjectUtils.notNull(BigDecimal.ZERO));

  /**
   * Get the type information for this item.
   *
   * @return the type information
   */
  @NonNull
  static IAtomicOrUnionType<IDecimalItem> type() {
    return MetaschemaDataTypeProvider.DECIMAL.getItemType();
  }

  @Override
  default IAtomicOrUnionType<? extends IDecimalItem> getType() {
    return type();
  }

  /**
   * Construct a new decimal item using the provided string {@code value}.
   *
   * @param value
   *          a string representing a decimal value
   * @return the new item
   * @throws InvalidTypeMetapathException
   *           if the given string is not a decimal value
   */
  @NonNull
  static IDecimalItem valueOf(@NonNull String value) {
    try {
      return valueOf(MetaschemaDataTypeProvider.DECIMAL.parse(value));
    } catch (IllegalArgumentException ex) {
      throw new InvalidTypeMetapathException(
          null,
          String.format("Invalid decimal value '%s'. %s",
              value,
              ex.getLocalizedMessage()),
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
   *          a double value
   * @return the new item
   */
  @NonNull
  static IDecimalItem valueOf(boolean value) {
    return valueOf(DecimalItemImpl.toBigDecimal(value));
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
  static IDecimalItem cast(@NonNull IAnyAtomicItem item) {
    IDecimalItem retval;
    if (item instanceof IDecimalItem) {
      retval = (IDecimalItem) item;
    } else if (item instanceof INumericItem) {
      retval = valueOf(((INumericItem) item).asDecimal());
    } else if (item instanceof IBooleanItem) {
      retval = valueOf(((IBooleanItem) item).toBoolean());
    } else {
      try {
        retval = valueOf(item.asString());
      } catch (IllegalStateException | InvalidTypeMetapathException ex) {
        // asString can throw IllegalStateException exception
        throw new InvalidValueForCastFunctionException(ex);
      }
    }
    return retval;
  }

  @Override
  default IDecimalItem castAsType(IAnyAtomicItem item) {
    return cast(item);
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
