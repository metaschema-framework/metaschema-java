/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen.datatype;

import dev.metaschema.core.datatype.IDataTypeAdapter;

import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Manages datatype mappings and tracks datatype usage during schema generation.
 */
public interface IDatatypeManager {
  /**
   * Get the schema type name for the provided datatype adapter.
   *
   * @param datatype
   *          the datatype adapter to get the type name for
   * @return the schema type name corresponding to the datatype
   */
  String getTypeNameForDatatype(@NonNull IDataTypeAdapter<?> datatype);

  /**
   * Get the set of datatype names that have been used during schema generation.
   *
   * @return an unmodifiable set of used datatype names
   */
  Set<String> getUsedTypes();
}
