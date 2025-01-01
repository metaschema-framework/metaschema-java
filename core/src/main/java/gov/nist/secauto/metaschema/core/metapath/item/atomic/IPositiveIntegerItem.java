/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.impl.PositiveIntegerItemImpl;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;

import java.math.BigInteger;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An atomic Metapath item containing a positive integer data value.
 */
public interface IPositiveIntegerItem extends INonNegativeIntegerItem {
  /**
   * The integer value "1".
   */
  @SuppressWarnings("null")
  @NonNull
  IPositiveIntegerItem ONE = valueOf(BigInteger.ONE);

  /**
   * Get the type information for this item.
   *
   * @return the type information
   */
  @NonNull
  static IAtomicOrUnionType<IPositiveIntegerItem> type() {
    return MetaschemaDataTypeProvider.POSITIVE_INTEGER.getItemType();
  }

  @Override
  default IAtomicOrUnionType<IPositiveIntegerItem> getType() {
    return type();
  }

  /**
   * Create an item from an existing integer value.
   *
   * @param value
   *          a string representing an integer value
   * @return the item
   * @throws InvalidTypeMetapathException
   *           if the provided value is not a positive integer
   */
  @NonNull
  static IPositiveIntegerItem valueOf(@NonNull String value) {
    try {
      return valueOf(MetaschemaDataTypeProvider.POSITIVE_INTEGER.parse(value));
    } catch (IllegalArgumentException ex) {
      throw new InvalidTypeMetapathException(
          null,
          String.format("Invalid positive integer value '%s'. %s",
              value,
              ex.getLocalizedMessage()),
          ex);
    }
  }

  /**
   * Create an item from an existing integer value.
   *
   * @param value
   *          an integer value
   * @return the item
   * @throws InvalidTypeMetapathException
   *           if the provided value is not a positive integer
   */
  @NonNull
  static IPositiveIntegerItem valueOf(@NonNull INumericItem value) {
    return value instanceof IPositiveIntegerItem ? (IPositiveIntegerItem) value : valueOf(value.asInteger());
  }

  /**
   * Create an item from an existing integer value.
   *
   * @param value
   *          an integer value
   * @return the item
   * @throws InvalidTypeMetapathException
   *           if the provided value is not a positive integer
   */
  @SuppressWarnings("null")
  @NonNull
  static IPositiveIntegerItem valueOf(long value) {
    return valueOf(BigInteger.valueOf(value));
  }

  /**
   * Create an item from an existing integer value.
   *
   * @param value
   *          an integer value
   * @return the item
   * @throws InvalidTypeMetapathException
   *           if the provided value is not a positive integer
   */
  @NonNull
  static IPositiveIntegerItem valueOf(@NonNull BigInteger value) {
    if (value.compareTo(BigInteger.ZERO) <= 0) {
      throw new InvalidTypeMetapathException(
          null,
          String.format("Integer value '%s' is negative or zero.", value));
    }
    return new PositiveIntegerItemImpl(value);
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
  static IPositiveIntegerItem cast(@NonNull IAnyAtomicItem item) {
    try {
      return item instanceof IPositiveIntegerItem
          ? (IPositiveIntegerItem) item
          : item instanceof INumericItem
              ? valueOf((INumericItem) item)
              : valueOf(item.asString());
    } catch (IllegalStateException | InvalidTypeMetapathException ex) {
      // asString can throw IllegalStateException exception
      throw new InvalidValueForCastFunctionException(ex);
    }
  }

  @Override
  default IPositiveIntegerItem castAsType(IAnyAtomicItem item) {
    return cast(item);
  }
}
