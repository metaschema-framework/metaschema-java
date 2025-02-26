/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.IArrayItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.IMapItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * A data value that can be a value in a Metapath array or map.
 */
public interface ICollectionValue {
  /**
   * Get the collection value as a sequence.
   * <p>
   * If the value is already a sequence, the value is returned as a sequence.
   * Otherwise, if the value is an item, a new sequence will be created containing
   * only that item.
   *
   * @return the resulting sequence
   */
  @NonNull
  default ISequence<?> toSequence() {
    return this instanceof ISequence
        // return the sequence
        ? (ISequence<?>) this
        // return the item as a new sequence
        : ISequence.of((IItem) this);
  }

  /**
   * Get the collection value as a sequence.
   * <p>
   * If the value is already a sequence, the value is returned as a sequence.
   * Otherwise, if the value is an item, what is returned depends on the item
   * type:
   * <ul>
   * <li>{@link IArrayItem} or {@link IMapItem}: the contents of the returned
   * sequence are the items of the array or map. Any member values that are a
   * sequence are flattened.
   * <li>Any other item: A singleton sequence is returned containing the item.
   * </ul>
   *
   * @return the resulting sequence
   */
  @NonNull
  ISequence<?> contentsAsSequence();

  /**
   * Get the stream of items for the collection value.
   * <p>
   * If the collection value is a sequence, then the items in the collection are
   * returned.
   *
   * @param value
   *          the collection value
   * @return the sequence of related items
   */
  @NonNull
  static Stream<? extends IItem> normalizeAsItems(@NonNull ICollectionValue value) {
    return value instanceof IItem
        ? ObjectUtils.notNull(Stream.of((IItem) value))
        : value.toSequence().stream();
  }

  /**
   * Produce a stream of atomic items based on the atomic value of these items.
   * <p>
   * Supports <a href="https://www.w3.org/TR/xpath-31/#id-atomization">item
   * atomization</a>.
   *
   * @return a stream of atomized atomic items.
   */
  @NonNull
  Stream<IAnyAtomicItem> atomize();

  /**
   * Get the stream of items for the collection value.
   * <p>
   * If the collection value contains items, then these items are returned.
   *
   * @return a stream of related items
   */
  @NonNull
  Stream<? extends IItem> flatten();

  /**
   * Determine if this and the other value are deeply equal.
   * <p>
   * Item equality is defined by the
   * <a href="https://www.w3.org/TR/xpath-functions-31/#func-deep-equal">XPath 3.1
   * fn:deep-equal</a> specification.
   *
   * @param other
   *          the other value to compare to this value to
   * @param dynamicContext
   *          used to provide evaluation information, including the implicit
   *          timezone
   * @return the {@code true} if the two values are equal, or {@code false}
   *         otherwise
   */
  boolean deepEquals(@Nullable ICollectionValue other, @NonNull DynamicContext dynamicContext);

  /**
   * Determine if this and the other value are deeply equal.
   * <p>
   * Item equality is defined by the
   * <a href="https://www.w3.org/TR/xpath-functions-31/#func-deep-equal">XPath 3.1
   * fn:deep-equal</a> specification.
   *
   * @param other
   *          the other value to compare to this value to
   * @return the {@code true} if the two values are equal, or {@code false}
   *         otherwise
   */
  default boolean deepEquals(@Nullable ICollectionValue other) {
    // use a default dynamic context
    // FIXME: Refactor DynamicContext to be an interface and provide a lightweight
    // version with the implicit timezone.
    return deepEquals(other, new DynamicContext());
  }

  /**
   * Get a representation of the value based on its type signature.
   *
   * @return the signature
   */
  @NonNull
  String toSignature();

  /**
   * Provides a {@link Predicate} which filters items in a stream returning
   * distinct values based on {@link #deepEquals(ICollectionValue)}.
   * <p>
   * These values should be first normalized using
   * {@link #normalize(DynamicContext)} to ensure their comparison is against
   * consistent values.
   *
   * @param dynamicContext
   *          used to provide evaluation information, including the implicit
   *          timezone
   * @return the predicate
   */
  static Predicate<? super ICollectionValue> distinctByDeepEquals(@NonNull DynamicContext dynamicContext) {
    return distinctByDeepEquals(ICollectionValue.class, dynamicContext);
  }

  /**
   * Provides a {@link Predicate} which filters items in a stream returning
   * distinct values based on {@link #deepEquals(ICollectionValue)}.
   * <p>
   * These values should be first normalized using
   * {@link #normalize(DynamicContext)} to ensure their comparison is against
   * consistent values.
   *
   * @param clazz
   *          the Java class for the type handled by the predicate
   * @param dynamicContext
   *          used to provide evaluation information, including the implicit
   *          timezone
   * @return the predicate
   */
  static <T extends ICollectionValue> Predicate<? super T> distinctByDeepEquals(
      @NonNull Class<T> clazz,
      @NonNull DynamicContext dynamicContext) {
    List<T> seen = new LinkedList<>();
    return item -> !seen.stream().anyMatch(other -> item.deepEquals(other, dynamicContext))
        && seen.add(item); // true
  }
}
