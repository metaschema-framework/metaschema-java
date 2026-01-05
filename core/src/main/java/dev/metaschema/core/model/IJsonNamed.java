/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a model element that has a JSON property name.
 * <p>
 * This interface provides access to the name used when serializing or
 * deserializing the element in JSON or YAML format.
 */
@FunctionalInterface
public interface IJsonNamed {

  /**
   * Get the name used for the associated property in JSON/YAML
   * serialization-related operations.
   *
   * @return the JSON property name
   */
  @NonNull
  String getJsonName();
}
