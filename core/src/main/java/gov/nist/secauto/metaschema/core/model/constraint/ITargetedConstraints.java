/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IFlagDefinition;
import gov.nist.secauto.metaschema.core.model.ISource;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a set of constraints that target a given definition using a target
 * Metapath expression.
 */
public interface ITargetedConstraints extends IValueConstrained {
  /**
   * Get information about the resource the constraints were sources from.
   *
   * @return the source information
   */
  @NonNull
  ISource getSource();

  /**
   * Get the Metapath expression used to identify the target of the constraint.
   *
   * @return the uncompiled Metapath expression
   */
  @NonNull
  String getTargetExpression();

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
