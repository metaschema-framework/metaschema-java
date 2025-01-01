/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.impl.NonNegativeIntegerItemImpl;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.math.BigInteger;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An atomic Metapath item containing a non-negative integer data value.
 */
public interface INonNegativeIntegerItem extends IIntegerItem {
  /**
   * The integer value "1".
   */
  @NonNull
  INonNegativeIntegerItem ONE = valueOf(ObjectUtils.notNull(BigInteger.ONE));
  /**
   * The integer value "0".
   */
  @NonNull
  INonNegativeIntegerItem ZERO = valueOf(ObjectUtils.notNull(BigInteger.ZERO));

  /**
   * Get the type information for this item.
   *
   * @return the type information
   */
  @NonNull
  static IAtomicOrUnionType<INonNegativeIntegerItem> type() {
    return MetaschemaDataTypeProvider.NON_NEGATIVE_INTEGER.getItemType();
  }

  @Override
  default IAtomicOrUnionType<? extends INonNegativeIntegerItem> getType() {
    return type();
  }

  /**
   * Create an item from an existing integer value.
   *
   * @param value
   *          a string representing an integer value
   * @return the item
   * @throws InvalidTypeMetapathException
   *           if the provided value is not a non-negative integer
   */
  @NonNull
  static INonNegativeIntegerItem valueOf(@NonNull String value) {
    try {
      return valueOf(MetaschemaDataTypeProvider.NON_NEGATIVE_INTEGER.parse(value));
    } catch (IllegalArgumentException ex) {
      throw new InvalidTypeMetapathException(
          null,
          String.format("Invalid non-negative integer value '%s'. %s",
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
   *           if the provided value is not a non-negative integer
   */
  @NonNull
  static INonNegativeIntegerItem valueOf(@NonNull INumericItem value) {
    return value instanceof INonNegativeIntegerItem ? (INonNegativeIntegerItem) value : valueOf(value.asInteger());
  }

  /**
   * Create an item from an existing integer value.
   *
   * @param value
   *          an integer value
   * @return the item
   * @throws InvalidTypeMetapathException
   *           if the provided value is not a non-negative integer
   */
  @SuppressWarnings("null")
  @NonNull
  static INonNegativeIntegerItem valueOf(long value) {
    return valueOf(BigInteger.valueOf(value));
  }

  /**
   * Create an item from an existing integer value.
   *
   * @param value
   *          an integer value
   * @return the item
   * @throws InvalidTypeMetapathException
   *           if the provided value is not a non-negative integer
   */
  @NonNull
  static INonNegativeIntegerItem valueOf(@NonNull BigInteger value) {
    if (value.compareTo(BigInteger.ZERO) < 0) {
      throw new InvalidTypeMetapathException(
          null,
          String.format("Integer value '%s' must not be negative.", value));
    }
    return new NonNegativeIntegerItemImpl(value);
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
  static INonNegativeIntegerItem cast(@NonNull IAnyAtomicItem item) {
    try {
      return item instanceof INonNegativeIntegerItem
          ? (INonNegativeIntegerItem) item
          : item instanceof INumericItem
              ? valueOf((INumericItem) item)
              : valueOf(item.asString());
    } catch (InvalidTypeMetapathException ex) {
      throw new InvalidValueForCastFunctionException(ex);
    }
  }

  @Override
  default INonNegativeIntegerItem castAsType(IAnyAtomicItem item) {
    return cast(item);
  }
}
