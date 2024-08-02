/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.schemagen.FlagInstanceFilter;
import gov.nist.secauto.metaschema.schemagen.json.IDataTypeJsonSchema;
import gov.nist.secauto.metaschema.schemagen.json.IDefinitionJsonSchema;
import gov.nist.secauto.metaschema.schemagen.json.IJsonGenerationState;
import gov.nist.secauto.metaschema.schemagen.json.impl.IJsonProperty.PropertyCollection;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class FieldDefinitionJsonSchema
    extends AbstractModelDefinitionJsonSchema<IFieldDefinition> {

  public FieldDefinitionJsonSchema(
      @NonNull IFieldDefinition definition,
      @Nullable String jsonKeyFlagName,
      @Nullable String discriminatorProperty,
      @Nullable String discriminatorValue,
      @NonNull IJsonGenerationState state) {
    super(definition, jsonKeyFlagName, discriminatorProperty, discriminatorValue);
    // register the flag data type
    state.getDataTypeSchemaForDefinition(definition);
  }

  @SuppressWarnings("PMD.CognitiveComplexity")
  @Override
  protected void generateBody(
      IJsonGenerationState state,
      ObjectNode obj) throws IOException {
    IFieldDefinition definition = getDefinition();

    Collection<? extends IFlagInstance> flags = definition.getFlagInstances();
    String discriminatorProperty = getDiscriminatorProperty();
    IFlagInstance jsonKeyFlag = definition.getJsonKey();
    if (discriminatorProperty == null
        && (flags.isEmpty() || jsonKeyFlag != null && flags.size() == 1)) { // NOPMD readability
      // field is a simple data type value if there are no flags or if the only flag
      // is a JSON key
      IDataTypeJsonSchema schema = state.getDataTypeSchemaForDefinition(definition);
      schema.generateSchemaOrRef(obj, state);
    } else {
      obj.put("type", "object");

      // determine the flag instances to generate
      IFlagInstance jsonValueKeyFlag = definition.getJsonValueKeyFlagInstance();
      flags = FlagInstanceFilter.filterFlags(flags, jsonKeyFlag, jsonValueKeyFlag);

      PropertyCollection properties = new PropertyCollection();

      // handle possible discriminator
      if (discriminatorProperty != null) {
        ObjectNode discriminatorObj = state.getJsonNodeFactory().objectNode();
        discriminatorObj.put("const", getDiscriminatorValue());
        properties.addProperty(discriminatorProperty, discriminatorObj);
      }

      // generate flag properties
      for (IFlagInstance flag : flags) {
        assert flag != null;
        new FlagInstanceJsonProperty(flag)
            .generateProperty(properties, state); // NOPMD unavoidable instantiation
      }

      // generate value property
      if (jsonValueKeyFlag == null) {
        generateSimpleFieldValueInstance(properties, state);
      }

      properties.generate(obj);

      if (jsonValueKeyFlag == null) {
        obj.put("additionalProperties", false);
      } else {
        ObjectNode additionalPropertiesTypeNode;

        additionalPropertiesTypeNode = ObjectUtils.notNull(JsonNodeFactory.instance.objectNode());
        // the type of the additional properties must be the datatype of the field value
        IDataTypeJsonSchema schema = state.getDataTypeSchemaForDefinition(definition);
        schema.generateSchemaOrRef(additionalPropertiesTypeNode, state);

        ObjectNode additionalPropertiesNode = ObjectUtils.notNull(JsonNodeFactory.instance.objectNode());
        ArrayNode allOf = additionalPropertiesNode.putArray("allOf");
        allOf.add(additionalPropertiesTypeNode);
        allOf.addObject()
            .put("minProperties", properties.getRequired().size() + 1)
            .put("maxProperties", properties.getProperties().size() + 1);

        obj.set("additionalProperties", additionalPropertiesNode);
      }
    }
  }

  public void generateSimpleFieldValueInstance(
      @NonNull PropertyCollection properties,
      @NonNull IJsonGenerationState state) {

    IFieldDefinition definition = getDefinition();

    String propertyName = definition.getEffectiveJsonValueKeyName();

    ObjectNode propertyObject = ObjectUtils.notNull(JsonNodeFactory.instance.objectNode());
    IDataTypeJsonSchema schema = state.getDataTypeSchemaForDefinition(definition);
    schema.generateSchemaOrRef(propertyObject, state);

    properties.addProperty(propertyName, propertyObject);
    properties.addRequired(propertyName);
  }

  @Override
  public void gatherDefinitions(
      @NonNull Map<IKey, IDefinitionJsonSchema<?>> gatheredDefinitions,
      @NonNull IJsonGenerationState state) {
    super.gatherDefinitions(gatheredDefinitions, state);

    IFieldDefinition definition = getDefinition();
    IDataTypeJsonSchema schema = state.getDataTypeSchemaForDefinition(definition);
    if (schema instanceof IDefinitionJsonSchema) {
      ((IDefinitionJsonSchema<?>) schema).gatherDefinitions(gatheredDefinitions, state);
    }
  }

}
