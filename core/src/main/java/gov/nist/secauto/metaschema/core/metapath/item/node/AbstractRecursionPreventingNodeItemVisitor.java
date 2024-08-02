
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;

public abstract class AbstractRecursionPreventingNodeItemVisitor<CONTEXT, RESULT>
    extends AbstractNodeItemVisitor<CONTEXT, RESULT> {

  @Override
  public RESULT visitAssembly(IAssemblyNodeItem item, CONTEXT context) {
    // only walk new records to avoid looping
    // check if this item's definition is the same as an ancestor's
    return isDecendant(item, item.getDefinition())
        ? defaultResult()
        : super.visitAssembly(item, context);
  }

  /**
   * Determines if the provided node is a descendant of the assembly definition.
   *
   * @param node
   *          the node item to test
   * @param assemblyDefinition
   *          the assembly definition to determine as an ancestor of the node
   * @return {@code true} if the assembly definition is the node's ancestor, or
   *         {@code false} otherwise
   */
  protected boolean isDecendant(IAssemblyNodeItem node, IAssemblyDefinition assemblyDefinition) {
    return node.ancestor()
        .map(ancestor -> {
          boolean retval = false;
          if (ancestor instanceof IAssemblyNodeItem) {
            IAssemblyDefinition ancestorDef = ((IAssemblyNodeItem) ancestor).getDefinition();
            retval = ancestorDef.equals(assemblyDefinition);
          }
          return retval;
        }).anyMatch(value -> value);
  }
}
