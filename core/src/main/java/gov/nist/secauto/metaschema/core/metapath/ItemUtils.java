/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath;

import gov.nist.secauto.metaschema.core.metapath.cst.path.Axis;
import gov.nist.secauto.metaschema.core.metapath.node.IDocumentBasedNodeItem;
import gov.nist.secauto.metaschema.core.metapath.node.INodeItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Provides a variety of utilities for working with Metapath items.
 */
public final class ItemUtils {

  private ItemUtils() {
    // disable construction
  }

  /**
   * Checks that the item is an {@link INodeItem}.
   *
   * @param item
   *          the item to check
   * @return the item cast to a {@link INodeItem}
   * @throws TypeMetapathError
   *           if the item is {@code null} or not an {@link INodeItem}
   */
  @NonNull
  public static INodeItem checkItemIsNodeItem(@Nullable IItem item) {
    return checkItemIsType(item, INodeItem.class);
  }

  @NonNull
  private static <T extends IItem> T checkItemIsType(@Nullable IItem item, @NonNull Class<T> itemClass) {
    if (itemClass.isInstance(item)) {
      return ObjectUtils.notNull(itemClass.cast(item));
    }

    if (item == null) {
      throw new TypeMetapathError(TypeMetapathError.NOT_A_NODE_ITEM_FOR_STEP,
          "Item is null.");
    }

    throw new TypeMetapathError(TypeMetapathError.NOT_A_NODE_ITEM_FOR_STEP,
        String.format(
            "The item of type '%s' is not of the type '%s'.",
            item.getClass().getName(),
            itemClass.getName()));
  }

  @NonNull
  public static ISequence<IDocumentBasedNodeItem> getDocumentNodeItems(@NonNull ISequence<?> items) {
    return ISequence.of(ObjectUtils.notNull(items.stream()
        // ensures a non-null INodeItem instance
        .map(ItemUtils::checkItemIsNodeItem)
        .map(item -> Axis.ANCESTOR_OR_SELF.execute(ObjectUtils.notNull(item))
            .filter(IDocumentBasedNodeItem.class::isInstance)
            .map(node -> (IDocumentBasedNodeItem) node)
            .findFirst().orElseThrow(() -> new InvalidTreatTypeDynamicMetapathException(
                String.format("The node '%s' is not the descendant of a document node.", item.getMetapath()))))));
  }

  /**
   * Check that the item is the type specified by {@code clazz}.
   *
   * @param <TYPE>
   *          the Java type the item is required to match
   * @param item
   *          the item to check
   * @param clazz
   *          the Java class to check the item against
   * @return the item cast to the required class value
   * @throws TypeMetapathError
   *           if the item is {@code null} or does not match the type specified by
   *           {@code clazz}
   */
  // FIXME: make this a method on the type implementation
  @SuppressWarnings("unchecked")
  @NonNull
  public static <TYPE> TYPE checkItemType(@NonNull IItem item, @NonNull Class<TYPE> clazz) {
    if (clazz.isInstance(item)) {
      return (TYPE) item;
    }
    throw new TypeMetapathError(TypeMetapathError.INVALID_TYPE_ERROR,
        String.format(
            "The item of type '%s' is not the required type '%s'.",
            item.getClass().getName(),
            clazz.getName()));
  }
}
