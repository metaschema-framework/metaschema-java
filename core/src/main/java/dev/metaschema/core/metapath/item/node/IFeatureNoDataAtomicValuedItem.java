
package dev.metaschema.core.metapath.item.node;

import dev.metaschema.core.metapath.function.InvalidTypeFunctionException;
import dev.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import dev.metaschema.core.metapath.item.atomic.IAtomicValuedItem;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * A feature interface representing an atomic-valued item that has no associated
 * value data.
 */
public interface IFeatureNoDataAtomicValuedItem extends IFeatureNoDataValuedItem, IAtomicValuedItem {
  @Override
  @Nullable
  default IAnyAtomicItem toAtomicItem() {
    throw new InvalidTypeFunctionException(InvalidTypeFunctionException.DATA_ITEM_IS_FUNCTION, this);
  }
}
