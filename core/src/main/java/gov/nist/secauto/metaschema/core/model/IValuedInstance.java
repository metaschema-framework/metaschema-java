/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a Metaschema instance that has an associated value (i.e., field or
 * flag instance).
 */
public interface IValuedInstance extends INamedInstance {
  @Override
  @NonNull
  IValuedDefinition getDefinition();
}
