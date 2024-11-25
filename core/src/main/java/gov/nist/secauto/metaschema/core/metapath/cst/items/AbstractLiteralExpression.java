/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.items;

import gov.nist.secauto.metaschema.core.metapath.cst.AbstractExpression;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractLiteralExpression<RESULT_TYPE extends IAnyAtomicItem, VALUE>
    extends AbstractExpression
    implements ILiteralExpression<RESULT_TYPE, VALUE> {
  @NonNull
  private final VALUE value;

  /**
   * Construct an expression that always returns the same literal value.
   *
   * @param value
   *          the literal value
   */
  public AbstractLiteralExpression(@NonNull VALUE value) {
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
