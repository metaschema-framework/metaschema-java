/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl;

import gov.nist.secauto.metaschema.core.model.INamedModelInstanceGrouped;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Supports generation of a JSON schema based on a Metaschema
 * {@link INamedModelInstanceGrouped}, which can be generated inline or as a
 * JSON schema definition.
 */
public interface IJsonSchemaPropertyGrouped extends IJsonSchemaProperty, IJsonSchemaModelDefinition {
  /**
   * Get the associated Metaschema grouped instance.
   * 
   * @return the instance
   */
  @NonNull
  INamedModelInstanceGrouped getInstance();
}
