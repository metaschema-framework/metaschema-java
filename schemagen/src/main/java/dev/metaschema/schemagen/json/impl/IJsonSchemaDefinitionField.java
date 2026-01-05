/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen.json.impl;

import dev.metaschema.core.model.IFieldDefinition;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Supports generation of a JSON schema based on a Metaschema model assembly
 * definition, which can be generated inline or as a JSON schema definition.
 */
public interface IJsonSchemaDefinitionField extends IJsonSchemaModelDefinition {
  @Override
  IFieldDefinition getDefinition();

  /**
   * Get the sequence of JSON schema properties, excluding the properties for the
   * field's value.
   *
   * @return the JSON schema properties
   */
  @NonNull
  List<? extends IJsonSchemaPropertyNamed> getNonValueProperties();

  /**
   * Get the JSON schema information for the field value's data type.
   *
   * @return the data type information
   */
  @NonNull
  IDataTypeJsonSchema getFieldValue();
}
