/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.impl;

import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A Metapath sequence supporting an unbounded number of items.
 *
 * @param <ITEM>
 *          the Java type of the items
 */
public class SequenceN<ITEM extends IItem>
    extends AbstractSequence<ITEM> {
  /**
   * The singleton empty sequence instance.
   * <p>
   * This field is located in SequenceN rather than AbstractSequence to prevent
   * class initialization deadlock. Since SequenceN extends AbstractSequence,
   * AbstractSequence is always initialized first, ensuring no circular dependency
   * when multiple threads initialize these classes concurrently.
   */
  @NonNull
  private static final ISequence<?> EMPTY = new SequenceN<>();

  /**
   * Get an immutable sequence that is empty.
   *
   * @param <T>
   *          the item Java type
   * @return the empty sequence
   */
  @SuppressWarnings("unchecked")
  public static <T extends IItem> ISequence<T> empty() {
    return (ISequence<T>) EMPTY;
  }

  @NonNull
  private final List<ITEM> items;

  /**
   * Construct a new sequence with the provided items.
   *
   * @param items
   *          a collection containing the items to add to the sequence
   * @param copy
   *          if {@code true} make a defensive copy of the list or {@code false}
   *          otherwise
   */
  public SequenceN(@NonNull List<ITEM> items, boolean copy) {
    this.items = CollectionUtil.unmodifiableList(copy ? new ArrayList<>(items) : items);
  }

  /**
   * Construct a new sequence with the provided items.
   *
   * @param items
   *          the items to add to the sequence
   */
  @SafeVarargs
  public SequenceN(@NonNull ITEM... items) {
    this(ObjectUtils.notNull(List.of(items)), false);
  }

  /**
   * Construct a new sequence with the provided items.
   *
   * @param items
   *          a collection containing the items to add to the sequence
   */
  public SequenceN(@NonNull Collection<ITEM> items) {
    this(new ArrayList<>(items), false);
  }

  /**
   * Construct a new sequence with the provided items.
   *
   * @param items
   *          a list containing the items to add to the sequence
   */
  public SequenceN(@NonNull List<ITEM> items) {
    this(items, false);
  }

  @Override
  public List<ITEM> asList() {
    return items;
  }
}
