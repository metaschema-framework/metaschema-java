
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.model.IFlagDefinition;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A {@link INodeItem} supported by a {@link IFlagInstance}.
 */
abstract class AbstractFlagInstanceNodeItem
    extends AbstractInstanceNodeItem<IFlagDefinition, IFlagInstance, IModelNodeItem<?, ?>>
    implements IFlagNodeItem {

  public AbstractFlagInstanceNodeItem(@NonNull IFlagInstance instance, @NonNull IModelNodeItem<?, ?> parent) {
    super(instance, parent);
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
