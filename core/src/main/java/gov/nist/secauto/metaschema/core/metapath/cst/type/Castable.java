/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.type;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.IItem;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.MetapathException;
import gov.nist.secauto.metaschema.core.metapath.atomic.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.atomic.IBooleanItem;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A compact syntax tree node that supports the Metapath
 * <a href="https://www.w3.org/TR/xpath-31/#id-cast">"cast as" operator</a>.
 */
@SuppressWarnings("PMD.ShortClassName")
public class Castable
    extends AbstractCastingExpression {

  /**
   * Construct a new castable expression.
   *
   * @param value
   *          the expression that will produce the item to cast
   * @param type
   *          the atomic type to cast to
   * @param allowEmptySequence
   *          {@code true} if the value expression is allowed to produce an empty
   *          sequence, or {@code false} otherwise
   */
  public Castable(
      @NonNull IExpression value,
      @NonNull IAtomicOrUnionType<?> type,
      boolean allowEmptySequence) {
    super(value, type, allowEmptySequence);
  }

  @Override
  public Class<? extends IItem> getBaseResultType() {
    return IBooleanItem.class;
  }

  @Override
  public ISequence<? extends IItem> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    boolean retval;
    try {
      cast(focus);
      retval = true;
    } catch (MetapathException ex) {
      retval = false;
    }
    return ISequence.of(IBooleanItem.valueOf(retval));
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitCastable(this, context);
  }
}
