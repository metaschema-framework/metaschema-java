
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.model.IDefinition;
import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.core.model.INamedModelInstance;

/**
 * A mixin interface used to identify that the implementation is a
 * {@link IModelNodeItem} that is based on a {@link IDefinition} that is an
 * orphan in it's hierarchy. As a result, this item has no other siblings, since
 * definitions cannot be instantiated.
 * <p>
 * This interface inherits the traits of the
 * {@link IFeatureOrhpanedDefinitionNodeItem} interface.
 *
 * @param <D>
 *          the definition's type
 * @param <I>
 *          the type of the instance that could be created from the definition
 */
interface IFeatureOrhpanedDefinitionModelNodeItem<D extends IModelDefinition, I extends INamedModelInstance>
    extends IModelNodeItem<D, I>, IFeatureOrhpanedDefinitionNodeItem<D, I> {

  @Override
  default int getPosition() {
    // always a singleton as a global definition
    return 1;
  }

  @Override
  default IAssemblyNodeItem getParentContentNodeItem() {
    // never has parent content as definition only
    return null;
  }
}
