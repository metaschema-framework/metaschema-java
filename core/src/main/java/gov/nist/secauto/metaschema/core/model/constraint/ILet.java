/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.metapath.MetapathException;
import gov.nist.secauto.metaschema.core.metapath.MetapathExpression;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.model.constraint.impl.DefaultLet;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents a variable assignment for use in Metaschema module constraints.
 */
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
   * @param remarks
   *          remarks about the let statement
   * @return the original let statement with the same name or {@code null}
   */
  @SuppressWarnings("PMD.ShortMethodName")
  @NonNull
  static ILet of(
      @NonNull IEnhancedQName name,
      @NonNull String valueExpression,
      @NonNull ISource source,
      @Nullable MarkupMultiline remarks) {
    try {
      return of(
          name,
          MetapathExpression.compile(valueExpression, source.getStaticContext()),
          source,
          remarks);
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
   * @param remarks
   *          remarks about the let statement
   * @return the original let statement with the same name or {@code null}
   */
  @SuppressWarnings("PMD.ShortMethodName")
  @NonNull
  static ILet of(
      @NonNull IEnhancedQName name,
      @NonNull MetapathExpression valueExpression,
      @NonNull ISource source,
      @Nullable MarkupMultiline remarks) {
    return new DefaultLet(name, valueExpression, source, remarks);
  }

  /**
   * Get the name of the let variable.
   *
   * @return the name
   */
  @NonNull
  IEnhancedQName getName();

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

  /**
   * Get the remarks associated with the let statement.
   *
   * @return the remark or {@code null} if no remarks are defined
   */
  @Nullable
  MarkupMultiline getRemarks();
}
