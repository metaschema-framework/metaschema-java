/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.path;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.DynamicMetapathException;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;

import java.util.Collections;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of the
 * <a href="https://www.w3.org/TR/xpath-31/#id-context-item-expression">Context
 * Item Expression</a> based on the current focus of the Metapath
 * {@link DynamicContext}.
 */
public final class ContextItem
    extends AbstractPathExpression<INodeItem> {
  @NonNull
  private static final ContextItem SINGLETON = new ContextItem();

  /**
   * Get the singleton context item CST node.
   *
   * @return the singleton instance
   */
  @SuppressWarnings("PMD.AvoidSynchronizedAtMethodLevel")
  @NonNull
  public static synchronized ContextItem instance() {
    return SINGLETON;
  }

  private ContextItem() {
    // disable construction
  }

  @Override
  public Class<INodeItem> getBaseResultType() {
    return INodeItem.class;
  }

  @Override
  public Class<? extends INodeItem> getStaticResultType() {
    return getBaseResultType();
  }

  @SuppressWarnings("null")
  @Override
  public List<? extends IExpression> getChildren() {
    return Collections.emptyList();
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitContextItem(this, context);
  }

  @Override
  public ISequence<?> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    if (focus.isEmpty()) {
      throw new DynamicMetapathException(DynamicMetapathException.DYNAMIC_CONTEXT_ABSENT, "The context is empty");
    }
    return focus;
  }
}
