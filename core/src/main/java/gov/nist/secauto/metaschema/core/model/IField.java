/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

/**
 * A marker interface for an information element that is a field model type.
 *
 */
public interface IField extends INamedModelElement, IAttributable {
  /**
   * Provides the Metaschema model type of "FIELD".
   *
   * @return the model type
   */
  @Override
  default ModelType getModelType() {
    return ModelType.FIELD;
  }
}
