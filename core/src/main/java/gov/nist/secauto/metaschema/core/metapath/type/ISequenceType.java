/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.type;

import gov.nist.secauto.metaschema.core.metapath.ICollectionValue;
import gov.nist.secauto.metaschema.core.metapath.type.impl.SequenceTypeImpl;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Supports
 * <a href="https://www.w3.org/TR/xpath-31/#id-sequencetype-syntax">sequence
 * type</a> testing.
 */
public interface ISequenceType {
  /**
   * A sequence type representing an empty sequence.
   *
   * @return the sequence type
   */
  @NonNull
  static ISequenceType empty() {
    return SequenceTypeImpl.empty();
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

  /**
   * Tests that a given collection value matches this sequence type.
   *
   * @param value
   *          the collection value to test
   * @return {@code true} if the value matches the expectations of this sequence
   *         type or {@code false} otherwise
   */
  boolean matches(@NonNull ICollectionValue value);
}
