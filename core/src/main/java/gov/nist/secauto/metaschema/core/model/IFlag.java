/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

/**
 * A marker interface for an information element that is an flag model type.
 *
 */
public interface IFlag extends INamedModelElement, IAttributable {
  /**
   * Provides the Metaschema model type of "FLAG".
   *
   * @return the model type
   */
  @Override
  default ModelType getModelType() {
    return ModelType.FLAG;
  }
}
