/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import gov.nist.secauto.metaschema.core.model.constraint.IConstraintSet;

import java.util.List;

/**
 * Provides loading capabilities for Metaschema constraint sets.
 * <p>
 * Loads constraint definitions that can be applied to Metaschema modules to
 * enforce validation rules.
 */
public interface IConstraintLoader extends ILoader<List<IConstraintSet>> {
  // no additional methods
}
