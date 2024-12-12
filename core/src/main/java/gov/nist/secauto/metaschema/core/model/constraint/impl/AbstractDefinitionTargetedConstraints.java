/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint.impl;

import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IFlagDefinition;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.model.constraint.AbstractTargetedConstraints;
import gov.nist.secauto.metaschema.core.model.constraint.ConstraintInitializationException;
import gov.nist.secauto.metaschema.core.model.constraint.IValueConstrained;

import java.util.Locale;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides an base implementation for a set of constraints that target a given
 * definition using a target Metapath expression.
 *
 * @param <T>
 *          the Java type of the definition target
 *
 * @param <S>
 *          the Java type of the constraint container
 */
public abstract class AbstractDefinitionTargetedConstraints<
    T extends IDefinition,
    S extends IValueConstrained>
    extends AbstractTargetedConstraints<S> {

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
  protected AbstractDefinitionTargetedConstraints(
      @NonNull ISource source,
      @NonNull IMetapathExpression target,
      @NonNull S constraints) {
    super(source, target, constraints);
  }

  /**
   * Apply the constraints to the provided {@code definition}.
   * <p>
   * This will be called when a definition is found that matches the target
   * expression.
   *
   * @param definition
   *          the definition to apply the constraints to.
   */
  protected void applyTo(@NonNull T definition) {
    getAllowedValuesConstraints().forEach(definition::addConstraint);
    getMatchesConstraints().forEach(definition::addConstraint);
    getIndexHasKeyConstraints().forEach(definition::addConstraint);
    getExpectConstraints().forEach(definition::addConstraint);
  }

  @Override
  public void target(@NonNull IFlagDefinition definition) {
    wrongDefinitionTypeTargeted(definition);
  }

  @Override
  public void target(@NonNull IFieldDefinition definition) {
    wrongDefinitionTypeTargeted(definition);
  }

  @Override
  public void target(@NonNull IAssemblyDefinition definition) {
    wrongDefinitionTypeTargeted(definition);
  }

  private void wrongDefinitionTypeTargeted(@NonNull IDefinition definition) {
    throw new ConstraintInitializationException(
        String.format(
            "The %s named '%s' from metaschema '%s' is incorrectly targeted by a set of" +
                " constraints with the target '%s'. Ensure the target expression is correct in '%s'.",
            definition.getModelType().name().toLowerCase(Locale.ROOT),
            definition.getEffectiveName(),
            definition.getContainingModule().getQName().toString(),
            getTarget(),
            getSource().getLocationHint()));
  }
}
