/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen.json.impl;

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

import dev.metaschema.core.datatype.IDataTypeAdapter;
import dev.metaschema.core.datatype.markup.MarkupLine;
import dev.metaschema.core.datatype.markup.MarkupMultiline;
import dev.metaschema.core.model.IAnyInstance;
import dev.metaschema.core.model.IChoiceInstance;
import dev.metaschema.core.model.IContainerModelAbsolute;
import dev.metaschema.core.model.IFlagInstance;
import dev.metaschema.core.model.IModelDefinition;
import dev.metaschema.core.model.IModelElement;
import dev.metaschema.core.model.INamedModelElement;
import dev.metaschema.core.model.IValuedDefinition;
import dev.metaschema.core.model.IValuedInstance;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.CollectionUtil;
import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Provides utility methods for generating JSON Schema elements from Metaschema
 * model components.
 * <p>
 * This class contains helper methods for generating titles, descriptions,
 * defaults, properties, and handling choice combinations in JSON Schema output.
 */
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

  /**
   * Generates a title property in the JSON Schema from the element's formal name.
   *
   * @param named
   *          the named model element to extract the title from
   * @param obj
   *          the object node to add the title property to
   */
  public static void generateTitle(
      @NonNull INamedModelElement named,
      @NonNull ObjectNode obj) {
    String formalName = named.getEffectiveFormalName();
    if (formalName != null) {
      obj.put("title", formalName);
    }
  }

  /**
   * Generates a description property in the JSON Schema from the element's
   * description and remarks.
   *
   * @param <NAMED>
   *          the type of the named model element
   * @param named
   *          the named model element to extract the description from
   * @param obj
   *          the object node to add the description property to
   */
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

  /**
   * Generates a default property in the JSON Schema from the instance's default
   * value.
   *
   * @param instance
   *          the valued instance to extract the default from
   * @param obj
   *          the object node to add the default property to
   */
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

  /**
   * Generates properties and required array for a JSON Schema object type.
   *
   * @param properties
   *          the collection of properties to generate
   * @param node
   *          the object node to add the properties and required array to
   * @param state
   *          the JSON generation state
   */
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

  /**
   * Builds a list of flag property schemas for the given definition.
   * <p>
   * Flags used as JSON keys are excluded from the returned list.
   *
   * @param definition
   *          the model definition containing the flags
   * @param jsonKeyFlagName
   *          the name of the flag used as JSON key, or {@code null} if none
   * @param state
   *          the JSON generation state
   * @return a list of flag property schemas, excluding the JSON key flag
   * @throws IllegalArgumentException
   *           if the specified JSON key flag name does not exist on the
   *           definition
   */
  @NonNull
  public static List<IJsonSchemaPropertyFlag> buildFlagProperties(
      @NonNull IModelDefinition definition,
      @Nullable IEnhancedQName jsonKeyFlagName,
      @NonNull IJsonGenerationState state) {

    Stream<? extends IFlagInstance> flagStream = definition.getFlagInstances().stream();

    // determine the flag instances to generate
    if (jsonKeyFlagName != null) {
      IFlagInstance jsonKeyFlag = definition.getFlagInstanceByName(jsonKeyFlagName.getIndexPosition());
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

  /**
   * Builds a list of model property schemas for the given container definition.
   * <p>
   * Choice instances and any instances are excluded from the returned list as
   * they are handled separately.
   *
   * @param definition
   *          the container model definition containing the model instances
   * @param state
   *          the JSON generation state
   * @return a list of model property schemas, excluding choice and any instances
   */
  @NonNull
  public static List<IJsonSchemaPropertyNamed> buildModelProperties(
      @NonNull IContainerModelAbsolute definition,
      @NonNull IJsonGenerationState state) {
    return ObjectUtils.notNull(definition.getModelInstances().stream()
        // filter out choice and any instances, which will be handled separately
        .filter(instance -> !(instance instanceof IChoiceInstance))
        .filter(instance -> !(instance instanceof IAnyInstance))
        .map(instance -> state.getJsonSchemaPropertyModel(ObjectUtils.notNull(instance)))
        .collect(Collectors.toUnmodifiableList()));
  }

  /**
   * Generates the body of a JSON Schema for a field definition.
   * <p>
   * For simple fields without non-value properties, generates a direct value
   * reference. For complex fields with flags, generates an object type with
   * properties.
   *
   * @param field
   *          the field definition schema to generate body for
   * @param node
   *          the object node to add the schema to
   * @param state
   *          the JSON generation state
   */
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

  /**
   * Generates the body of a JSON Schema for an assembly definition.
   * <p>
   * Handles choice combinations by generating either a single object type or an
   * anyOf array when multiple choice combinations exist.
   *
   * @param assembly
   *          the assembly definition schema to generate body for
   * @param node
   *          the object node to add the schema to
   * @param state
   *          the JSON generation state
   */
  public static void generateAssemblyBody(
      @NonNull IJsonSchemaDefinitionAssembly assembly,
      @NonNull ObjectNode node,
      @NonNull IJsonGenerationState state) {
    node.put("type", "object");

    // when an any instance is present, additional properties are allowed
    boolean hasAny = assembly.getDefinition().getAnyInstance() != null;

    List<JsonSchemaHelper.Choice> availableChoices = assembly.getChoices();

    if (availableChoices.size() == 1) {
      generateProperties(
          availableChoices.iterator().next().getCombinations(),
          node,
          state);
      node.put("additionalProperties", hasAny);
    } else if (availableChoices.size() > 1) {
      ArrayNode oneOf = node.putArray("anyOf");
      availableChoices.forEach(choice -> {
        ObjectNode schemaNode = ObjectUtils.notNull(oneOf.addObject());

        generateProperties(
            choice.getCombinations(),
            schemaNode,
            state);
        schemaNode.put("additionalProperties", hasAny);
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

  /**
   * Expands a base choice into all possible combinations with choice instances.
   * <p>
   * Creates a Cartesian product of the base choice with all options from the
   * provided choice instances.
   *
   * @param baseChoice
   *          the base choice to expand
   * @param choiceInstances
   *          the choice instances to combine with
   * @param state
   *          the JSON generation state
   * @return a stream of all possible choice combinations
   */
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

  /**
   * Represents a single combination of properties in a choice group.
   * <p>
   * Choice objects are used to track different valid combinations of properties
   * when generating JSON Schema for assemblies with choice elements.
   */
  public static final class Choice {
    @NonNull
    private final List<IJsonSchemaPropertyNamed> combinations;

    /**
     * Constructs a new choice with the specified property combinations.
     *
     * @param combinations
     *          the list of properties in this choice combination
     */
    public Choice(@NonNull List<IJsonSchemaPropertyNamed> combinations) {
      this.combinations = combinations;
    }

    /**
     * Retrieves the properties in this choice combination.
     *
     * @return the list of property schemas
     */
    @NonNull
    public List<IJsonSchemaPropertyNamed> getCombinations() {
      return combinations;
    }

    /**
     * Creates new choice combinations by adding each new choice to this choice.
     * <p>
     * If newChoices is empty, returns a stream containing only this choice.
     *
     * @param newChoices
     *          the new property options to combine with this choice
     * @return a stream of new choices, each containing this choice's properties
     *         plus one new property
     */
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
