/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.items;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.AbstractExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;

import java.util.Collections;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An XPath 3.1
 * <a href="https://www.w3.org/TR/xpath-31/#id-paren-expressions">empty
 * parenthesized expression</a>.
 *
 * @param <RESULT_TYPE>
 *          the Java type of the literal result
 */
public final class EmptySequence<RESULT_TYPE extends IItem>
    extends AbstractExpression {
  @NonNull
  private static final EmptySequence<?> SINGLETON = new EmptySequence<>();

  /**
   * Get a singleton CST node instance representing an expression that returns an
   * empty sequence.
   * <p>
   * This class implements the singleton pattern and is thread-safe. The singleton
   * instance can be obtained using {@link #instance()}.
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
    super("()");
  }

  @SuppressWarnings("null")
  @Override
  public List<? extends IExpression> getChildren() {
    // no children
    return Collections.emptyList();
  }

  @Override
  protected ISequence<?> evaluate(DynamicContext dynamicContext, ISequence<?> focus) {
    return ISequence.empty();
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitEmptySequence(this, context);
  }
}
