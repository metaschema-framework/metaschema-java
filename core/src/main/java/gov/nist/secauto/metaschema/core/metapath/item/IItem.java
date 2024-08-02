/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.metapath.ICollectionValue;
import gov.nist.secauto.metaschema.core.metapath.ISequence;

import java.util.stream.Stream;

public interface IItem extends ICollectionValue {

  /**
   * Get the item's "wrapped" value. This "wrapped" value may be:
   * <ul>
   * <li>In the case of an Assembly, a Java object representing the fields and
   * flags of the assembly.</li>
   * <li>In the case of a Field with flags, a Java object representing the field
   * value and flags of the field.
   * <li>In the case of a Field without flags or a flag, a Java type managed by a
   * {@link IDataTypeAdapter} or a primitive type provided by the Java standard
   * library.
   * </ul>
   *
   * @return the value or {@code null} if the item has no available value
   */
  Object getValue();

  /**
   * Determine if the item has an associated value.
   *
   * @return {@code true} if the item has a non-{@code null} value or
   *         {@code false} otherwise
   */
  default boolean hasValue() {
    return getValue() != null;
  }

  @Override
  default ISequence<?> asSequence() {
    return ISequence.of(this);
  }

  @SuppressWarnings("null")
  @Override
  default Stream<? extends IItem> flatten() {
    return Stream.of(this);
  }
}
