/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.impl.NcNameItemImpl;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An atomic Metapath item containing a non-colonized name (NCName) data value.
 */
@Deprecated(forRemoval = true, since = "0.7.0")
public interface INcNameItem extends IStringItem {
  /**
   * Get the type information for this item.
   *
   * @return the type information
   */
  @NonNull
  static IAtomicOrUnionType<INcNameItem> type() {
    return MetaschemaDataTypeProvider.NCNAME.getItemType();
  }

  /**
   * Construct a new item using the provided string {@code value}.
   *
   * @param value
   *          a string representing a NCName value
   * @return the new item
   */

  @NonNull
  static INcNameItem valueOf(@NonNull String value) {
    try {
      return new NcNameItemImpl(MetaschemaDataTypeProvider.NCNAME.parse(value));
    } catch (IllegalArgumentException ex) {
      throw new InvalidTypeMetapathException(
          null,
          String.format("Invalid non-colonized name value '%s'. %s",
              value,
              ex.getLocalizedMessage()),
          ex);
    }
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
  static INcNameItem cast(@NonNull IAnyAtomicItem item) {
    try {
      return item instanceof INcNameItem
          ? (INcNameItem) item
          : valueOf(item.asString());
    } catch (IllegalStateException | InvalidTypeMetapathException ex) {
      // asString can throw IllegalStateException exception
      throw new InvalidValueForCastFunctionException(ex);
    }
  }

  @Override
  default INcNameItem castAsType(IAnyAtomicItem item) {
    return cast(item);
  }
}
