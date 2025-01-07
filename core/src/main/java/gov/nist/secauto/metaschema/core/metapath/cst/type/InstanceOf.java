/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.type;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.AbstractExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBooleanItem;
import gov.nist.secauto.metaschema.core.metapath.type.ISequenceType;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 *
 */
/**
 * A compact syntax tree node that supports the Metapath
 * <a href="https://www.w3.org/TR/xpath-31/#id-instance-of">"instance of" operator</a>.
 */
public class InstanceOf
    extends AbstractExpression {
  @NonNull
  private final IExpression value;
  @NonNull
  private final ISequenceType sequenceType;

  /**
   * Construct a new instance of expression.
   *
   * @param text
   *          the parsed text of the expression
   * @param value
   *          the expression that will produce the item to test
   * @param sequenceType
   *          the sequence type to test with
   */
  public InstanceOf(
      @NonNull String text,
      @NonNull IExpression value,
      @NonNull ISequenceType sequenceType) {
    super(text);
    this.value = value;
    this.sequenceType = sequenceType;
  }

  @Override
  public Class<? extends IItem> getBaseResultType() {
    return IBooleanItem.class;
  }

  /**
   * Get the expression that will produce the item to test.
   *
   * @return the expression
   */
  @NonNull
  public IExpression getValue() {
    return value;
  }

  /**
   * Get the sequence type to test with.
   *
   * @return the sequence type
   */
  @NonNull
  public ISequenceType getSequenceType() {
    return sequenceType;
  }

  @Override
  public List<? extends IExpression> getChildren() {
    return ObjectUtils.notNull(List.of(value));
  }

  @Override
  protected ISequence<IBooleanItem> evaluate(DynamicContext dynamicContext, ISequence<?> focus) {
    return ISequence.of(IBooleanItem.valueOf(sequenceType.matches(getValue().accept(dynamicContext, focus))));
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitInstanceOf(this, context);
  }

  @Override
  public String toCSTString() {
    return ObjectUtils.notNull(String.format("%s[sequenceType=%s]",
        getClass().getName(),
        getSequenceType().toSignature()));
  }

}
