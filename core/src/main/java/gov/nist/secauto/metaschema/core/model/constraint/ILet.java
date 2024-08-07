/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.metapath.MetapathException;
import gov.nist.secauto.metaschema.core.metapath.MetapathExpression;
import gov.nist.secauto.metaschema.core.model.constraint.impl.DefaultLet;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

@SuppressWarnings("PMD.ShortClassName")
public interface ILet {
  /**
   * Create a new Let expression by compiling the provided Metapath expression
   * string.
   *
   * @param name
   *          the let expression variable name
   * @param valueExpression
   *          a Metapath expression string representing the variable value
   * @param source
   *          the source descriptor for the resource containing the constraint
   * @return the original let statement with the same name or {@code null}
   */
  @SuppressWarnings("PMD.ShortMethodName")
  @NonNull
  static ILet of(
      @NonNull QName name,
      @NonNull String valueExpression,
      @NonNull ISource source) {
    try {
      return of(name, MetapathExpression.compile(valueExpression, source.getStaticContext()), source);
    } catch (MetapathException ex) {
      throw new MetapathException(
          String.format("Unable to compile the let expression '%s=%s'%s. %s",
              name,
              valueExpression,
              source.getSource() == null ? "" : " in " + source.getSource(),
              ex.getMessage()),
          ex);
    }
  }

  /**
   * Create a new Let expression.
   *
   * @param name
   *          the let expression variable name
   * @param valueExpression
   *          a Metapath expression representing the variable value
   * @param source
   *          the source descriptor for the resource containing the constraint
   * @return the original let statement with the same name or {@code null}
   */
  @SuppressWarnings("PMD.ShortMethodName")
  @NonNull
  static ILet of(
      @NonNull QName name,
      @NonNull MetapathExpression valueExpression,
      @NonNull ISource source) {
    return new DefaultLet(name, valueExpression, source);
  }

  /**
   * Get the name of the let variable.
   *
   * @return the name
   */
  @NonNull
  QName getName();

  /**
   * Get the Metapath expression to use to query the value.
   *
   * @return the Metapath expression to use to query the value
   */
  @NonNull
  MetapathExpression getValueExpression();

  /**
   * Information about the source resource containing the let statement.
   *
   * @return the source information
   */
  @NonNull
  ISource getSource();
}
