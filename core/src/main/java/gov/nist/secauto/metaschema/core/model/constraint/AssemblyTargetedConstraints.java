/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.constraint.impl.AbstractDefinitionTargetedConstraints;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A set of constraints targeting a {@link IAssemblyDefinition} based on a
 * target Metapath expression.
 *
 * @see #getTargetExpression()
 */
public class AssemblyTargetedConstraints
    extends AbstractDefinitionTargetedConstraints<IAssemblyDefinition, IModelConstrained>
    implements IFeatureModelConstrained {

  /**
   * Construct a new set of targeted constraints.
   *
   * @param target
   *          the Metapath expression that can be used to find matching targets
   * @param constraints
   *          the constraints to apply to matching targets
   */
  public AssemblyTargetedConstraints(
      @NonNull String target,
      @NonNull IModelConstrained constraints) {
    super(target, constraints);
  }

  @Override
  public void target(@NonNull IAssemblyDefinition definition) {
    applyTo(definition);
  }

  @Override
  protected void applyTo(@NonNull IAssemblyDefinition definition) {
    super.applyTo(definition);
    getIndexConstraints().forEach(definition::addConstraint);
    getUniqueConstraints().forEach(definition::addConstraint);
    getHasCardinalityConstraints().forEach(definition::addConstraint);
  }
}
