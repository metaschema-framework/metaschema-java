/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.model.constraint.impl.AbstractDefinitionTargetedConstraints;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A set of constraints targeting a {@link IFieldDefinition} based on a target
 * Metapath expression.
 *
 * @see #getTarget()
 */
public class FieldTargetedConstraints
    extends AbstractDefinitionTargetedConstraints<IFieldDefinition, IValueConstrained> {

  /**
   * Construct a new set of targeted constraints.
   *
   * @param source
   *          information about the resource the constraints were sources from
   * @param target
   *          the Metapath expression that can be used to find matching targets
   * @param constraints
   *          the constraints to apply to matching targets
   */
  public FieldTargetedConstraints(
      @NonNull ISource source,
      @NonNull IMetapathExpression target,
      @NonNull IValueConstrained constraints) {
    super(source, target, constraints);
  }

  @Override
  public void target(@NonNull IFieldDefinition definition) {
    applyTo(definition);
  }
}
