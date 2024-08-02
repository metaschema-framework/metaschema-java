
package gov.nist.secauto.metaschema.core.metapath.item.node;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A marker interface used to identify an {@link IAssemblyNodeItem} as the head
 * of a cycle of item that loop back to the head.
 * <p>
 * This is needed to prevent infinite recursion when searching
 * {@link IModuleNodeItem} graphs.
 */
public interface ICycledAssemblyNodeItem extends IAssemblyNodeItem {
  /**
   * Get the assembly item at the head of the cycle.
   *
   * @return the assembly item at the head of the cycle
   */
  @NonNull
  IAssemblyNodeItem getCycledNodeItem();
}
