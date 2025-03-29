/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BigIntegerNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.metapath.StaticMetapathException;
import gov.nist.secauto.metaschema.core.model.IChoiceInstance;
import gov.nist.secauto.metaschema.core.model.IContainerModelAbsolute;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.core.model.IModelElement;
import gov.nist.secauto.metaschema.core.model.INamedModelElement;
import gov.nist.secauto.metaschema.core.model.IValuedDefinition;
import gov.nist.secauto.metaschema.core.model.IValuedInstance;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public final class JsonSchemaHelper {
  /**
   * Supports comparison of named properties by their property name.
   */
  public static final Comparator<IJsonSchemaPropertyNamed> INSTANCE_NAME_COMPARATOR
      = Comparator.comparing(IJsonSchemaPropertyNamed::getName);
  /**
   * Supports comparison of JSON schema definitions by their definition name.
   */
  public static final Comparator<IJsonSchemaDefinable> DEFINABLE_NAME_COMPARATOR
      = Comparator.comparing(IJsonSchemaDefinable::getDefinitionName);

  public static void generateTitle(
      @NonNull INamedModelElement named,
      @NonNull ObjectNode obj) {
    String formalName = named.getEffectiveFormalName();
    if (formalName != null) {
      obj.put("title", formalName);
    }
  }

  public static <NAMED extends INamedModelElement & IModelElement> void generateDescription(
      @NonNull NAMED named,
      @NonNull ObjectNode obj) {
    MarkupLine description = named.getEffectiveDescription();

    StringBuilder retval = null;
    if (description != null) {
      retval = new StringBuilder().append(description.toMarkdown());
    }

    MarkupMultiline remarks = named.getRemarks();
    if (remarks != null) {
      if (retval == null) {
        retval = new StringBuilder();
      } else {
        retval.append("\n\n");
      }
      retval.append(remarks.toMarkdown());
    }
    if (retval != null) {
      obj.put("description", retval.toString());
    }
  }

  public static void generateDefault(
      @NonNull IValuedInstance instance,
      @NonNull ObjectNode obj) {
    Object defaultValue = instance.getEffectiveDefaultValue();
    if (defaultValue != null) {
      IValuedDefinition definition = instance.getDefinition();
      IDataTypeAdapter<?> adapter = definition.getJavaTypeAdapter();
      obj.set("default", toJsonValue(defaultValue, adapter));
    }
  }

  private static JsonNode toJsonValue(
      @Nullable Object defaultValue,
      @NonNull IDataTypeAdapter<?> adapter) {
    JsonNode retval = null; // use default conversion by default
    switch (adapter.getJsonRawType()) {
    case BOOLEAN:
      if (defaultValue instanceof Boolean) {
        retval = BooleanNode.valueOf((Boolean) defaultValue);
      } // else use default conversion
      break;
    case INTEGER:
      if (defaultValue instanceof BigInteger) {
        retval = BigIntegerNode.valueOf((BigInteger) defaultValue);
      } else if (defaultValue instanceof Integer) {
        retval = IntNode.valueOf((Integer) defaultValue);
      } else if (defaultValue instanceof Long) {
        retval = LongNode.valueOf((Long) defaultValue);
      } // else use default conversion
      break;
    case NUMBER:
      if (defaultValue instanceof BigDecimal) {
        retval = DecimalNode.valueOf((BigDecimal) defaultValue);
      } else if (defaultValue instanceof Double) {
        retval = DoubleNode.valueOf((Double) defaultValue);
      } // else use default conversion
      break;
    case ANY:
    case ARRAY:
    case OBJECT:
    case NULL:
      throw new UnsupportedOperationException("Invalid type: " + adapter.getClass());
    case STRING:
      // use default conversion
      break;
    }

    if (retval == null && defaultValue != null) {
      retval = TextNode.valueOf(adapter.asString(defaultValue));
    }
    return retval;
  }

  /**
   * Generate a JSON pointer expression that points to the provided JSON schema
   * definition for use as a schema reference.
   *
   * @param schema
   *          the JSON schema definition to generate the pointer for
   * @return the JSON pointer
   */
  @NonNull
  public static String generateDefinitionJsonPointer(@NonNull IJsonSchemaDefinable schema) {
    return ObjectUtils.notNull(new StringBuilder()
        .append("#/definitions/")
        .append(schema.getDefinitionName())
        .toString());
  }

  public static void generateProperties(
      @NonNull Collection<IJsonSchemaPropertyNamed> properties,
      @NonNull ObjectNode node,
      @NonNull IJsonGenerationState state) {

    if (!properties.isEmpty()) {
      ObjectNode propertiesNode = node.putObject("properties");

      List<String> required = new LinkedList<>();
      properties.forEach(property -> {
        ObjectNode propertyNode = ObjectUtils.notNull(state.getJsonNodeFactory().objectNode());
        property.generate(propertyNode, state);

        String name = property.getName();
        propertiesNode.set(name, propertyNode);

        if (property.isRequired()) {
          required.add(name);
        }
      });

      if (!required.isEmpty()) {
        ArrayNode requiredNode = node.putArray("required");
        required.forEach(requiredNode::add);
      }
    }
  }

  @NonNull
  public static List<IJsonSchemaPropertyFlag> buildFlagProperties(
      @NonNull IModelDefinition definition,
      @Nullable IEnhancedQName jsonKeyFlagName,
      @NonNull IJsonGenerationState state) {

    Stream<? extends IFlagInstance> flagStream = definition.getFlagInstances().stream();

    // determine the flag instances to generate
    if (jsonKeyFlagName != null) {
      IFlagInstance jsonKeyFlag;
      try {
        jsonKeyFlag = definition.getFlagInstanceByName(jsonKeyFlagName.getIndexPosition());
      } catch (StaticMetapathException ex) {
        throw new IllegalArgumentException(ex);
      }
      if (jsonKeyFlag == null) {
        throw new IllegalArgumentException(
            String.format("The referenced json-key flag-name '%s' does not exist on definition '%s'.",
                jsonKeyFlagName,
                definition.getName()));
      }
      flagStream = flagStream.filter(instance -> !jsonKeyFlag.equals(instance));
    }

    return ObjectUtils.notNull(flagStream
        .map(instance -> state.getJsonSchemaPropertyFlag(ObjectUtils.requireNonNull(instance)))
        .collect(Collectors.toUnmodifiableList()));
  }

  @NonNull
  public static List<IJsonSchemaPropertyNamed> buildModelProperties(
      @NonNull IContainerModelAbsolute definition,
      @NonNull IJsonGenerationState state) {
    return ObjectUtils.notNull(definition.getModelInstances().stream()
        // filter out choice instances, which will be handled separately
        .filter(instance -> !(instance instanceof IChoiceInstance))
        .map(instance -> state.getJsonSchemaPropertyModel(ObjectUtils.notNull(instance)))
        .collect(Collectors.toUnmodifiableList()));
  }

  public static void generateFieldBody(
      @NonNull IJsonSchemaDefinitionField field,
      @NonNull ObjectNode node,
      @NonNull IJsonGenerationState state) {
    if (field.getNonValueProperties().isEmpty()) {
      // simple case
      field.getFieldValue().generateJsonSchemaOrDefinitionRef(node, state);
    } else {
      generateComplexFieldBody(field, node, state);
    }
  }

  private static void generateComplexFieldBody(
      @NonNull IJsonSchemaDefinitionField field,
      @NonNull ObjectNode node,
      @NonNull IJsonGenerationState state) {
    List<? extends IJsonSchemaPropertyNamed> nonValueProperties = field.getNonValueProperties();
    node.put("type", "object");

    IFlagInstance jsonValueKeyFlag = field.getDefinition().getJsonValueKeyFlagInstance();

    Stream<? extends IJsonSchemaPropertyNamed> propertiesStream = nonValueProperties.stream();

    if (jsonValueKeyFlag == null) {
      // make room for the value
      propertiesStream = Stream.concat(propertiesStream, Stream.of(new FieldValueProperty(field)));
    } else {
      propertiesStream = propertiesStream.filter(property -> !(property instanceof IJsonSchemaPropertyFlag)
          || !jsonValueKeyFlag.equals(((IJsonSchemaPropertyFlag) property).getInstance()));
    }

    List<IJsonSchemaPropertyNamed> properties = ObjectUtils.notNull(propertiesStream
        .sorted(INSTANCE_NAME_COMPARATOR)
        .collect(Collectors.toUnmodifiableList()));
    generateProperties(
        properties,
        node,
        state);

    if (jsonValueKeyFlag == null) {
      node.put("additionalProperties", false);
    } else {
      ObjectNode additionalPropertiesTypeNode;

      additionalPropertiesTypeNode = ObjectUtils.notNull(JsonNodeFactory.instance.objectNode());
      // the type of the additional properties must be the datatype of the field value
      field.getFieldValue().generateJsonSchemaOrDefinitionRef(additionalPropertiesTypeNode, state);

      ObjectNode additionalPropertiesNode = ObjectUtils.notNull(JsonNodeFactory.instance.objectNode());
      ArrayNode allOf = additionalPropertiesNode.putArray("allOf");
      allOf.add(additionalPropertiesTypeNode);
      allOf.addObject()
          .put(
              "minProperties",
              // increment by one to allow for the value key
              properties.stream()
                  .filter(IJsonSchemaPropertyNamed::isRequired)
                  .count() + 1)
          .put("maxProperties", properties.size() + 1);

      node.set("additionalProperties", additionalPropertiesNode);
    }
  }

  public static void generateAssemblyBody(
      @NonNull IJsonSchemaDefinitionAssembly assembly,
      @NonNull ObjectNode node,
      @NonNull IJsonGenerationState state) {
    node.put("type", "object");

    List<JsonSchemaHelper.Choice> availableChoices = assembly.getChoices();

    if (availableChoices.size() == 1) {
      generateProperties(
          availableChoices.iterator().next().getCombinations(),
          node,
          state);
      node.put("additionalProperties", false);
    } else if (availableChoices.size() > 1) {
      ArrayNode oneOf = node.putArray("anyOf");
      availableChoices.forEach(choice -> {
        ObjectNode schemaNode = ObjectUtils.notNull(oneOf.addObject());

        generateProperties(
            choice.getCombinations(),
            schemaNode,
            state);
        schemaNode.put("additionalProperties", false);
      });
    }
  }

  private static class FieldValueProperty implements IJsonSchemaPropertyNamed {
    private final IJsonSchemaDefinitionField field;

    public FieldValueProperty(IJsonSchemaDefinitionField field) {
      this.field = field;
    }

    @Override
    public String getName() {
      return field.getDefinition().getEffectiveJsonValueKeyName();
    }

    @Override
    public Stream<IJsonSchemaDefinable> collectDefinitions(
        Set<IJsonSchemaDefinitionAssembly> visited,
        IJsonGenerationState state) {
      return ObjectUtils.notNull(Stream.empty());
    }

    @Override
    public void generate(ObjectNode node, IJsonGenerationState state) {
      field.getFieldValue().generateJsonSchemaOrDefinitionRef(ObjectUtils.notNull(node.putObject(getName())), state);
    }

    @Override
    public boolean isRequired() {
      return true;
    }
  }

  @NonNull
  public static Stream<Choice> explodeChoices(
      @NonNull Choice baseChoice,
      @NonNull List<? extends IChoiceInstance> choiceInstances,
      @NonNull IJsonGenerationState state) {
    Stream<Choice> retval = Stream.of(baseChoice);
    for (IChoiceInstance choice : choiceInstances) {
      List<IJsonSchemaPropertyNamed> newChoices = buildModelProperties(ObjectUtils.notNull(choice), state);
      retval = retval.flatMap(oldChoice -> oldChoice.explode(newChoices));
    }
    return ObjectUtils.notNull(retval);
  }

  public static final class Choice {
    @NonNull
    private final List<IJsonSchemaPropertyNamed> combinations;

    public Choice(@NonNull List<IJsonSchemaPropertyNamed> combinations) {
      this.combinations = combinations;
    }

    @NonNull
    public List<IJsonSchemaPropertyNamed> getCombinations() {
      return combinations;
    }

    @NonNull
    public Stream<Choice> explode(@NonNull List<IJsonSchemaPropertyNamed> newChoices) {
      return ObjectUtils.notNull(newChoices.isEmpty()
          ? Stream.of(this)
          : newChoices.stream()
              .map(next -> {
                List<IJsonSchemaPropertyNamed> retval = new ArrayList<>(combinations.size() + 1);
                retval.addAll(combinations);
                retval.add(next);
                return new Choice(CollectionUtil.unmodifiableList(retval));
              }));
    }
  }

  private JsonSchemaHelper() {
    // disable construction
  }
}
