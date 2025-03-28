/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A JSON schema for a given Metaschema-based definition instance, that has a
 * distinct property name, which is part of a larger JSON schema.
 */
public interface IJsonSchemaPropertyNamed extends IJsonSchemaProperty {
  /**
   * Get the name of the JSON property.
   *
   * @return the JSON property name
   */
  @NonNull
  String getName();

  /**
   * Determine if the property is required or not.
   *
   * @return {@code true} if the property is required or {@code false} otherwise
   */
  boolean isRequired();
}
