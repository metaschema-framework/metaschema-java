/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.impl;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.IExpression;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractMetapathExpression implements IMetapathExpression {

  @NonNull
  private final String path;
  @NonNull
  private final StaticContext staticContext;

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
