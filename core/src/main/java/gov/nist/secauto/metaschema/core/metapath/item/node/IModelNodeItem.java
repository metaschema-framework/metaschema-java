
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.core.model.INamedModelInstance;

public interface IModelNodeItem<D extends IModelDefinition, I extends INamedModelInstance>
    extends IDefinitionNodeItem<D, I> {

  /**
   * Retrieve the relative position of this node relative to sibling nodes.
   * <p>
   * A singleton item in a sequence will have a position value of {@code 1}.
   * <p>
   * The value {@code 1} is used as the starting value to align with the XPath
   * specification.
   *
   * @return a positive integer value designating this instance's position within
   *         a collection
   */
  int getPosition();

  /**
   * {@inheritDoc}
   * <p>
   * The parent can be an assembly or a document (in the case of a root assembly.
   */
  @Override
  INodeItem getParentNodeItem();

  @Override
  IAssemblyNodeItem getParentContentNodeItem();
}
