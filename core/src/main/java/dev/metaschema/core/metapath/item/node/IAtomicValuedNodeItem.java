
package dev.metaschema.core.metapath.item.node;

import dev.metaschema.core.metapath.item.atomic.IAtomicValuedItem;
import dev.metaschema.core.metapath.type.IAtomicOrUnionType;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a Metapath node item that has an atomic value.
 */
public interface IAtomicValuedNodeItem extends IAtomicValuedItem, INodeItem {
  /**
   * Get the item type of the item's value.
   *
   * @return the item type
   */
  @NonNull
  IAtomicOrUnionType<?> getValueItemType();

  @Override
  default String stringValue() {
    return toAtomicItem().asString();
  }
}
