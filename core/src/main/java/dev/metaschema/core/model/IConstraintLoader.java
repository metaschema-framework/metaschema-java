/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model;

import java.util.List;

import dev.metaschema.core.model.constraint.IConstraintSet;

/**
 * Provides loading capabilities for Metaschema constraint sets.
 * <p>
 * Loads constraint definitions that can be applied to Metaschema modules to
 * enforce validation rules.
 */
public interface IConstraintLoader extends ILoader<List<IConstraintSet>> {
  // no additional methods
}
