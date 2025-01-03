/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A common base class for Metapath expression implementations, providing common
 * utility functions.
 */
public abstract class AbstractExpression implements IExpression {
  @NonNull
  private final String text;

  /**
   * Construct a new expression.
   *
   * @param text
   *          the parsed text of the expression
   */
  public AbstractExpression(@NonNull String text) {
    this.text = text;
  }

  @Override
  public String getText() {
    return text;
  }

  @Override
  public String toString() {
    return CSTPrinter.toString(this);
  }
}
