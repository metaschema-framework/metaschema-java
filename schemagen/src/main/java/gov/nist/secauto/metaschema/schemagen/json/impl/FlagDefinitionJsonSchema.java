/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IFlagDefinition;
import gov.nist.secauto.metaschema.schemagen.json.IDataTypeJsonSchema;
import gov.nist.secauto.metaschema.schemagen.json.IDefinitionJsonSchema;
import gov.nist.secauto.metaschema.schemagen.json.IJsonGenerationState;

import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

public class FlagDefinitionJsonSchema
    extends AbstractDefinitionJsonSchema<IFlagDefinition> {
  @NonNull
  private final IKey key;

  public FlagDefinitionJsonSchema(@NonNull IFlagDefinition definition, @NonNull IJsonGenerationState state) {
    super(definition);
    this.key = IKey.of(definition);
    state.getDataTypeSchemaForDefinition(definition);
  }

  @Override
  protected String generateDefinitionName(IJsonGenerationState state) {
    return state.getTypeNameForDefinition(getDefinition(), null);
  }

  @Override
  protected void generateBody(
      IJsonGenerationState state,
      ObjectNode obj) {
    IFlagDefinition definition = getDefinition();
    IDataTypeJsonSchema schema = state.getDataTypeSchemaForDefinition(definition);
    schema.generateSchemaOrRef(obj, state);
  }

  @Override
  public void gatherDefinitions(
      @NonNull Map<IKey, IDefinitionJsonSchema<?>> gatheredDefinitions,
      @NonNull IJsonGenerationState state) {
    super.gatherDefinitions(gatheredDefinitions, state);

    IDataTypeJsonSchema schema = state.getDataTypeSchemaForDefinition(getDefinition());
    if (schema instanceof IDefinitionJsonSchema) {
      ((IDefinitionJsonSchema<?>) schema).gatherDefinitions(gatheredDefinitions, state);
    }
  }

  @Override
  public IKey getKey() {
    return key;
  }
}
