/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.function.ArithmeticFunctionException;
import gov.nist.secauto.metaschema.core.metapath.function.FunctionUtils;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface INumericItem extends IAnyAtomicItem {

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
  static INumericItem cast(@NonNull IAnyAtomicItem item) {
    return MetaschemaDataTypeProvider.DECIMAL.cast(item);
  }

  /**
   * Get this item's value as a decimal.
   *
   * @return the equivalent decimal value
   */
  @NonNull
  BigDecimal asDecimal();

  /**
   * Get this item's value as an integer.
   *
   * @return the equivalent integer value
   */
  @NonNull
  BigInteger asInteger();

  /**
   * Get the effective boolean value of this item based on
   * <a href="https://www.w3.org/TR/xpath-31/#id-ebv">XPath 3.1</a>.
   *
   * @return the effective boolean value
   */
  boolean toEffectiveBoolean();

  @Override
  INumericItem castAsType(IAnyAtomicItem item);

  /**
   * Get the absolute value of the item.
   *
   * @return this item negated if this item is negative, or the item otherwise
   */
  @NonNull
  INumericItem abs();

  /**
   * Round the value to the whole number closest to positive infinity.
   *
   * @return the rounded value
   */
  @NonNull
  IIntegerItem ceiling();

  /**
   * Round the value to the whole number closest to negative infinity.
   *
   * @return the rounded value
   */
  @NonNull
  IIntegerItem floor();

  /**
   * Round the item's value with zero precision.
   * <p>
   * This is the same as calling {@link #round(IIntegerItem)} with a precision of
   * {@code 0}.
   *
   * @return the rounded value
   */
  @NonNull
  default INumericItem round() {
    return round(IIntegerItem.ZERO);
  }

  /**
   * Round the item's value with the specified precision.
   * <p>
   * This is the same as calling {@link #round(IIntegerItem)} with a precision of
   * {@code 0}.
   *
   * @param precisionItem
   *          the precision indicating the number of digits to round to before
   *          (negative value} or after (positive value) the decimal point.
   * @return the rounded value
   */
  @SuppressWarnings("PMD.CognitiveComplexity") // ok
  @NonNull
  default INumericItem round(@NonNull IIntegerItem precisionItem) {
    int precision;
    try {
      precision = FunctionUtils.asInteger(precisionItem);
    } catch (ArithmeticException ex) {
      throw new ArithmeticFunctionException(ArithmeticFunctionException.OVERFLOW_UNDERFLOW_ERROR,
          "Numeric operation overflow/underflow.", ex);
    }
    INumericItem retval;
    if (precision >= 0) {
      // round to precision decimal places
      if (this instanceof IIntegerItem) {
        retval = this;
      } else {
        // IDecimalItem
        BigDecimal value = asDecimal();
        if (value.signum() == -1) {
          retval = IDecimalItem.valueOf(
              ObjectUtils.notNull(
                  value.round(new MathContext(precision + value.precision() - value.scale(), RoundingMode.HALF_DOWN))));
        } else {
          retval = IDecimalItem.valueOf(
              ObjectUtils.notNull(
                  value.round(new MathContext(precision + value.precision() - value.scale(), RoundingMode.HALF_UP))));
        }

        // cast result to original type
        retval = castAsType(retval);
      }
    } else {
      // round to a power of 10
      BigInteger value = asInteger();
      BigInteger divisor = BigInteger.TEN.pow(0 - precision);

      @NonNull BigInteger result;
      if (divisor.compareTo(value.abs()) > 0) {
        result = ObjectUtils.notNull(BigInteger.ZERO);
      } else {
        BigInteger remainder = value.mod(divisor);
        BigInteger lessRemainder = value.subtract(remainder);
        BigInteger halfDivisor = divisor.divide(BigInteger.TWO);
        result = ObjectUtils.notNull(
            remainder.compareTo(halfDivisor) >= 0 ? lessRemainder.add(divisor) : lessRemainder);
      }
      retval = IIntegerItem.valueOf(result);
    }
    return retval;
  }
}
