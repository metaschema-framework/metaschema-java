/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.item;

import dev.metaschema.core.metapath.function.IFunction;
import dev.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import dev.metaschema.core.metapath.item.function.IArrayItem;
import dev.metaschema.core.metapath.item.function.IMapItem;
import dev.metaschema.core.metapath.item.node.INodeItem;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Use to visit the major types of items.
 */
public interface IItemVisitor {
  /**
   * Visit the array item instance.
   *
   * @param array
   *          the instance to visit
   */
  void visit(@NonNull IArrayItem<?> array);

  /**
   * Visit the map item instance.
   *
   * @param map
   *          the instance to visit
   */
  void visit(@NonNull IMapItem<?> map);

  /**
   * Visit the node item instance.
   *
   * @param node
   *          the instance to visit
   */
  void visit(@NonNull INodeItem node);

  /**
   * Visit the atomic item instance.
   *
   * @param item
   *          the instance to visit
   */
  void visit(@NonNull IAnyAtomicItem item);

  /**
   * Visit the function item instance.
   *
   * @param function
   *          the instance to visit
   */
  void visit(@NonNull IFunction function);
}
