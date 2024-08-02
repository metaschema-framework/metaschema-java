/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.typeinfo;

import gov.nist.secauto.metaschema.core.model.IInstance;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IInstanceTypeInfo extends IPropertyTypeInfo {
  /**
   * Get the instance associated with this type information.
   *
   * @return the instance
   */
  @NonNull
  IInstance getInstance();
}
