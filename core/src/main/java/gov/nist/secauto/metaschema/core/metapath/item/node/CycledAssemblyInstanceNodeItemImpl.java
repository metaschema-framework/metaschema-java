
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstance;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

import java.util.Collection;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

class CycledAssemblyInstanceNodeItemImpl
    extends AbstractInstanceNodeItem<IAssemblyDefinition, IAssemblyInstance, IAssemblyNodeItem>
    implements ICycledAssemblyNodeItem, IFeatureNoDataValuedItem,
    IFeatureChildNodeItem {
  @NonNull
  private final IAssemblyNodeItem cycledNodeItem;

  /**
   * Construct a new assembly node item that represents a loop back to a
   * previously declared item.
   *
   * @param instance
   *          the instance in the parent's model
   * @param parent
   *          the parent containing the instance
   * @param cycledNodeItem
   *          the original node item at the start of the loop
   */
  public CycledAssemblyInstanceNodeItemImpl(
      @NonNull IAssemblyInstance instance,
      @NonNull IAssemblyNodeItem parent,
      @NonNull IAssemblyNodeItem cycledNodeItem) {
    super(instance, parent);
    this.cycledNodeItem = cycledNodeItem;
  }

  @Override
  public IAssemblyNodeItem getCycledNodeItem() {
    return cycledNodeItem;
  }

  @Override
  public Collection<? extends IFlagNodeItem> getFlags() {
    return getCycledNodeItem().getFlags();
  }

  @Override
  public IFlagNodeItem getFlagByName(@NonNull IEnhancedQName name) {
    return getCycledNodeItem().getFlagByName(name);
  }

  @Override
  public Collection<? extends List<? extends IModelNodeItem<?, ?>>> getModelItems() {
    return getCycledNodeItem().getModelItems();
  }

  @Override
  public List<? extends IModelNodeItem<?, ?>> getModelItemsByName(IEnhancedQName name) {
    return getCycledNodeItem().getModelItemsByName(name);
  }

  @Override
  public int getPosition() {
    // always a singleton as a non-valued item
    return 1;
  }

  @Override
  public String stringValue() {
    return "";
  }
}
