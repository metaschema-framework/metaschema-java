/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.impl.AnyUriItemImpl;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An atomic Metapath item containing a URI data value.
 */
public interface IAnyUriItem extends IAnyAtomicItem {
  /**
   * Get the type information for this item.
   *
   * @return the type information
   */
  @NonNull
  static IAtomicOrUnionType<IAnyUriItem> type() {
    return MetaschemaDataTypeProvider.URI.getItemType();
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
  static IAnyUriItem valueOf(@NonNull String value) {
    try {
      return valueOf(MetaschemaDataTypeProvider.URI.parse(value));
    } catch (IllegalArgumentException ex) {
      throw new InvalidTypeMetapathException(
          null,
          String.format("Invalid URI value '%s'. %s",
              value,
              ex.getLocalizedMessage()),
          ex);
    }
  }

  /**
   * Construct a new URI item using the provided URI {@code value}.
   * <p>
   * Example usage:
   *
   * <pre>
   * URI uri = URI.create("http://example.com");
   * IAnyUriItem item = IAnyUriItem.valueOf(uri);
   * </pre>
   *
   * @param value
   *          a URI
   * @return the new item as a {@link IAnyUriItem} if the URI is absolute or
   *         opaque, otherwise as an {@link IUriReferenceItem}
   */
  @NonNull
  static IAnyUriItem valueOf(@NonNull URI value) {
    IAnyUriItem retval;
    if (value.isAbsolute() || value.isOpaque()) {
      retval = new AnyUriItemImpl(value);
    } else {
      retval = IUriReferenceItem.valueOf(value);
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
  static IAnyUriItem cast(@NonNull IAnyAtomicItem item) {
    try {
      return item instanceof IAnyUriItem
          ? (IAnyUriItem) item
          : valueOf(item.asString());
    } catch (IllegalStateException | InvalidTypeMetapathException ex) {
      // asString can throw IllegalStateException exception
      throw new InvalidValueForCastFunctionException(ex);
    }
  }

  @Override
  default IAnyUriItem castAsType(IAnyAtomicItem item) {
    return cast(item);
  }

  /**
   * Get the "wrapped" URI value.
   *
   * @return the underlying URI value
   */
  @NonNull
  URI asUri();

  /**
   * Determines if this URI has a scheme component, making it absolute.
   *
   * @return {@code true} if the URI is absolute, or {@code false} otherwise
   */
  default boolean isAbsolute() {
    return asUri().isAbsolute();
  }

  /**
   * Determines if this URI is opaque.
   *
   * @return {@code true} if the URI is opaque, or {@code false} otherwise
   * @see URI#isOpaque()
   */
  default boolean isOpaque() {
    return asUri().isOpaque();
  }

  /**
   * Resolve the provided URI against this URI.
   *
   * @param other
   *          the URI to resolve
   * @return the resolved URI
   * @see URI#resolve(URI)
   */
  @NonNull
  default IAnyUriItem resolve(@NonNull IAnyUriItem other) {
    return valueOf(ObjectUtils.notNull(asUri().resolve(other.asUri())));
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
  default int compareTo(@NonNull IAnyUriItem item) {
    return asUri().compareTo(item.asUri());
  }
}
