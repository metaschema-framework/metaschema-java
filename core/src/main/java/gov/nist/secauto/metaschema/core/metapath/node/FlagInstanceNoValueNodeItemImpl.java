
package gov.nist.secauto.metaschema.core.metapath.node;

import gov.nist.secauto.metaschema.core.model.IFlagInstance;

import edu.umd.cs.findbugs.annotations.NonNull;

class FlagInstanceNoValueNodeItemImpl
    extends AbstractFlagInstanceNodeItem
    implements IFeatureNoDataAtomicValuedItem, IFeatureChildNodeItem {

  public FlagInstanceNoValueNodeItemImpl(
      @NonNull IFlagInstance instance,
      @NonNull IModelNodeItem<?, ?> parent) {
    super(instance, parent);
  }

  @Override
  public String stringValue() {
    return "";
  }
}
