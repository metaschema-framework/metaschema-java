/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model;

import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * A common interface found bound objects that have a complex model consisting
 * of flags, fields, or assemblies.
 */
@FunctionalInterface
public interface IBoundObject {
  /**
   * Get additional Metaschema-related information for the object (i.e., resource
   * location).
   *
   * @return the Metaschema-related information
   */
  @Nullable
  IMetaschemaData getMetaschemaData();
}
