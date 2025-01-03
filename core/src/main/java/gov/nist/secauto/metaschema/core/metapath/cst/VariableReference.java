/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * The CST node for a Metapath
 * <a href="https://www.w3.org/TR/xpath-31/#id-variables">variable
 * reference</a>.
 */
public class VariableReference
    extends AbstractExpression {
  @NonNull
  private final IEnhancedQName name;

  /**
   * Construct a new Metapath variable reference CST node.
   *
   * @param text
   *          the parsed text of the expression
   * @param name
   *          the variable name
   */
  public VariableReference(
      @NonNull String text,
      @NonNull IEnhancedQName name) {
    super(text);
    this.name = name;
  }

  /**
   * Get the variable name.
   *
   * @return the variable name
   */
  @NonNull
  public IEnhancedQName getName() {
    return name;
  }

  @Override
  public List<? extends IExpression> getChildren() {
    return CollectionUtil.emptyList();
  }

  @SuppressWarnings("null")
  @Override
  public String toASTString() {
    return String.format("%s[name=%s]", getClass().getName(), getName());
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitVariableReference(this, context);
  }

  @Override
  public ISequence<? extends IItem> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    return dynamicContext.getVariableValue(getName());
  }
}
