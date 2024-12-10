/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.type;

import gov.nist.secauto.metaschema.core.metapath.item.ICollectionValue;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
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

  /**
   * Test if the provided sequence matches this sequence type.
   *
   * @param sequence
   *          the sequence to test
   * @return the sequence if the test passes
   * @throw InvalidTypeMetapathException if the test fails because the sequence is
   *        not the required type
   */
  @NonNull
  default <T extends IItem> ISequence<T> test(@NonNull ISequence<T> sequence) {
    if (matches(sequence)) {
      return sequence;
    }
    throw new InvalidTypeMetapathException(
        null,
        String.format("The argument '%s' is not a '%s'",
            sequence.toSignature(),
            toSignature()));
  }

  /**
   * Test if the provided item matches this sequence type.
   *
   * @param item
   *          the item to test
   * @return the item if the test passes
   * @throw InvalidTypeMetapathException if the test fails because the item is not
   *        the required type
   */
  @NonNull
  default <T extends IItem> T test(@NonNull T item) {
    ISequence<T> sequence = ISequence.of(item);
    if (matches(sequence)) {
      return item;
    }
    throw new InvalidTypeMetapathException(
        null,
        String.format("The item '%s' is not a '%s'",
            item.toSignature(),
            toSignature()));
  }
}
