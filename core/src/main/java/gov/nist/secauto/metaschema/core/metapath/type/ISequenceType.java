/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.type;

import gov.nist.secauto.metaschema.core.metapath.ICollectionValue;
import gov.nist.secauto.metaschema.core.metapath.type.impl.SequenceTypeImpl;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface ISequenceType {

  @NonNull
  static ISequenceType empty() {
    return SequenceTypeImpl.EMPTY;
  }

  /**
   * Create new sequence type using the provide type and occurrence.
   *
   * @param type
   *          the required sequence item type
   * @param occurrence
   *          the expected occurrence of the sequence
   * @return the new sequence type
   */
  @SuppressWarnings("PMD.ShortMethodName")
  @NonNull
  static ISequenceType of(
      @NonNull IItemType type,
      @NonNull Occurrence occurrence) {
    return Occurrence.ZERO.equals(occurrence)
        ? empty()
        : new SequenceTypeImpl(type, occurrence);
  }

  /**
   * Determine if the sequence is empty (if it holds any data) or not.
   *
   * @return {@code true} if the sequence is empty or {@code false} otherwise
   */
  boolean isEmpty();

  /**
   * Get the type of the sequence.
   *
   * @return the type of the sequence or {@code null} if the sequence is empty
   */
  @NonNull
  IItemType getType();

  /**
   * Get the occurrence of the sequence.
   *
   * @return the occurrence of the sequence or {@code Occurrence#ZERO} if the
   *         sequence is empty
   */
  @NonNull
  Occurrence getOccurrence();

  /**
   * Get the signature of the function as a string.
   *
   * @return the signature
   */
  @NonNull
  String toSignature();

  boolean matches(@NonNull ICollectionValue item);
}
