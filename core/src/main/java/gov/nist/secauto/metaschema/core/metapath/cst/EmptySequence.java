/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;

import java.util.Collections;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

public final class EmptySequence<RESULT_TYPE extends IItem>
    extends AbstractExpression {
  @NonNull
  private static final EmptySequence<?> SINGLETON = new EmptySequence<>();

  /**
   * Get a singleton CST node instance representing an expression that returns an
   * empty sequence.
   *
   * @param <T>
   *          the Java type of the resulting empty sequence
   * @return the singleton CST node instance
   */
  @SuppressWarnings({ "unchecked", "PMD.AvoidSynchronizedAtMethodLevel" })
  @NonNull
  public static synchronized <T extends IItem> EmptySequence<T> instance() {
    return (EmptySequence<T>) SINGLETON;
  }

  private EmptySequence() {
    // disable construction
  }

  @SuppressWarnings("null")
  @Override
  public List<? extends IExpression> getChildren() {
    // no children
    return Collections.emptyList();
  }

  @Override
  public ISequence<RESULT_TYPE> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    return ISequence.empty();
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitEmptySequence(this, context);
  }

}
