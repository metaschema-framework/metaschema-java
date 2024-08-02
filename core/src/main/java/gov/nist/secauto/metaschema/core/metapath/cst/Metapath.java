/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

public class Metapath
    extends AbstractNAryExpression {

  @NonNull
  private final Class<? extends IItem> staticResultType;

  /**
   * Create a new internal metapath expression.
   *
   * @param expressions
   *          the expressions to evaluate
   */
  public Metapath(@NonNull List<IExpression> expressions) {
    super(expressions);
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
  public ISequence<?> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    Stream<? extends IItem> retval = ObjectUtils.notNull(getChildren().stream()
        .flatMap(child -> {
          ISequence<?> result = child.accept(dynamicContext, focus);
          return result.stream();
        }));
    return ISequence.of(retval);
  }
}
