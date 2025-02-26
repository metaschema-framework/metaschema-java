/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.impl.BooleanItemImpl;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An atomic Metapath item with a boolean value.
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
   * Get the type information for this item.
   *
   * @return the type information
   */
  @NonNull
  static IAtomicOrUnionType<IBooleanItem> type() {
    return MetaschemaDataTypeProvider.BOOLEAN.getItemType();
  }

  @Override
  default IAtomicOrUnionType<IBooleanItem> getType() {
    return type();
  }

  /**
   * Construct a new boolean item using the provided string {@code value}.
   * <p>
   * The item will be {@link #TRUE} if the value is "1" or "true", or
   * {@link #FALSE} otherwise
   *
   * @param value
   *          a string representing a boolean value
   * @return the new item
   * @throws InvalidTypeMetapathException
   *           if the provided value is not a valid boolean value
   */
  @NonNull
  static IBooleanItem valueOf(@NonNull String value) {
    IBooleanItem retval;
    if ("1".equals(value)) {
      retval = TRUE;
    } else {
      try {
        retval = valueOf(MetaschemaDataTypeProvider.BOOLEAN.parse(value));
      } catch (IllegalArgumentException ex) {
        throw new InvalidTypeMetapathException(
            null,
            String.format("Invalid boolean value '%s'. %s",
                value,
                ex.getLocalizedMessage()),
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
    IBooleanItem retval;
    if (item instanceof INumericItem) {
      retval = valueOf(((INumericItem) item).toEffectiveBoolean());
    } else {
      try {
        retval = valueOf(INumericItem.cast(item).toEffectiveBoolean());
      } catch (InvalidValueForCastFunctionException ex) {
        try {
          retval = valueOf(item.asString());
        } catch (IllegalStateException | InvalidTypeMetapathException ex2) {
          // asString can throw IllegalStateException exception
          InvalidValueForCastFunctionException thrown = new InvalidValueForCastFunctionException(ex2);
          thrown.addSuppressed(ex);
          throw thrown;
        }
      }
    }
    return retval;
  }

  @Override
  default IBooleanItem castAsType(IAnyAtomicItem item) {
    return cast(item);
  }

  /**
   * Get the "wrapped" boolean value.
   *
   * @return the underlying boolean value
   */
  boolean toBoolean();

  /**
   * Get the boolean negation of this value.
   *
   * @return the negated boolean value
   */
  @NonNull
  default IBooleanItem negate() {
    return this.toBoolean() ? FALSE : TRUE;
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
