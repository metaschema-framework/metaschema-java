/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath;

import dev.metaschema.core.metapath.item.IItem;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents the focus context for Metapath evaluation, containing the context
 * item, position, and size as defined in the
 * <a href="https://www.w3.org/TR/xpath-31/#eval_context">XPath 3.1 evaluation
 * context</a>.
 * <p>
 * The focus context is established when evaluating predicates and provides the
 * information needed by the {@code fn:position()} and {@code fn:last()}
 * functions.
 */
public final class FocusContext {
  @NonNull
  private final IItem contextItem;
  private final int position;
  private final int size;

  private FocusContext(@NonNull IItem contextItem, int position, int size) {
    this.contextItem = contextItem;
    this.position = position;
    this.size = size;
  }

  /**
   * Create a new focus context for the given item at the specified position
   * within a sequence.
   *
   * @param item
   *          the context item
   * @param position
   *          the 1-based position of the item within the sequence
   * @param size
   *          the total number of items in the sequence
   * @return a new focus context
   * @throws IllegalArgumentException
   *           if position is less than 1, size is less than 1, or position is
   *           greater than size
   */
  @NonNull
  public static FocusContext of(@NonNull IItem item, int position, int size) {
    if (position < 1) {
      throw new IllegalArgumentException("Position must be >= 1, got: " + position);
    }
    if (size < 1) {
      throw new IllegalArgumentException("Size must be >= 1, got: " + size);
    }
    if (position > size) {
      throw new IllegalArgumentException(
          String.format("Position (%d) cannot be greater than size (%d)", position, size));
    }
    return new FocusContext(item, position, size);
  }

  /**
   * Get the context item.
   *
   * @return the context item
   */
  @NonNull
  public IItem getContextItem() {
    return contextItem;
  }

  /**
   * Get the context position.
   * <p>
   * This is the 1-based position of the context item within the sequence
   * currently being processed, as returned by {@code fn:position()}.
   *
   * @return the context position (1-based)
   */
  public int getPosition() {
    return position;
  }

  /**
   * Get the context size.
   * <p>
   * This is the total number of items in the sequence currently being processed,
   * as returned by {@code fn:last()}.
   *
   * @return the context size
   */
  public int getSize() {
    return size;
  }
}
