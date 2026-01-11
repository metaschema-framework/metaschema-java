/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.cst.items;

import java.util.List;

import dev.metaschema.core.metapath.IExpression;
import dev.metaschema.core.metapath.cst.IExpressionVisitor;
import dev.metaschema.core.metapath.item.IItem;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * The CST node for a Metapath
 * <a href="https://www.w3.org/TR/xpath-31/#combining_seq">intersect
 * expression</a>.
 */
public class Intersect
    extends AbstractFilterExpression {

  /**
   * Construct a new Metapath intersect expression CST node.
   *
   * @param text
   *          the parsed text of the expression
   * @param left
   *          an expression indicating the items to filter
   * @param right
   *          an expression indicating the items to keep
   */
  public Intersect(
      @NonNull String text,
      @NonNull IExpression left,
      @NonNull IExpression right) {
    super(text, left, right);
  }

  @Override
  protected ISequence<?> applyFilterTo(@NonNull List<? extends IItem> source, @NonNull List<? extends IItem> items) {
    return ISequence.of(ObjectUtils.notNull(source.stream()
        .distinct()
        .filter(items::contains)));
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(@NonNull IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitIntersect(this, context);
  }
}
