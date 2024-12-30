/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.impl;

import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A Metapath sequence supporting a singleton item.
 *
 * @param <ITEM>
 *          the Java type of the items
 */
public class SingletonSequence<ITEM extends IItem>
    extends AbstractSequence<ITEM> {
  @NonNull
  private final ITEM item;

  /**
   * Construct a new sequence with the provided item.
   *
   * @param item
   *          the item to add to the sequence
   */
  public SingletonSequence(@NonNull ITEM item) {
    this.item = item;
  }

  @Override
  public List<ITEM> asList() {
    return CollectionUtil.singletonList(item);
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public int size() {
    return 1;
  }

  @SuppressWarnings("null")
  @Override
  public Stream<ITEM> stream() {
    return Stream.of(item);
  }

  @Override
  public void forEach(Consumer<? super ITEM> action) {
    action.accept(item);
  }
}
