
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.model.IFlagDefinition;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A {@link INodeItem} supported by a {@link IFlagInstance}.
 */
abstract class AbstractFlagInstanceNodeItem
    implements IFlagNodeItem {
  @NonNull
  private final IFlagInstance instance;
  @NonNull
  private final IModelNodeItem<?, ?> parent;

  public AbstractFlagInstanceNodeItem(@NonNull IFlagInstance instance, @NonNull IModelNodeItem<?, ?> parent) {
    this.instance = instance;
    this.parent = parent;
  }

  @Override
  public IFlagDefinition getDefinition() {
    return getInstance().getDefinition();
  }

  @Override
  @NonNull
  public IFlagInstance getInstance() {
    return instance;
  }

  @Override
  @NonNull
  public IModelNodeItem<?, ?> getParentContentNodeItem() {
    return getParentNodeItem();
  }

  @Override
  @NonNull
  public IModelNodeItem<?, ?> getParentNodeItem() {
    return parent;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder()
        .append(getInstance().getXmlQName().toString());
    IAnyAtomicItem value = toAtomicItem();
    if (value != null) {
      builder
          .append('(')
          .append(value.asString())
          .append(')');
    }
    return builder.toString();
  }
}
