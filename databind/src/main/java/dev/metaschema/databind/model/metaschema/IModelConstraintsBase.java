/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.metaschema;

import java.util.List;

/**
 * Provides a common interface for model (assembly/field) constraint binding
 * objects.
 */
/**
 * Base interface for model-level constraint bindings.
 * <p>
 * This interface provides access to constraints that apply to assemblies and
 * their model content.
 */
public interface IModelConstraintsBase extends IValueConstraintsBase {
  @Override
  List<? extends ITargetedConstraintBase> getRules();
}
