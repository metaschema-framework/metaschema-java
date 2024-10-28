/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.function.library.FnConcat;
import gov.nist.secauto.metaschema.core.metapath.function.library.FnData;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

public class StringConcat
    extends AbstractNAryExpression {

  /**
   * Create a new expression that concatenates the results of evaluating the
   * provided {@code expressions} as strings.
   *
   * @param expressions
   *          the expressions to evaluate
   */
  public StringConcat(@NonNull List<IExpression> expressions) {
    super(expressions);
  }

  @Override
  public Class<IStringItem> getBaseResultType() {
    return IStringItem.class;
  }

  @Override
  public Class<IStringItem> getStaticResultType() {
    return getBaseResultType();
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitStringConcat(this, context);
  }

  @Override
  public ISequence<?> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    return ISequence.of(FnConcat.concat(ObjectUtils.notNull(getChildren().stream()
        .map(child -> child.accept(dynamicContext, focus))
        .flatMap(result -> FnData.fnData(ObjectUtils.notNull(result)).stream()))));
  }
}
