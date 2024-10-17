/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.path;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.cst.AbstractExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IPathExpression;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ItemUtils;
import gov.nist.secauto.metaschema.core.metapath.item.node.ICycledAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractPathExpression<RESULT_TYPE extends IItem>
    extends AbstractExpression
    implements IPathExpression<RESULT_TYPE> {
  @Override
  public abstract Class<RESULT_TYPE> getBaseResultType();

  @Override
  public Class<? extends RESULT_TYPE> getStaticResultType() {
    return getBaseResultType();
  }

  /**
   * Evaluate the {@code nodeContext} and its ancestors against the provided
   * {@code expression}, keeping any matching nodes.
   *
   * @param expression
   *          the expression to evaluate
   * @param dynamicContext
   *          the evaluation context
   * @param outerFocus
   *          the current context node
   * @return the matching nodes
   */
  @NonNull
  protected Stream<? extends INodeItem> searchExpression(
      @NonNull IExpression expression,
      @NonNull DynamicContext dynamicContext,
      @NonNull ISequence<?> outerFocus) {
    // ensure the sequence is backed by a list
    outerFocus.getValue();

    // check the current focus
    @SuppressWarnings("unchecked") Stream<? extends INodeItem> nodeMatches
        = (Stream<? extends INodeItem>) expression.accept(dynamicContext, outerFocus).stream();

    Stream<? extends INodeItem> childMatches = outerFocus.stream()
        .map(ItemUtils::checkItemIsNodeItemForStep)
        .flatMap(focusedNode -> {

          Stream<? extends INodeItem> matches;
          if (focusedNode instanceof ICycledAssemblyNodeItem) {
            // prevent stack overflow
            matches = Stream.empty();
          } else {
            assert focusedNode != null; // may be null?
            // create a stream of flags and model elements to check
            Stream<? extends INodeItem> flags = focusedNode.flags();
            Stream<? extends INodeItem> modelItems = focusedNode.modelItems();

            matches = searchExpression(
                expression,
                dynamicContext,
                ISequence.of(ObjectUtils.notNull(Stream.concat(flags, modelItems))));
          }
          return matches;
        });

    return ObjectUtils.notNull(Stream.concat(nodeMatches, childMatches).distinct());
  }

  /**
   * Evaluate the {@code nodeContext} and its ancestors against the provided
   * {@code expression}, keeping any matching nodes.
   *
   * @param expression
   *          the expression to evaluate
   * @param dynamicContext
   *          the evaluation context
   * @param focus
   *          the current context node
   * @return the matching nodes
   */
  @NonNull
  protected Stream<? extends INodeItem> search(
      @NonNull IExpression expression,
      @NonNull DynamicContext dynamicContext,
      @NonNull ISequence<?> focus) {
    // Stream<? extends INodeItem> retval;
    // if (expr instanceof Flag) {
    // // check instances as a flag
    // retval = searchFlags((Flag) expr, context);
    // } else if (expr instanceof ModelInstance) {
    // // check instances as a ModelInstance
    // retval = searchModelInstances((ModelInstance) expr, context);
    // } else {
    // recurse tree
    // searchExpression(expr, context);
    // retval = searchExpression(expr, dynamicContext, context);
    // }
    // return retval;
    return searchExpression(expression, dynamicContext, focus);

  }

  // /**
  // * Recursively searches the node graph for {@link IRequiredValueModelNodeItem}
  // instances that
  // match
  // * the provided {@link ModelInstance} expression. The resulting nodes are
  // returned in document
  // * order.
  // *
  // * @param modelInstance
  // * the search expression
  // * @param context
  // * the current node context
  // * @return a stream of matching model node items
  // */
  // @NonNull
  // protected Stream<? extends IModelNodeItem> searchModelInstances(@NonNull
  // ModelInstance
  // modelInstance,
  // @NonNull IFocus context) {
  //
  // // check if the current node context matches the expression
  // Stream<? extends IModelNodeItem> nodeMatches =
  // matchModelInstance(modelInstance, context);
  //
  // // next iterate over the child model instances, if the context item is an
  // assembly
  // @SuppressWarnings("null")
  // Stream<? extends IModelNodeItem> childMatches
  // = context.modelItems().flatMap(modelItem -> {
  // // apply the search criteria to these node items
  // return searchModelInstances(modelInstance, modelItem);
  // });
  //
  // // combine the results
  // @SuppressWarnings("null")
  // @NonNull
  // Stream<? extends IModelNodeItem> retval = Stream.concat(nodeMatches,
  // childMatches);
  // return retval;
  // }

  // /**
  // * Recursively searches the node graph for {@link IRequiredValueFlagNodeItem}
  // instances that match
  // the provided
  // * {@link Flag} expression. The resulting nodes are returned in document
  // order.
  // *
  // * @param expr
  // * the search expression
  // * @param context
  // * the current node context
  // * @return a stream of matching flag node items
  // */
  // @NonNull
  // private Stream<? extends IRequiredValueFlagNodeItem> searchFlags(Flag expr,
  // IFocus context)
  // {
  //
  // // check if any flags on the the current node context matches the expression
  // Stream<? extends IRequiredValueFlagNodeItem> retval =
  // context.getMatchingChildFlags(expr);
  //
  // // next iterate over the child model instances, if the context item is an
  // assembly
  // INodeItem contextItem = context.getContextNodeItem();
  //
  // if (contextItem instanceof IRequiredValueAssemblyNodeItem) {
  // IRequiredValueAssemblyNodeItem assemblyContextItem =
  // (IRequiredValueAssemblyNodeItem)
  // contextItem;
  //
  // Stream<? extends IRequiredValueFlagNodeItem> childFlagInstances =
  // assemblyContextItem.modelItems().flatMap(modelItem -> {
  // // apply the search criteria to these node items
  // return searchFlags(expr, modelItem);
  // });
  // retval = Stream.concat(retval, childFlagInstances);
  // }
  // return retval;
  // return Stream.empty();
  // }
}
