/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.impl;

import gov.nist.secauto.metaschema.core.metapath.item.ICollectionValue;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An array item that supports an unbounded number of items.
 *
 * @param <ITEM>
 *          the Java type of the items
 */
public class ArrayItemN<ITEM extends ICollectionValue>
    extends AbstractArrayItem<ITEM> {
  @NonNull
  private final List<ITEM> items;

  /**
   * Construct a new array item with the provided items.
   *
   * @param items
   *          the items to add to the array
   */
  @SafeVarargs
  public ArrayItemN(@NonNull ITEM... items) {
    this(ObjectUtils.notNull(List.of(items)));
  }

  /**
   * Construct a new array item using the items from the provided list.
   *
   * @param items
   *          a list containing the items to add to the array
   */
  public ArrayItemN(@NonNull List<ITEM> items) {
    this.items = CollectionUtil.unmodifiableList(items);
  }

  @Override
  public List<ITEM> getValue() {
    return items;
  }

}
