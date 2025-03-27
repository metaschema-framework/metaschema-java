/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json;

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
import gov.nist.secauto.metaschema.schemagen.json.IDefineableJsonSchema.IKey;
import gov.nist.secauto.metaschema.schemagen.json.state.impl.IJsonSchemaDefinable;
import gov.nist.secauto.metaschema.schemagen.json.state.impl.IJsonSchemaDefinition;
import gov.nist.secauto.metaschema.schemagen.json.state.impl.IJsonSchemaDefinitionAssembly;
import gov.nist.secauto.metaschema.schemagen.json.state.impl.IJsonSchemaModelDefinition;
import gov.nist.secauto.metaschema.schemagen.json.state.impl.IJsonSchemaPropertyFlag;
import gov.nist.secauto.metaschema.schemagen.json.state.impl.IJsonSchemaPropertyGrouped;
import gov.nist.secauto.metaschema.schemagen.json.state.impl.IJsonSchemaPropertyNamed;

import java.util.Collection;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public interface IJsonGenerationState extends IGenerationState<JsonGenerator> {
  /**
   * Get the JSON schema info for the provided definition.
   *
   * @param <DEF>
   *          the definition's Java type
   * @param definition
   *          the definition to get the schema info for
   * @param jsonKeyFlagName
   *          the name of the flag to use as the JSON key, or @{code null} if no
   *          flag is used as the JSON key
   * @param discriminatorProperty
   *          the property name to use as the choice group discriminator,
   *          or @{code null} if no choice group discriminator is used
   * @param discriminatorValue
   *          the property value to use as the choice group discriminator,
   *          or @{code null} if no choice group discriminator is used
   * @return the definition's schema info
   */
  @NonNull
  default <DEF extends IDefinition> IDefinitionJsonSchema<DEF> getSchema(
      @NonNull DEF definition,
      @Nullable String jsonKeyFlagName,
      @Nullable String discriminatorProperty,
      @Nullable String discriminatorValue) {
    return getSchema(IKey.of(definition, jsonKeyFlagName, discriminatorProperty, discriminatorValue));
  }

  /**
   * Get the module this data is associated with.
   * 
   * @return the module
   */
  @Override
  @NonNull
  IModule getModule();

  /**
   * Get the schemas for referenced definitions for model objects and data types
   * used within this module schema.
   * 
   * @return the ordered collection of definitions
   */
  @NonNull
  Collection<IJsonSchemaDefinable> getDefinitionSchemas();

  @Nullable
  IJsonSchemaDefinition getDefinitionSchema(
      @NonNull IDefinition definition,
      @Nullable IEnhancedQName jsonKeyName);

  @NonNull
  IJsonSchemaDefinitionAssembly newRootAssemblyDefinition(@NonNull IAssemblyDefinition definition);

  @NonNull
  IJsonSchemaDefinition getFlagDefinition(@NonNull IFlagDefinition definition);

  @NonNull
  IJsonSchemaModelDefinition getFieldDefinition(
      @NonNull IFieldDefinition defintion,
      @Nullable IEnhancedQName jsonKeyName);

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
  ObjectNode generateDefinitions();

  // TODO: consider which of the following to remove

  @NonNull
  <DEF extends IDefinition> IDefinitionJsonSchema<DEF> getSchema(@NonNull IKey key);

  @NonNull
  IDataTypeJsonSchema getSchema(@NonNull IDataTypeAdapter<?> datatype);

  @NonNull
  IDataTypeJsonSchema getDataTypeSchemaForDefinition(@NonNull IValuedDefinition definition);

  @NonNull
  JsonNodeFactory getJsonNodeFactory();

  void registerDefinitionSchema(IDefinitionJsonSchema<?> schema);

  boolean isDefinitionRegistered(IDefinitionJsonSchema<?> schema);

  default String toFlagName(@NonNull IEnhancedQName jsonKeyFlagName) {
    return jsonKeyFlagName.toEQName();
  }
}
