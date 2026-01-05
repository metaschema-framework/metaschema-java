
package dev.metaschema.core.metapath.item.node;

import dev.metaschema.core.model.IFlagDefinition;
import dev.metaschema.core.model.IFlagInstance;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A {@link INodeItem} supported by a {@link IFlagInstance}.
 */
public abstract class AbstractFlagInstanceNodeItem
    extends AbstractInstanceNodeItem<IFlagDefinition, IFlagInstance, IModelNodeItem<?, ?>>
    implements IFlagNodeItem {

  /**
   * Construct a new flag instance node item.
   *
   * @param instance
   *          the flag instance this node represents
   * @param parent
   *          the parent node item containing this flag
   */
  public AbstractFlagInstanceNodeItem(@NonNull IFlagInstance instance, @NonNull IModelNodeItem<?, ?> parent) {
    super(instance, parent);
  }

  @Override
  protected String getValueSignature() {
    return toAtomicItem().toSignature();
  }
}
