
package gov.nist.secauto.metaschema.core.metapath.node;

import gov.nist.secauto.metaschema.core.metapath.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.atomic.IAtomicValuedItem;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidTypeFunctionException;

import edu.umd.cs.findbugs.annotations.Nullable;

public interface IFeatureNoDataAtomicValuedItem extends IFeatureNoDataValuedItem, IAtomicValuedItem {
  @Override
  @Nullable
  default IAnyAtomicItem toAtomicItem() {
    throw new InvalidTypeFunctionException(InvalidTypeFunctionException.DATA_ITEM_IS_FUNCTION, this);
  }
}
