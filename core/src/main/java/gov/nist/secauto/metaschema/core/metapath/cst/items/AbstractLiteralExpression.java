/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.items;

import gov.nist.secauto.metaschema.core.metapath.cst.AbstractExpression;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A base class for all Metapath
 * <a href="https://www.w3.org/TR/xpath-31/#id-literals">literal value
 * expressions</a>.
 *
 * @param <RESULT_TYPE>
 *          the Java type of the literal result
 * @param <VALUE>
 *          the Java type of the wrapped literal values
 */
public abstract class AbstractLiteralExpression<RESULT_TYPE extends IAnyAtomicItem, VALUE>
    extends AbstractExpression
    implements ILiteralExpression<RESULT_TYPE, VALUE> {
  @NonNull
  private final VALUE value;

  /**
   * Construct an expression that always returns the same literal value.
   *
   * @param text
   *          the parsed text of the expression
   * @param value
   *          the literal value
   */
  public AbstractLiteralExpression(@NonNull String text, @NonNull VALUE value) {
    super(text);
    this.value = value;
  }

  @Override
  public VALUE getValue() {
    return value;
  }

  @SuppressWarnings("null")
  @Override
  public String toASTString() {
    return String.format("%s[value=%s]", getClass().getName(), getValue().toString());
  }
}
