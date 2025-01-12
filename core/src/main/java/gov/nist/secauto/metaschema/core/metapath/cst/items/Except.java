/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.items;

import gov.nist.secauto.metaschema.core.metapath.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * The CST node for a Metapath
 * <a href="https://www.w3.org/TR/xpath-31/#combining_seq">except
 * expression</a>.
 */
public class Except
    extends AbstractFilterExpression {

  /**
   * Construct a except filter expression, which removes the items resulting from
   * the filter expression from the items expression.
   *
   * @param text
   *          the parsed text of the expression
   * @param itemsExpression
   *          an expression indicating the items to filter
   * @param filterExpression
   *          an expression indicating the items to omit
   */
  public Except(
      @NonNull String text,
      @NonNull IExpression itemsExpression,
      @NonNull IExpression filterExpression) {
    super(text, itemsExpression, filterExpression);
  }

  @Override
  protected ISequence<?> applyFilterTo(@NonNull List<? extends IItem> source, @NonNull List<? extends IItem> items) {
    Set<IItem> filterSet = new HashSet<>(items);
    return ISequence.of(ObjectUtils.notNull(source.stream()
        .filter(item -> !filterSet.contains(item))));
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(@NonNull IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitExcept(this, context);
  }
}
