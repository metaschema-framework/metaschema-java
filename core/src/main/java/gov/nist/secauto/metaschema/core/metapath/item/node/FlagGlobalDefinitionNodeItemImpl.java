
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.model.IFlagDefinition;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * a new {@link INodeItem} instance, that is orphaned from any parent nodes,
 * supported by an {@link IFlagDefinition}.
 */
class FlagGlobalDefinitionNodeItemImpl
    extends AbstractGlobalDefinitionNodeItem<IFlagDefinition, IFlagInstance>
    implements IFlagNodeItem, IFeatureNoDataAtomicValuedItem {

  /**
   * Construct a new {@link INodeItem} instance, that is orphaned from any parent
   * nodes, based on the provided flag {@code definition}.
   *
   * @param definition
   *          the flag
   * @param parent
   *          the item for the Metaschema containing this definition
   */
  public FlagGlobalDefinitionNodeItemImpl(
      @NonNull IFlagDefinition definition,
      @NonNull IModuleNodeItem parent) {
    super(definition, parent);
  }

  @Override
  public IFlagInstance getInstance() {
    return null;
  }
}
