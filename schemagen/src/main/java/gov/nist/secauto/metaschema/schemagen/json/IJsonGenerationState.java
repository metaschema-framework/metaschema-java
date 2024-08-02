/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.model.IDefinition;
import gov.nist.secauto.metaschema.core.model.IValuedDefinition;
import gov.nist.secauto.metaschema.schemagen.IGenerationState;
import gov.nist.secauto.metaschema.schemagen.json.IDefineableJsonSchema.IKey;

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
}
