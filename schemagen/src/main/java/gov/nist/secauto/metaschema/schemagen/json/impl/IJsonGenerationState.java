/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IFlagDefinition;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.IModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceGrouped;
import gov.nist.secauto.metaschema.core.model.IValuedDefinition;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.schemagen.IGenerationState;

import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

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
  IJsonSchemaDefinitionAssembly getAssemblyDefinition(
      @NonNull IAssemblyDefinition defintion,
      @Nullable IEnhancedQName jsonKeyName);

  @NonNull
  IJsonSchemaPropertyFlag getJsonSchemaPropertyFlag(@NonNull IFlagInstance instance);

  @NonNull
  IJsonSchemaPropertyNamed newJsonSchemaPropertyModel(@NonNull IModelInstanceAbsolute instance);

  @NonNull
  IJsonSchemaPropertyGrouped getJsonSchemaPropertyGrouped(@NonNull INamedModelInstanceGrouped instance);

  @NonNull
  ObjectNode generateDefinitions(@NonNull Set<IJsonSchemaDefinable> usedDefinitions);

  @NonNull
  JsonNodeFactory getJsonNodeFactory();

  @NonNull
  IDataTypeJsonSchema getSchema(@NonNull IDataTypeAdapter<?> datatype);

  @NonNull
  IDataTypeJsonSchema getDataTypeSchemaForDefinition(@NonNull IValuedDefinition definition);

  @NonNull
  default String toFlagName(@NonNull IEnhancedQName jsonKeyFlagName) {
    return jsonKeyFlagName.toEQName();
  }

  @NonNull
  default String generateJsonSchemaDefinitionName(
      @NonNull IDefinition definition,
      @Nullable String jsonKeyFlagName,
      @Nullable String suffix) {
    return generateJsonSchemaDefinitionName(definition, jsonKeyFlagName, null, null, suffix);
  }

  @NonNull
  String generateJsonSchemaDefinitionName(
      @NonNull IDefinition definition,
      @Nullable String jsonKeyFlagName,
      @Nullable String discriminatorProperty,
      @Nullable String discriminatorValue,
      @Nullable String suffix);
}
