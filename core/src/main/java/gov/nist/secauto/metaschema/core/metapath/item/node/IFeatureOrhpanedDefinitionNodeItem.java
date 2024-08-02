
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.model.IDefinition;
import gov.nist.secauto.metaschema.core.model.INamedInstance;

/**
 * A mixin interface used to identify that the implementation is a
 * {@link IDefinitionNodeItem} that is based on a {@link IDefinition} that is an
 * orphan in it's hierarchy. As a result, this item has no other siblings, since
 * definitions cannot be instantiated.
 *
 * @param <D>
 *          the definition's type
 * @param <I>
 *          the type of the instance that could be created from the definition
 */
interface IFeatureOrhpanedDefinitionNodeItem<D extends IDefinition, I extends INamedInstance>
    extends IDefinitionNodeItem<D, I> {

  @Override
  default I getInstance() {
    // no instance
    return null;
  }

  @Override
  default IModelNodeItem<?, ?> getParentContentNodeItem() {
    // never has parent content as definition only
    return null;
  }
}
