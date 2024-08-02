
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAtomicValuedItem;

import edu.umd.cs.findbugs.annotations.Nullable;

public interface IFeatureNoDataAtomicValuedItem extends IFeatureNoDataValuedItem, IAtomicValuedItem {

  @Override
  @Nullable
  default IAnyAtomicItem toAtomicItem() {
    // no value
    return null;
  }
}
