/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a model instance that has a JSON property name.
 * <p>
 * This interface provides access to the name used when the instance appears in
 * JSON or YAML serialization.
 */
@FunctionalInterface
public interface IJsonInstance {
  /**
   * Get the name used for the instance in JSON/YAML serialization.
   *
   * @return the JSON property name
   */
  @NonNull
  String getJsonName();
}
