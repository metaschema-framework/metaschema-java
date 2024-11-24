/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.impl.UriReferenceItemImpl;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An atomic Metapath item containing a URI reference data value that complies
 * with RFC2396. URI references can be absolute URIs, relative URIs, or
 * same-document references.
 *
 * @see java.net.URI
 * @see <a href="https://www.ietf.org/rfc/rfc2396.txt">RFC2396</a>
 */
public interface IUriReferenceItem extends IAnyUriItem {
  /**
   * Get the type information for this item.
   *
   * @return the type information
   */
  @NonNull
  static IAtomicOrUnionType<IUriReferenceItem> type() {
    return MetaschemaDataTypeProvider.URI_REFERENCE.getItemType();
  }

  /**
   * Construct a new URI item using the provided string {@code value}.
   *
   * @param value
   *          a string representing a URI
   * @return the new item
   * @throws InvalidTypeMetapathException
   *           if the given string violates RFC2396
   */
  @NonNull
  static IUriReferenceItem valueOf(@NonNull String value) {
    try {
      return valueOf(MetaschemaDataTypeProvider.URI_REFERENCE.parse(value));
    } catch (IllegalArgumentException ex) {
      throw new InvalidTypeMetapathException(
          null,
          String.format("Invalid URI reference value '%s'. %s",
              value,
              ex.getLocalizedMessage()),
          ex);
    }
  }

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
    try {
      return item instanceof IUriReferenceItem
          ? (IUriReferenceItem) item
          : item instanceof IAnyUriItem
              ? valueOf(((IAnyUriItem) item).asUri())
              : valueOf(item.asString());
    } catch (IllegalStateException | InvalidTypeMetapathException ex) {
      // asString can throw IllegalStateException exception
      throw new InvalidValueForCastFunctionException(ex);
    }
  }

  @Override
  default IUriReferenceItem castAsType(IAnyAtomicItem item) {
    return cast(item);
  }
}
