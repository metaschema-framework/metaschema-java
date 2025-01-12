/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.items;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.cst.AbstractExpression;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A base class for all Metapath
 * <a href="https://www.w3.org/TR/xpath-31/#id-literals">literal value
 * expressions</a>.
 *
 * @param <RESULT_TYPE>
 *          the Java type of the literal result
 */
public abstract class AbstractLiteralExpression<RESULT_TYPE extends IAnyAtomicItem>
    extends AbstractExpression
    implements ILiteralExpression<RESULT_TYPE> {
  @NonNull
  private final RESULT_TYPE value;

  /**
   * Construct an expression that always returns the same literal value.
   *
   * @param text
   *          the parsed text of the expression
   * @param value
   *          the literal value
   */
  public AbstractLiteralExpression(@NonNull String text, @NonNull RESULT_TYPE value) {
    super(text);
    this.value = value;
  }

  @Override
  public RESULT_TYPE getValue() {
    return value;
  }

  @Override
  protected ISequence<RESULT_TYPE> evaluate(DynamicContext dynamicContext, ISequence<?> focus) {
    return ISequence.of(getValue());
  }

  @SuppressWarnings("null")
  @Override
  public String toCSTString() {
    return String.format("%s[value=%s]", getClass().getName(), getValue().asString());
  }
}
