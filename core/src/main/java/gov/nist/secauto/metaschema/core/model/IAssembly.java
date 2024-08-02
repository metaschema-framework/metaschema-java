/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A marker interface for an information element that is an assembly model type.
 *
 */
public interface IAssembly extends INamedModelElement, IAttributable {
  /**
   * Provides the Metaschema model type of "ASSEMBLY".
   *
   * @return the model type
   */
  @Override
  @NonNull
  default ModelType getModelType() {
    return ModelType.ASSEMBLY;
  }
}
