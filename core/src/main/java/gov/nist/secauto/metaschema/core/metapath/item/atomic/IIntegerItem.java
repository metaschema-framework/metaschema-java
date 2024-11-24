/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.impl.IntegerItemImpl;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.math.BigInteger;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An atomic Metapath item containing an integer data value.
 */
public interface IIntegerItem extends IDecimalItem {
  /**
   * The integer value "1".
   */
  @NonNull
  IIntegerItem ONE = valueOf(ObjectUtils.notNull(BigInteger.ONE));
  /**
   * The integer value "0".
   */
  @NonNull
  IIntegerItem ZERO = valueOf(ObjectUtils.notNull(BigInteger.ZERO));
  /**
   * The integer value "-1".
   */
  @NonNull
  IIntegerItem NEGATIVE_ONE = valueOf(ObjectUtils.notNull(BigInteger.ONE.negate()));

  /**
   * Get the type information for this item.
   *
   * @return the type information
   */
  @NonNull
  static IAtomicOrUnionType<IIntegerItem> type() {
    return MetaschemaDataTypeProvider.INTEGER.getItemType();
  }

  /**
   * Create an item from an existing integer value.
   *
   * @param value
   *          a string representing an integer value
   * @return the item
   * @throws InvalidTypeMetapathException
   *           if the provided value is not an integer
   */
  @NonNull
  static IIntegerItem valueOf(@NonNull String value) {
    try {
      return valueOf(MetaschemaDataTypeProvider.INTEGER.parse(value));
    } catch (NumberFormatException ex) {
      throw new InvalidTypeMetapathException(null,
          ex.getMessage(),
          ex);
    }
  }

  /**
   * Construct a new integer item using the provided {@code value}.
   *
   * @param value
   *          a long value
   * @return the new item
   */
  @NonNull
  static IIntegerItem valueOf(int value) {
    @SuppressWarnings("null")
    @NonNull
    BigInteger bigInteger = BigInteger.valueOf(value);
    return valueOf(bigInteger);
  }

  /**
   * Construct a new integer item using the provided {@code value}.
   *
   * @param value
   *          a long value
   * @return the new item
   */
  @NonNull
  static IIntegerItem valueOf(long value) {
    @SuppressWarnings("null")
    @NonNull
    BigInteger bigInteger = BigInteger.valueOf(value);
    return valueOf(bigInteger);
  }

  /**
   * Construct a new integer item using the provided {@code value}.
   *
   * @param value
   *          a long value
   * @return the new item
   */
  @NonNull
  static IIntegerItem valueOf(boolean value) {
    return valueOf(ObjectUtils.notNull(value ? BigInteger.ONE : BigInteger.ZERO));
  }

  /**
   * Construct a new integer item using the provided {@code value}.
   *
   * @param value
   *          an integer value
   * @return the new item
   */
  @NonNull
  static IIntegerItem valueOf(@NonNull BigInteger value) {
    int signum = value.signum();

    IIntegerItem retval;
    if (signum == -1) { // negative
      retval = new IntegerItemImpl(value);
    } else if (signum == 0) { // zero
      retval = INonNegativeIntegerItem.valueOf(value);
    } else { // positive
      retval = IPositiveIntegerItem.valueOf(value);
    }
    return retval;
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
  static IIntegerItem cast(@NonNull IAnyAtomicItem item) {
    IIntegerItem retval;
    if (item instanceof IIntegerItem) {
      retval = (IIntegerItem) item;
    } else if (item instanceof INumericItem) {
      retval = valueOf(((INumericItem) item).asInteger());
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
  default IIntegerItem castAsType(IAnyAtomicItem item) {
    return cast(item);
  }

  @Override
  IIntegerItem abs();

  @Override
  default IIntegerItem ceiling() {
    return this;
  }

  @Override
  default IIntegerItem floor() {
    return this;
  }

  @Override
  default int compareTo(IAnyAtomicItem item) {
    return compareTo(cast(item));
  }

  /**
   * Compares this value with the argument.
   *
   * @param item
   *          the item to compare with this value
   * @return a negative integer, zero, or a positive integer if this value is less
   *         than, equal to, or greater than the {@code item}.
   */
  default int compareTo(IIntegerItem item) {
    return asInteger().compareTo(item.asInteger());
  }
}
