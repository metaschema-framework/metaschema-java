/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IFlagDefinition;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.schemagen.json.IDataTypeJsonSchema;
import gov.nist.secauto.metaschema.schemagen.json.IDefineableJsonSchema.IKey;
import gov.nist.secauto.metaschema.schemagen.json.IDefinitionJsonSchema;
import gov.nist.secauto.metaschema.schemagen.json.IJsonGenerationState;

import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

public class FlagInstanceJsonProperty
    extends AbstractNamedInstanceJsonProperty<IFlagInstance> {

  public FlagInstanceJsonProperty(@NonNull IFlagInstance instance) {
    super(instance);

  }

  @Override
  public boolean isRequired() {
    return getInstance().isRequired();
  }

  @Override
  protected void generateMetadata(ObjectNode obj) {
    IFlagInstance instance = getInstance();
    MetadataUtils.generateTitle(instance, obj);
    MetadataUtils.generateDescription(instance, obj);
    MetadataUtils.generateDefault(instance, obj);
  }

  @Override
  public void gatherDefinitions(
      @NonNull Map<IKey, IDefinitionJsonSchema<?>> gatheredDefinitions,
      @NonNull IJsonGenerationState state) {
    // ensure data type use is registered
    IDataTypeJsonSchema dataTypeSchema = state.getDataTypeSchemaForDefinition(getInstance().getDefinition());
    if (dataTypeSchema instanceof IDefinitionJsonSchema) {
      // this is an extension schema. Use a definition if the restricted definition is
      // a definition
      ((IDefinitionJsonSchema<?>) dataTypeSchema).gatherDefinitions(gatheredDefinitions, state);
    }

    IFlagDefinition definition = getInstance().getDefinition();
    if (!state.isInline(definition)) {
      state.getSchema(IKey.of(definition)).gatherDefinitions(gatheredDefinitions, state);
    }
  }

  @Override
  protected void generateBody(
      ObjectNode obj,
      IJsonGenerationState state) {
    IFlagInstance instance = getInstance();
    IFlagDefinition definition = instance.getDefinition();
    IDataTypeJsonSchema dataTypeSchema = state.getDataTypeSchemaForDefinition(definition);
    dataTypeSchema.generateSchemaOrRef(obj, state);
  }
}
