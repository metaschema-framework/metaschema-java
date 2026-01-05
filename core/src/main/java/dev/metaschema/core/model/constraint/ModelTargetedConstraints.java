/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model.constraint;

import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.model.IAssemblyDefinition;
import dev.metaschema.core.model.ISource;

import java.util.List;
import java.util.function.Supplier;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a set of constraints targeted at model definitions using Metapath
 * expressions.
 */
public class ModelTargetedConstraints
    extends AbstractTargetedConstraints<IModelConstrained>
    implements IFeatureModelConstrained {

  /**
   * Construct a new set of targeted constraints.
   *
   * @param source
   *          information about the resource the constraints were sources from
   * @param targets
   *          a supplier to get the Metapath expressions that can be used to find
   *          matching targets
   * @param constraints
   *          the constraints to apply to matching targets
   */
  public ModelTargetedConstraints(
      @NonNull ISource source,
      @NonNull Supplier<List<IMetapathExpression>> targets,
      @NonNull IModelConstrained constraints) {
    super(source, targets, constraints);
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
  protected void applyTo(@NonNull IModelConstrained definition) {
    applyTo((IValueConstrained) definition);
    getIndexConstraints().forEach(definition::addConstraint);
    getUniqueConstraints().forEach(definition::addConstraint);
    getHasCardinalityConstraints().forEach(definition::addConstraint);
  }

  @Override
  public void target(IAssemblyDefinition definition) {
    applyTo(definition);
  }
}
