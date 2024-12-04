
package gov.nist.secauto.metaschema.core.metapath.node;

import gov.nist.secauto.metaschema.core.metapath.atomic.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.atomic.IAtomicValuedItem;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IAtomicValuedNodeItem extends IAtomicValuedItem, INodeItem {
  @NonNull
  IAtomicOrUnionType<?> getValueItemType();

  @Override
  default String stringValue() {
    return toAtomicItem().asString();
  }
}
