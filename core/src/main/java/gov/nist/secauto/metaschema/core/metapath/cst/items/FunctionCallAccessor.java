/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.items;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ICollectionValue;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.StaticMetapathException;
import gov.nist.secauto.metaschema.core.metapath.cst.AnonymousFunctionCall;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.function.library.ArrayGet;
import gov.nist.secauto.metaschema.core.metapath.function.library.MapGet;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IIntegerItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.IArrayItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.IMapItem;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

public class FunctionCallAccessor implements IExpression {
  @NonNull
  private final IExpression base;
  @NonNull
  private final List<IExpression> arguments;

  /**
   * Construct a new functional call accessor.
   *
   * @param base
   *          the expression whose result is used as the map or array to perform
   *          the lookup on
   * @param keyOrIndex
   *          the value to find, which will be the key for a map or the index for
   *          an array
   */
  public FunctionCallAccessor(@NonNull IExpression base, @NonNull List<IExpression> arguments) {
    this.base = base;
    this.arguments = arguments;
  }

  /**
   * Get the base sub-expression.
   *
   * @return the sub-expression
   */
  @NonNull
  public IExpression getBase() {
    return base;
  }

  /**
   * Retrieve the argument to use for the lookup.
   *
   * @return the argument
   */
  @NonNull
  public List<IExpression> getArguments() {
    return arguments;
  }

  @SuppressWarnings("null")
  @Override
  public List<IExpression> getChildren() {
    return Stream.concat(Stream.of(getBase()), getArguments().stream())
        .collect(Collectors.toUnmodifiableList());
  }

  @Override
  public ISequence<? extends IItem> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    ISequence<?> target = getBase().accept(dynamicContext, focus);
    IItem collection = target.getFirstItem(true);

    if (collection instanceof AnonymousFunctionCall) {
      return ((AnonymousFunctionCall) collection).execute(
          getArguments().stream()
              .map(expr -> expr.accept(dynamicContext, focus))
              .collect(Collectors.toUnmodifiableList()),
          dynamicContext,
          focus);
    }

    // the value to find, which will be the key for a map or the index for an array
    IExpression argument = getArguments().stream().findFirst().get();

    IAnyAtomicItem key = ISequence.of(argument.accept(dynamicContext, focus).atomize())
        .getFirstItem(false);
    if (key == null) {
      throw new StaticMetapathException(StaticMetapathException.NO_FUNCTION_MATCH,
          "No key provided for functional call lookup");
    }

    ICollectionValue retval = null;
    if (collection instanceof IArrayItem) {
      retval = ArrayGet.get((IArrayItem<?>) collection, IIntegerItem.cast(key));
    } else if (collection instanceof IMapItem) {
      retval = MapGet.get((IMapItem<?>) collection, key);
    }

    return retval == null ? ISequence.empty() : retval.toSequence();
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(@NonNull IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitFunctionCallAccessor(this, context);
  }
}
