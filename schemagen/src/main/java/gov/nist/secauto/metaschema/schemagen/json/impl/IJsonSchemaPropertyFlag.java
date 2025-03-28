/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl;

import gov.nist.secauto.metaschema.core.model.IFlagInstance;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Supports generation of a JSON schema based on a Metaschema
 * {@link IFlagInstance}, which can be generated inline or as a JSON schema
 * definition.
 */
public interface IJsonSchemaPropertyFlag extends IJsonSchemaPropertyNamed {
  /**
   * Get the associated Metaschema flag instance.
   * 
   * @return the instance
   */
  @NonNull
  IFlagInstance getInstance();
}
