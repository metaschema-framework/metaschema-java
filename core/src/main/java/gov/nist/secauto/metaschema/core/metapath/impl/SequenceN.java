/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.impl;

import gov.nist.secauto.metaschema.core.metapath.item.IItem;
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
  public List<ITEM> getValue() {
    return items;
  }
}
