/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.items;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.cst.AbstractBinaryExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.util.CustomCollectors;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of the
 * <a href="https://www.w3.org/TR/xpath-31/#id-map-operator">Simple Map Operator
 * <code>!</code></a> supporting evaluating a right expression against every
 * item in a sequence produced by a left expression.
 */
public class SimpleMap
    extends AbstractBinaryExpression<IExpression, IExpression> {

  /**
   * Construct a simple map expression.
   *
   * @param left
   *          the expression used to generate the right sequence
   * @param right
   *          the expression used to evaluate each item in the right sequence
   */
  public SimpleMap(@NonNull IExpression left, @NonNull IExpression right) {
    super(left, right);
  }

  @Override
  public ISequence<?> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    ISequence<?> leftResult = getLeft().accept(dynamicContext, focus);

    IExpression right = getRight();
    return ObjectUtils.notNull(leftResult.stream()
        .flatMap(item -> right.accept(dynamicContext, ISequence.of(item)).stream())
        .collect(CustomCollectors.toSequence()));
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitSimpleMap(this, context);
  }
}
