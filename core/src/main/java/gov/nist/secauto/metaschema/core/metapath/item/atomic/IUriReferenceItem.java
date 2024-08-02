/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IUriReferenceItem extends IAnyUriItem {
  /**
   * Construct a new item using the provided URI {@code value}.
   *
   * @param value
   *          a string representing a URI
   * @return the new item as a {@link IUriReferenceItem}
   */
  @NonNull
  static IUriReferenceItem valueOf(@NonNull URI value) {
    return new UriReferenceItemImpl(value);
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
  static IUriReferenceItem cast(@NonNull IAnyAtomicItem item) {
    return MetaschemaDataTypeProvider.URI_REFERENCE.cast(item);
  }

  @Override
  default IUriReferenceItem castAsType(IAnyAtomicItem item) {
    return cast(item);
  }
}
