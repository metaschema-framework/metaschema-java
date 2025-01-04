/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.items;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.AbstractNAryExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.ExpressionUtils;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of the XPath 3.1
 * <a href="https://www.w3.org/TR/xpath-31/#doc-xpath31-Expr">sequence
 * expression</a>.
 */
public class SequenceExpression
    extends AbstractNAryExpression {

  @NonNull
  private final Class<? extends IItem> staticResultType;

  /**
   * Create a new internal metapath expression.
   *
   * @param text
   *          the parsed text of the expression
   * @param expressions
   *          the expressions to evaluate
   */
  public SequenceExpression(@NonNull String text, @NonNull List<IExpression> expressions) {
    super(text, expressions);
    this.staticResultType = ExpressionUtils.analyzeStaticResultType(IItem.class, expressions);
  }

  @Override
  public Class<? extends IItem> getStaticResultType() {
    return staticResultType;
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitMetapath(this, context);
  }

  @Override
  protected ISequence<?> evaluate(DynamicContext dynamicContext, ISequence<?> focus) {
    Stream<? extends IItem> retval = ObjectUtils.notNull(getChildren().stream()
        .flatMap(child -> {
          ISequence<?> result = child.accept(dynamicContext, focus);
          return result.stream();
        }));
    return ISequence.of(retval);
  }
}
