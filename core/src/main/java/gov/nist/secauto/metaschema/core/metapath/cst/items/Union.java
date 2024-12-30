/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.items;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.cst.AbstractNAryExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.ExpressionUtils;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * The CST node for a Metapath
 * <a href="https://www.w3.org/TR/xpath-31/#combining_seq">union expression</a>.
 */
public class Union
    extends AbstractNAryExpression {

  @NonNull
  private final Class<? extends IItem> staticResultType;

  /**
   * Create a new expression that gets the union of the results of evaluating the
   * provided {@code expressions}.
   *
   * @param expressions
   *          the expressions to evaluate
   */
  public Union(@NonNull List<IExpression> expressions) {
    super(expressions);
    this.staticResultType = ExpressionUtils.analyzeStaticResultType(IItem.class, expressions);
  }

  @Override
  public Class<? extends IItem> getStaticResultType() {
    return staticResultType;
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitUnion(this, context);
  }

  @Override
  public ISequence<?> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    // now process the union
    @NonNull
    Stream<? extends IItem> retval = ObjectUtils.notNull(getChildren().stream()
        .flatMap(child -> {
          ISequence<?> result = child.accept(
              dynamicContext,
              // ensure the sequence is backed by a list
              focus.reusable());
          return result.stream();
        }).distinct());
    return ISequence.of(retval);
  }
}
