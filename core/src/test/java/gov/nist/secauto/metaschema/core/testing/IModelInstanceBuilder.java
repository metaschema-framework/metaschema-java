/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.testing;

import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceAbsolute;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IModelInstanceBuilder {
  /**
   * Generate an instance of the implementing type using the provided parent.
   *
   * @param parent
   *          the parent assembly definition containing the instance
   * @return the new instance
   */
  @NonNull
  INamedModelInstanceAbsolute toInstance(@NonNull IAssemblyDefinition parent);
}
