
package gov.nist.secauto.metaschema.core.metapath.item.node;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides a set of callbacks used when iterating over node items in a directed
 * graph.
 * <p>
 * The {@link AbstractNodeItemVisitor} provides an abstract implementation of
 * this visitor pattern.
 *
 * @param <CONTEXT>
 *          the type of data to pass to each visited node
 * @param <RESULT>
 *          the type of result produced by visitation
 * @see AbstractNodeItemVisitor
 */
public interface INodeItemVisitor<CONTEXT, RESULT> {
  /**
   * This callback is called when the {@link IDocumentNodeItem} is visited.
   *
   * @param item
   *          the visited item
   * @param context
   *          provides contextual information for use by the visitor
   * @return the visitation result
   */
  RESULT visitDocument(@NonNull IDocumentNodeItem item, CONTEXT context);

  /**
   * This callback is called when an {@link IFlagNodeItem} is visited.
   *
   * @param item
   *          the visited item
   * @param context
   *          provides contextual information for use by the visitor
   * @return the visitation result
   */
  RESULT visitFlag(@NonNull IFlagNodeItem item, CONTEXT context);

  /**
   * This callback is called when an {@link IFieldNodeItem} is visited.
   *
   * @param item
   *          the visited item
   * @param context
   *          provides contextual information for use by the visitor
   * @return the visitation result
   */
  RESULT visitField(@NonNull IFieldNodeItem item, CONTEXT context);

  /**
   * This callback is called when an {@link IAssemblyNodeItem} is visited.
   *
   * @param item
   *          the visited item
   * @param context
   *          provides contextual information for use by the visitor
   * @return the visitation result
   */
  RESULT visitAssembly(@NonNull IAssemblyNodeItem item, CONTEXT context);

  /**
   * This callback is called when an {@link IAssemblyInstanceGroupedNodeItem} is
   * visited.
   *
   * @param item
   *          the visited item
   * @param context
   *          provides contextual information for use by the visitor
   * @return the visitation result
   */
  RESULT visitAssembly(@NonNull IAssemblyInstanceGroupedNodeItem item, CONTEXT context);

  /**
   * This callback is called when an {@link IModuleNodeItem} is visited.
   *
   * @param item
   *          the visited item
   * @param context
   *          provides contextual information for use by the visitor
   * @return the visitation result
   */
  RESULT visitMetaschema(@NonNull IModuleNodeItem item, CONTEXT context);
}
