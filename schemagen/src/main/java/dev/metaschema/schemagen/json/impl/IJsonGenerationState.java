/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import dev.metaschema.core.datatype.IDataTypeAdapter;
import dev.metaschema.core.model.IAssemblyDefinition;
import dev.metaschema.core.model.IDefinition;
import dev.metaschema.core.model.IFieldDefinition;
import dev.metaschema.core.model.IFlagDefinition;
import dev.metaschema.core.model.IFlagInstance;
import dev.metaschema.core.model.IModelInstanceAbsolute;
import dev.metaschema.core.model.IModule;
import dev.metaschema.core.model.INamedModelInstanceGrouped;
import dev.metaschema.core.model.IValuedDefinition;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.schemagen.IGenerationState;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents the state information used during JSON schema generation.
 * <p>
 * This interface extends {@link IGenerationState} with JSON-specific operations
 * for managing schema definitions, properties, and datatype mappings.
 */
public interface IJsonGenerationState extends IGenerationState<JsonGenerator> {
  /**
   * Get the module this data is associated with.
   *
   * @return the module
   */
  @Override
  @NonNull
  IModule getModule();

  /**
   * Get the JSON schema information for the provided definition.
   * <p>
   * This will return a cached value. The same instance will be returned if this
   * method is called multiple times.
   *
   * @param definition
   *          the flag definition to get the JSON schema information for
   * @return the JSON schema information
   */
  @NonNull
  IJsonSchemaDefinition getFlagDefinition(@NonNull IFlagDefinition definition);

  /**
   * Get the JSON schema information for the provided definition.
   * <p>
   * This will return a cached value. The same instance will be returned if this
   * method is called multiple times.
   * <p>
   * If a JSON key is provided, a definition that is unique for this JSON key will
   * be returned.
   *
   * @param definition
   *          the flag definition to get the JSON schema information for
   * @param jsonKeyName
   *          the JSON key to use with the definition or {@code null} if no JSON
   *          key is used
   * @return the JSON schema information
   */
  @NonNull
  IJsonSchemaDefinitionField getFieldDefinition(
      @NonNull IFieldDefinition definition,
      @Nullable IEnhancedQName jsonKeyName);

  /**
   * Get the JSON schema information for the provided definition.
   * <p>
   * This will return a cached value on subsequent calls with the same definition.
   * The same object will be returned if this method is called multiple times.
   * <p>
   * If a JSON key is provided, a definition that is unique for this JSON key will
   * be returned.
   *
   * @param definition
   *          the flag definition to get the JSON schema information for
   * @param jsonKeyName
   *          the JSON key to use with the definition or {@code null} if no JSON
   *          key is used
   * @return the JSON schema information
   */
  @NonNull
  IJsonSchemaDefinitionAssembly getAssemblyDefinition(
      @NonNull IAssemblyDefinition definition,
      @Nullable IEnhancedQName jsonKeyName);

  /**
   * Get the JSON schema information for the provided instance.
   * <p>
   * This will return a cached value on subsequent calls with the same instance.
   * The same object will be returned if this method is called multiple times.
   *
   * @param instance
   *          the flag instance to get the JSON schema information for
   * @return the JSON schema information
   */
  @NonNull
  IJsonSchemaPropertyFlag getJsonSchemaPropertyFlag(@NonNull IFlagInstance instance);

  /**
   * Get the JSON schema information for the provided instance.
   * <p>
   * This will return a cached value on subsequent calls with the same instance.
   * The same object will be returned if this method is called multiple times.
   *
   * @param instance
   *          the model instance to get the JSON schema information for
   * @return the JSON schema information
   */
  @NonNull
  IJsonSchemaPropertyNamed getJsonSchemaPropertyModel(@NonNull IModelInstanceAbsolute instance);

  /**
   * Get the JSON schema information for the provided instance.
   * <p>
   * This will return a cached value on subsequent calls with the same instance.
   * The same object will be returned if this method is called multiple times.
   *
   * @param instance
   *          the grouped instance to get the JSON schema information for
   * @return the JSON schema information
   */
  @NonNull
  IJsonSchemaPropertyGrouped getJsonSchemaPropertyGrouped(@NonNull INamedModelInstanceGrouped instance);

  /**
   * Generate JSON schema definitions for all used datatypes and add them to the
   * provided definitions node.
   *
   * @param definitionsNode
   *          the JSON object node to add datatype definitions to
   */
  void generateDataTypeDefinitions(@NonNull ObjectNode definitionsNode);

  /**
   * Get the JSON node factory used for creating JSON schema nodes.
   *
   * @return the JSON node factory
   */
  @NonNull
  JsonNodeFactory getJsonNodeFactory();

  /**
   * Get the JSON schema representation for the provided datatype adapter.
   *
   * @param datatype
   *          the datatype adapter to get the schema for
   * @return the JSON schema representation for the datatype
   */
  @NonNull
  IDataTypeJsonSchema getSchema(@NonNull IDataTypeAdapter<?> datatype);

  /**
   * Get the JSON schema representation for the datatype of the provided valued
   * definition.
   *
   * @param definition
   *          the valued definition to get the datatype schema for
   * @return the JSON schema representation for the definition's datatype
   */
  @NonNull
  IDataTypeJsonSchema getDataTypeSchemaForDefinition(@NonNull IValuedDefinition definition);

  /**
   * Convert a JSON key flag name to its string representation.
   *
   * @param jsonKeyFlagName
   *          the qualified name of the JSON key flag
   * @return the string representation of the flag name
   */
  @NonNull
  default String toFlagName(@NonNull IEnhancedQName jsonKeyFlagName) {
    return jsonKeyFlagName.toEQName();
  }

  /**
   * Generate a JSON schema definition name based on the provided values.
   *
   * @param definition
   *          the definition to produce the name for
   * @param jsonKeyFlagName
   *          an optional JSON property key flag name
   * @param suffix
   *          an extra value used to make the name unique
   * @return the JSON schema definition name
   */
  @NonNull
  default String generateJsonSchemaDefinitionName(
      @NonNull IDefinition definition,
      @Nullable String jsonKeyFlagName,
      @Nullable String suffix) {
    return generateJsonSchemaDefinitionName(definition, jsonKeyFlagName, null, null, suffix);
  }

  /**
   * Generate a JSON schema definition name based on the provided values.
   *
   * @param definition
   *          the definition to produce the name for
   * @param jsonKeyFlagName
   *          an optional JSON property key flag name
   * @param discriminatorProperty
   *          the JSON property name used to identify the object type
   * @param discriminatorValue
   *          the JSON property value used to identify the object type
   * @param suffix
   *          an extra value used to make the name unique
   * @return the JSON schema definition name
   */
  @SuppressWarnings("PMD.UseObjectForClearerAPI")
  @NonNull
  String generateJsonSchemaDefinitionName(
      @NonNull IDefinition definition,
      @Nullable String jsonKeyFlagName,
      @Nullable String discriminatorProperty,
      @Nullable String discriminatorValue,
      @Nullable String suffix);
}
