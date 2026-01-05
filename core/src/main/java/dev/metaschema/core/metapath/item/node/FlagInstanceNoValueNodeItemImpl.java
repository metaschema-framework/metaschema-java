
package dev.metaschema.core.metapath.item.node;

import dev.metaschema.core.model.IFlagInstance;

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
