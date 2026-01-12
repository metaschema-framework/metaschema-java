/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.type;

import dev.metaschema.core.metapath.item.IItem;
import dev.metaschema.core.metapath.item.function.IArrayItem;
import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Declares the expected type information for an {@link IArrayItem}.
 */
@FunctionalInterface
public interface IArrayTest extends IItemType {
  @SuppressWarnings({ "rawtypes" })
  @Override
  default Class<? extends IArrayItem> getItemClass() {
    return IArrayItem.class;
  }

  /**
   * Get the sequence test to use to check the array's contents.
   *
   * @return the sequence type
   */
  @NonNull
  ISequenceType getValueType();

  @Override
  default boolean isInstance(IItem item) {
    boolean retval = getItemClass().isInstance(item);
    if (retval) {
      // this is an array
      IArrayItem<?> array = (IArrayItem<?>) item;
      retval = getValueType().matches(array.contentsAsSequence());
    }
    return retval;
  }

  @Override
  default String toSignature() {
    return ObjectUtils.notNull(
        new StringBuilder()
            .append("array(")
            .append(getValueType().toSignature())
            .append(')')
            .toString());
  }
}
