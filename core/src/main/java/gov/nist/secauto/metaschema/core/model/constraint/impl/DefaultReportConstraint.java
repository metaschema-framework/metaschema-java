/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint.impl;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBooleanItem;
import gov.nist.secauto.metaschema.core.model.IAttributable;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.model.constraint.IReportConstraint;

import java.util.Map;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents a report constraint.
 * <p>
 * A report constraint generates a finding when the associated test evaluates to
 * {@link IBooleanItem#TRUE} against the target. This is the opposite behavior
 * of an expect constraint, which generates a finding when the test evaluates to
 * {@code FALSE}.
 * <p>
 * Report constraints are useful for:
 * <ul>
 * <li>Flagging deprecated patterns or values</li>
 * <li>Reporting known issues or limitations</li>
 * <li>Providing informational messages about content characteristics</li>
 * </ul>
 *
 * @since 2.0.0
 */
public final class DefaultReportConstraint
    extends AbstractConfigurableMessageConstraint
    implements IReportConstraint {
  @NonNull
  private final IMetapathExpression test;

  /**
   * Construct a new report constraint.
   *
   * @param id
   *          the optional identifier for the constraint
   * @param formalName
   *          the constraint's formal name or {@code null} if not provided
   * @param description
   *          the constraint's semantic description or {@code null} if not
   *          provided
   * @param source
   *          information about the constraint source
   * @param level
   *          the significance of a violation of this constraint
   * @param target
   *          the Metapath expression identifying the nodes the constraint targets
   * @param properties
   *          a collection of associated properties
   * @param test
   *          a Metapath expression that is evaluated against the target node to
   *          determine if a condition should be reported; a finding is generated
   *          when this evaluates to {@code true}
   * @param message
   *          an optional message to emit when the constraint condition is matched
   * @param remarks
   *          optional remarks describing the intent of the constraint
   */
  @SuppressWarnings("PMD.ExcessiveParameterList")
  public DefaultReportConstraint(
      @Nullable String id,
      @Nullable String formalName,
      @Nullable MarkupLine description,
      @NonNull ISource source,
      @NonNull Level level,
      @NonNull IMetapathExpression target,
      @NonNull Map<IAttributable.Key, Set<String>> properties,
      @NonNull IMetapathExpression test,
      @Nullable String message,
      @Nullable MarkupMultiline remarks) {
    super(id, formalName, description, source, level, target, properties, message, remarks);
    this.test = test;
  }

  @Override
  public IMetapathExpression getTest() {
    return test;
  }
}
