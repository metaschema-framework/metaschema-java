/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model.constraint;

import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.model.IAssemblyDefinition;
import dev.metaschema.core.model.IFieldDefinition;
import dev.metaschema.core.model.IFlagDefinition;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a set of constraints that target a given definition using a target
 * Metapath expression.
 */
public interface ITargetedConstraints extends IValueConstrained {
  /**
   * Get the Metapath expressions used to identify the targets of the constraint.
   *
   * @return the Metapath expressions identifying the targets of the associated
   *         constraints
   */
  @NonNull
  List<IMetapathExpression> getTargets();

  /**
   * Apply the constraint to the provided definition.
   *
   * @param definition
   *          the definition to apply the constraint to
   */
  void target(@NonNull IFlagDefinition definition);

  /**
   * Apply the constraint to the provided definition.
   *
   * @param definition
   *          the definition to apply the constraint to
   */
  void target(@NonNull IFieldDefinition definition);

  /**
   * Apply the constraint to the provided definition.
   *
   * @param definition
   *          the definition to apply the constraint to
   */
  void target(@NonNull IAssemblyDefinition definition);
}
