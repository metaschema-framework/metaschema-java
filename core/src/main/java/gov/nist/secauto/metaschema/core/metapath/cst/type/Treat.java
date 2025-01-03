/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.type;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.DynamicMetapathException;
import gov.nist.secauto.metaschema.core.metapath.cst.AbstractExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.type.ISequenceType;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A compact syntax tree node that supports the Metapath
 * <a href="https://www.w3.org/TR/xpath-31/#id-cast">"cast as" operator</a>.
 */
@SuppressWarnings("PMD.ShortClassName")
public class Treat
    extends AbstractExpression {
  @NonNull
  private final IExpression value;
  @NonNull
  private final ISequenceType type;

  /**
   * Construct a new cast expression.
   *
   * @param text
   *          the parsed text of the expression
   * @param value
   *          the expression that will produce the item to cast
   * @param type
   *          the sequence type to treat the expression type as
   */
  public Treat(
      @NonNull String text,
      @NonNull IExpression value,
      @NonNull ISequenceType type) {
    super(text);
    this.value = value;
    this.type = type;
  }

  @Override
  public Class<? extends IItem> getBaseResultType() {
    return type.getType().getItemClass();
  }

  @Override
  public List<? extends IExpression> getChildren() {
    return ObjectUtils.notNull(List.of(value));
  }

  @Override
  public ISequence<? extends IItem> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    ISequence<?> retval = value.accept(dynamicContext, focus);
    if (!type.matches(retval)) {
      throw new DynamicMetapathException(
          DynamicMetapathException.TREAT_DOES_NOT_MATCH_TYPE,
          String.format("The sequence '%s' does not match the sequence type '%s'.", retval, type.toSignature()));
    }
    return retval;
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitTreat(this, context);
  }

  @Override
  public String toASTString() {
    return ObjectUtils.notNull(String.format("%s[type=%s]",
        getClass().getName(),
        type.toSignature()));
  }
}
