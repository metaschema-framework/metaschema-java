
package dev.metaschema.core.metapath.item.node;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Marks a node item as visitable by a {@link INodeItemVisitor}.
 */
@FunctionalInterface
public interface INodeItemVisitable {
  /**
   * A visitor callback.
   *
   * @param <CONTEXT>
   *          the type of the context parameter
   * @param <RESULT>
   *          the type of the visitor result
   * @param visitor
   *          the calling visitor
   * @param context
   *          a parameter used to pass contextual information between visitors
   * @return the visitor result
   */
  <CONTEXT, RESULT> RESULT accept(
      @NonNull INodeItemVisitor<CONTEXT, RESULT> visitor,
      CONTEXT context);
}
