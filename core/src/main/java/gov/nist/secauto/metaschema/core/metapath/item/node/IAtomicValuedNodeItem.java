
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAtomicValuedItem;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IAtomicValuedNodeItem extends IAtomicValuedItem, INodeItem {
  @NonNull
  IAtomicOrUnionType<?> getValueItemType();

  @Override
  default String stringValue() {
    return toAtomicItem().asString();
  }
}
