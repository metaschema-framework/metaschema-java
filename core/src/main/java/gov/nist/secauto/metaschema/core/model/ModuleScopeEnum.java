/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

public enum ModuleScopeEnum {
  // TODO: consider naming these PRIVATE and PUBLIC in a 2.0
  /**
   * The definition is scoped to only the defining module.
   */
  LOCAL,
  /**
   * The definition is scoped to its defining module and any importing module.
   */
  INHERITED;
}
