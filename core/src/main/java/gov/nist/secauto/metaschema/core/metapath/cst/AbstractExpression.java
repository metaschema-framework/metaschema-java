/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

/**
 * A common base class for Metapath expression implementations, providing common
 * utility functions.
 */
public abstract class AbstractExpression implements IExpression {
  @Override
  public String toString() {
    return CSTPrinter.toString(this);
  }
}
