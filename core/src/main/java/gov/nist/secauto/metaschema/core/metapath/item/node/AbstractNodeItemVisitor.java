
package gov.nist.secauto.metaschema.core.metapath.item.node;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Used by implementations of this class to visit a sequence of node items in a
 * directed graph, using depth-first ordering.
 *
 * @param <CONTEXT>
 *          the type of data to pass to each visited node
 * @param <RESULT>
 *          the type of result produced by visitation
 */
public abstract class AbstractNodeItemVisitor<CONTEXT, RESULT> implements INodeItemVisitor<CONTEXT, RESULT> {
  /**
   * Visit the provided {@code item}.
   *
   * @param item
   *          the item to visit
   * @param context
   *          provides contextual information for use by the visitor
   * @return the result produced by visiting the item
   */
  public final RESULT visit(@NonNull INodeItemVisitable item, CONTEXT context) {
    return item.accept(this, context);
  }

  /**
   * Visit any child flags associated with the provided {@code item}.
   *
   * @param item
   *          the item to visit
   * @param context
   *          provides contextual information for use by the visitor
   * @return the result produced by visiting the item's flags
   */
  protected RESULT visitFlags(@NonNull INodeItem item, CONTEXT context) {
    RESULT result = defaultResult();
    for (IFlagNodeItem flag : item.getFlags()) {
      assert flag != null;
      if (!shouldVisitNextChild(item, flag, result, context)) {
        break;
      }

      RESULT childResult = flag.accept(this, context);
      result = aggregateResult(result, childResult, context);
    }
    return result;
  }

  /**
   * Visit any child model items associated with the provided {@code item}.
   *
   * @param item
   *          the item to visit
   * @param context
   *          provides contextual information for use by the visitor
   * @return the result produced by visiting the item's child model items
   */
  protected RESULT visitModelChildren(@NonNull INodeItem item, CONTEXT context) {
    RESULT result = defaultResult();

    for (List<? extends IModelNodeItem<?, ?>> childItems : item.getModelItems()) {
      for (IModelNodeItem<?, ?> childItem : childItems) {
        assert childItem != null;
        if (!shouldVisitNextChild(item, childItem, result, context)) {
          break;
        }

        RESULT childResult = childItem.accept(this, context);
        result = aggregateResult(result, childResult, context);
      }
    }
    return result;
  }

  /**
   * Determine if the child should be visited next, or skipped.
   *
   * @param parent
   *          the parent of the child to visit next
   * @param child
   *          the next child to visit
   * @param result
   *          the current visitation result
   * @param context
   *          provides contextual information for use by the visitor
   * @return {@code true} if the child should be visited, or {@code false} if the
   *         child should be skipped
   */
  protected boolean shouldVisitNextChild(
      @NonNull INodeItem parent,
      @NonNull INodeItem child,
      RESULT result,
      CONTEXT context) {
    // this is the default behavior, which can be overridden
    return true;
  }

  /**
   * Determine if the child should be visited next, or skipped.
   *
   * @param parent
   *          the parent of the child to visit next
   * @param child
   *          the next child to visit
   * @param result
   *          the current visitation result
   * @param context
   *          provides contextual information for use by the visitor
   * @return {@code true} if the child should be visited, or {@code false} if the
   *         child should be skipped
   */
  protected boolean shouldVisitNextChild(
      @NonNull INodeItem parent,
      @NonNull IModelNodeItem<?, ?> child,
      RESULT result,
      CONTEXT context) {
    // this is the default behavior, which can be overridden
    return true;
  }

  /**
   * The initial, default visitation result, which will be used as the basis for
   * aggregating results produced when visiting.
   *
   * @return the default result
   * @see #aggregateResult(Object, Object, Object)
   */
  protected abstract RESULT defaultResult();

  /**
   * Combine two results into a single, aggregate result.
   *
   * @param first
   *          the original result
   * @param second
   *          the new result to combine with the original result
   * @param context
   *          provides contextual information for use by the visitor
   * @return the combined result
   */
  protected RESULT aggregateResult(RESULT first, RESULT second, CONTEXT context) {
    // this is the default behavior, which can be overridden
    return second;
  }

  @Override
  public RESULT visitDocument(IDocumentNodeItem item, CONTEXT context) {
    // this is the default behavior, which can be overridden
    return visitModelChildren(item, context);
    // return visitAssembly(item.getRootAssemblyNodeItem(), context);
  }

  @Override
  public RESULT visitFlag(IFlagNodeItem item, CONTEXT context) {
    // this is the default behavior, which can be overridden
    return defaultResult();
  }

  @Override
  public RESULT visitField(IFieldNodeItem item, CONTEXT context) {
    // this is the default behavior, which can be overridden
    return visitFlags(item, context);
  }

  @Override
  public RESULT visitAssembly(IAssemblyNodeItem item, CONTEXT context) {
    // this is the default behavior, which can be overridden
    return aggregateResult(visitFlags(item, context), visitModelChildren(item, context), context);
  }

  @Override
  public RESULT visitAssembly(IAssemblyInstanceGroupedNodeItem item, CONTEXT context) {
    // this is the default behavior, which can be overridden
    return aggregateResult(visitFlags(item, context), visitModelChildren(item, context), context);
  }

  @Override
  public RESULT visitMetaschema(IModuleNodeItem item, CONTEXT context) {
    // this is the default behavior, which can be overridden
    return aggregateResult(visitFlags(item, context), visitModelChildren(item, context), context);
  }
}
