/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.impl;

import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.IExpression;
import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.StaticContext;
import dev.metaschema.core.metapath.cst.IExpressionVisitor;
import dev.metaschema.core.metapath.item.IItem;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.util.CollectionUtil;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractMetapathExpression implements IMetapathExpression {

  @NonNull
  private final String path;
  @NonNull
  private final StaticContext staticContext;

  /**
   * Construct a new Metapath expression.
   *
   * @param path
   *          the Metapath expression string
   * @param context
   *          the static context for expression evaluation
   */
  public AbstractMetapathExpression(
      @NonNull String path,
      @NonNull StaticContext context) {
    this.path = path;
    this.staticContext = context;
  }

  @Override
  public String getPath() {
    return path;
  }

  @Override
  public StaticContext getStaticContext() {
    return staticContext;
  }

  /**
   * Get the compiled expression tree for this Metapath expression.
   *
   * @return the root expression node
   */
  @NonNull
  protected abstract IExpression getExpression();

  @Override
  public Class<? extends IItem> getBaseResultType() {
    return getExpression().getStaticResultType();
  }

  @Override
  public List<? extends IExpression> getChildren() {
    return CollectionUtil.singletonList(getExpression());
  }

  @Override
  public ISequence<? extends IItem> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    return getExpression().accept(dynamicContext, focus);
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return getExpression().accept(visitor, context);
  }
}
