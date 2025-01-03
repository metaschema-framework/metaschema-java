/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.items;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IIntegerItem;

import java.math.BigInteger;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of the
 * <a href="https://www.w3.org/TR/xpath-31/#id-literals">Integer Literal
 * Expression</a> supporting the creation of a Metapath constant literal
 * {@link IIntegerItem}.
 */
public class IntegerLiteral
    extends AbstractLiteralExpression<IIntegerItem, BigInteger> {

  /**
   * Construct a new expression that always returns the same integer value.
   *
   * @param text
   *          the parsed text of the expression
   * @param value
   *          the literal value
   */
  public IntegerLiteral(@NonNull String text, @NonNull BigInteger value) {
    super(text, value);
  }

  @Override
  public Class<IIntegerItem> getBaseResultType() {
    return IIntegerItem.class;
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitIntegerLiteral(this, context);
  }

  @Override
  public ISequence<? extends IIntegerItem> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    return ISequence.of(IIntegerItem.valueOf(getValue()));
  }
}
