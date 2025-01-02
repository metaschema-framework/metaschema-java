/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.function.ArithmeticFunctionException;
import gov.nist.secauto.metaschema.core.metapath.function.CastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.function.FunctionUtils;
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
   * Convert this decimal item to a Java int, exactly. If the decimal is not in a
   * valid int range, an exception is thrown.
   *
   * @return the int value
   * @throws CastFunctionException
   *           if the value does not fit in an int
   */
  @Override
  default int toIntValueExact() {
    try {
      // asDecimal() yields a BigDecimal, so we can call intValueExact().
      // Throw an exception if it does not fit in a 32-bit int.
      return asDecimal().intValueExact();
    } catch (ArithmeticException ex) {
      throw new CastFunctionException(
          CastFunctionException.INPUT_VALUE_TOO_LARGE,
          this,
          String.format("Decimal value '%s' is out of range for a Java int.", asString()),
          ex);
    }
  }

  /**
   * Create a new sum by adding this value to the provided addend value.
   *
   * @param addend
   *          the second value to sum
   * @return a new value resulting from adding this value to the provided addend
   *         value
   */
  @NonNull
  default IDecimalItem add(@NonNull IDecimalItem addend) {
    BigDecimal addendLeft = asDecimal();
    BigDecimal addendRight = addend.asDecimal();
    return valueOf(ObjectUtils.notNull(addendLeft.add(addendRight)));
  }

  /**
   * Determine the difference by subtracting the provided subtrahend value from
   * this minuend value.
   *
   * @param subtrahend
   *          the value to subtract
   * @return a new value resulting from subtracting the subtrahend from the
   *         minuend
   */
  @NonNull
  default IDecimalItem subtract(@NonNull IDecimalItem subtrahend) {
    BigDecimal minuendDecimal = asDecimal();
    BigDecimal subtrahendDecimal = subtrahend.asDecimal();
    return valueOf(ObjectUtils.notNull(minuendDecimal.subtract(subtrahendDecimal, FunctionUtils.MATH_CONTEXT)));
  }

  /**
   * Multiply this multiplicand value by the provided multiplier value.
   *
   * @param multiplier
   *          the value to multiply by
   * @return a new value resulting from multiplying the multiplicand by the
   *         multiplier
   */
  @NonNull
  default IDecimalItem multiply(@NonNull IDecimalItem multiplier) {
    return valueOf(ObjectUtils.notNull(asDecimal().multiply(multiplier.asDecimal(), FunctionUtils.MATH_CONTEXT)));
  }

  /**
   * Divide this dividend value by the provided divisor value.
   *
   * @param divisor
   *          the value to divide by
   * @return a new value resulting from dividing the dividend by the divisor
   * @throws ArithmeticFunctionException
   *           with the code {@link ArithmeticFunctionException#DIVISION_BY_ZERO}
   *           if the divisor is zero
   */
  @NonNull
  default IDecimalItem divide(@NonNull IDecimalItem divisor) {
    // create a decimal result
    BigDecimal divisorDecimal = divisor.asDecimal();

    if (BigDecimal.ZERO.compareTo(divisorDecimal) == 0) {
      throw new ArithmeticFunctionException(ArithmeticFunctionException.DIVISION_BY_ZERO,
          ArithmeticFunctionException.DIVISION_BY_ZERO_MESSAGE);
    }
    return valueOf(ObjectUtils.notNull(asDecimal().divide(divisorDecimal, FunctionUtils.MATH_CONTEXT)));
  }

  /**
   * Divide this dividend value by the provided divisor value using integer
   * division.
   *
   * @param divisor
   *          the value to divide by
   * @return a new value resulting from dividing the dividend by the divisor
   */
  @Override
  @NonNull
  default IIntegerItem integerDivide(INumericItem divisor) {
    // create a decimal result
    BigDecimal decimalDivisor = divisor.asDecimal();

    if (BigDecimal.ZERO.compareTo(decimalDivisor) == 0) {
      throw new ArithmeticFunctionException(ArithmeticFunctionException.DIVISION_BY_ZERO,
          ArithmeticFunctionException.DIVISION_BY_ZERO_MESSAGE);
    }

    BigDecimal decimalDividend = asDecimal();
    return IIntegerItem.valueOf(
        ObjectUtils.notNull(decimalDividend
            .divideToIntegralValue(decimalDivisor, FunctionUtils.MATH_CONTEXT).toBigInteger()));
  }

  /**
   * Compute the remainder when dividing this dividend value by the provided
   * divisor value.
   *
   * @param divisor
   *          the value to divide by
   * @return a new value containing the remainder resulting from dividing the
   *         dividend by the divisor
   */
  @Override
  @NonNull
  default IDecimalItem mod(INumericItem divisor) {
    // create a decimal result
    BigDecimal decimalDivisor = divisor.asDecimal();

    if (BigDecimal.ZERO.compareTo(decimalDivisor) == 0) {
      throw new ArithmeticFunctionException(ArithmeticFunctionException.DIVISION_BY_ZERO,
          ArithmeticFunctionException.DIVISION_BY_ZERO_MESSAGE);
    }
    return valueOf(ObjectUtils.notNull(asDecimal().remainder(decimalDivisor, FunctionUtils.MATH_CONTEXT)));
  }

  @Override
  default IDecimalItem negate() {
    return valueOf(ObjectUtils.notNull(asDecimal().negate(FunctionUtils.MATH_CONTEXT)));
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
