/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A Metapath atomic item with a boolean value.
 */
public interface IBooleanItem extends IAnyAtomicItem {
  /**
   * The boolean item value of {@code true}.
   */
  @NonNull
  IBooleanItem TRUE = new BooleanItemImpl(true);
  /**
   * The boolean item value of {@code false}.
   */
  @NonNull
  IBooleanItem FALSE = new BooleanItemImpl(false);

  /**
   * Construct a new boolean item using the provided string {@code value}.
   * <p>
   * The item will be {@link #TRUE} if the value is "1" or "true", or
   * {@link #FALSE} otherwise
   *
   * @param value
   *          a string representing a boolean value
   * @return the new item
   */
  @NonNull
  static IBooleanItem valueOf(@NonNull String value) {
    IBooleanItem retval;
    if ("1".equals(value)) {
      retval = TRUE;
    } else {
      try {
        Boolean bool = MetaschemaDataTypeProvider.BOOLEAN.parse(value);
        retval = valueOf(bool);
      } catch (IllegalArgumentException ex) {
        throw new InvalidValueForCastFunctionException(String.format("Unable to parse string value '%s'", value),
            ex);
      }
    }
    return retval;
  }

  /**
   * Construct a new boolean item using the provided {@code value}.
   *
   * @param value
   *          a boolean
   * @return the new item
   */
  @NonNull
  static IBooleanItem valueOf(boolean value) {
    return value ? TRUE : FALSE;
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
  static IBooleanItem cast(@NonNull IAnyAtomicItem item) {
    return MetaschemaDataTypeProvider.BOOLEAN.cast(item);
  }

  /**
   * Get the "wrapped" boolean value.
   *
   * @return the underlying boolean value
   */
  boolean toBoolean();

  @Override
  default IBooleanItem castAsType(IAnyAtomicItem item) {
    return cast(item);
  }

  /**
   * Get the boolean negation of this value.
   *
   * @return the negated boolean value
   */
  @NonNull
  default IBooleanItem negate() {
    return this.toBoolean() ? FALSE : TRUE;
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
  default int compareTo(@NonNull IBooleanItem item) {
    return Boolean.compare(toBoolean(), item.toBoolean());
  }
}
