/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import dev.metaschema.core.configuration.IConfiguration;
import dev.metaschema.core.datatype.IDataTypeAdapter;
import dev.metaschema.core.model.IAssemblyDefinition;
import dev.metaschema.core.model.IAssemblyInstanceAbsolute;
import dev.metaschema.core.model.IAssemblyInstanceGrouped;
import dev.metaschema.core.model.IChoiceGroupInstance;
import dev.metaschema.core.model.IDefinition;
import dev.metaschema.core.model.IFieldDefinition;
import dev.metaschema.core.model.IFieldInstanceAbsolute;
import dev.metaschema.core.model.IFieldInstanceGrouped;
import dev.metaschema.core.model.IFlagDefinition;
import dev.metaschema.core.model.IFlagInstance;
import dev.metaschema.core.model.IModelDefinition;
import dev.metaschema.core.model.IModelInstanceAbsolute;
import dev.metaschema.core.model.IModule;
import dev.metaschema.core.model.INamedModelInstanceGrouped;
import dev.metaschema.core.model.IValuedDefinition;
import dev.metaschema.core.model.constraint.IAllowedValue;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.schemagen.AbstractGenerationState;
import dev.metaschema.schemagen.IGenerationState;
import dev.metaschema.schemagen.SchemaGenerationFeature;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Maintains state during JSON Schema generation from a Metaschema module.
 * <p>
 * This class manages caches for data type schemas, definition schemas, and
 * provides methods for generating JSON schema definitions and writing the
 * output.
 */
public class JsonGenerationState
    extends AbstractGenerationState<JsonGenerator, JsonDatatypeManager>
    implements IJsonGenerationState {
  @NonNull
  private final JsonNodeFactory jsonNodeFactory = new JsonNodeFactory(true);
  @NonNull
  private final Map<IValuedDefinition, IDataTypeJsonSchema> definitionValueToDataTypeSchemaMap
      = new ConcurrentHashMap<>();
  @NonNull
  private final Map<IDataTypeAdapter<?>, IDataTypeJsonSchema> dataTypeToSchemaMap = new ConcurrentHashMap<>();

  @NonNull
  private final Map<String, IJsonSchemaDefinable> definitionNameToJsonSchemaMap = new ConcurrentHashMap<>();

  @NonNull
  private final Map<IDefinition, Map<IEnhancedQName, IJsonSchemaDefinition>> definitionToJsonKeyToJsonSchemaMap
      = new ConcurrentHashMap<>();

  @NonNull
  private final Map<GroupedDefinition,
      Map<IEnhancedQName, IJsonSchemaPropertyGrouped>> groupedInstanceToJsonKeyToJsonSchemaMap
          = new ConcurrentHashMap<>();

  /**
   * Constructs a new JSON generation state for the specified module.
   *
   * @param module
   *          the Metaschema module to generate a schema for
   * @param writer
   *          the JSON generator for writing the schema output
   * @param configuration
   *          the schema generation configuration settings
   */
  public JsonGenerationState(
      @NonNull IModule module,
      @NonNull JsonGenerator writer,
      @NonNull IConfiguration<SchemaGenerationFeature<?>> configuration) {
    super(module, writer, configuration, new JsonDatatypeManager());
  }

  @NonNull
  private <T extends IJsonSchemaDefinition> T addToCache(
      @NonNull IDefinition definition,
      @Nullable IEnhancedQName jsonKeyName,
      @NonNull Supplier<T> supplier) {
    // add to definition to JSON key to JsonSchema map
    Map<IEnhancedQName, IJsonSchemaDefinition> jsonKeyMap
        = definitionToJsonKeyToJsonSchemaMap.computeIfAbsent(definition, (key) -> new LinkedHashMap<>());

    @SuppressWarnings("unchecked")
    T retval = (T) jsonKeyMap.computeIfAbsent(jsonKeyName, (key) -> supplier.get());

    assert definition.equals(retval.getDefinition());

    if (!isInline(definition)) {
      // add to definition to JSON definition name to definition map
      IJsonSchemaDefinable newSchema
          = definitionNameToJsonSchemaMap.computeIfAbsent(retval.getDefinitionName(), (key) -> retval);

      assert newSchema.equals(retval) : "Duplicate JSON definition name: "
          + retval.getDefinitionName();
    }

    return retval;
  }

  @NonNull
  private <T extends IJsonSchemaPropertyGrouped> T addToCache(
      @NonNull INamedModelInstanceGrouped instance,
      @Nullable IEnhancedQName jsonKeyName,
      @NonNull Supplier<T> supplier) {
    GroupedDefinition grouped = new GroupedDefinition(instance);

    // add to definition to JSON key to JsonSchema map
    Map<IEnhancedQName, IJsonSchemaPropertyGrouped> jsonKeyMap
        = groupedInstanceToJsonKeyToJsonSchemaMap.computeIfAbsent(grouped, (key) -> new LinkedHashMap<>());

    @SuppressWarnings("unchecked")
    T retval = (T) jsonKeyMap.computeIfAbsent(jsonKeyName, (key) -> supplier.get());

    assert grouped.equals(new GroupedDefinition(retval.getInstance()));

    if (!isInline(instance.getDefinition())) {
      // add to definition to JSON definition name to definition map
      IJsonSchemaDefinable newSchema
          = definitionNameToJsonSchemaMap.computeIfAbsent(retval.getDefinitionName(), (key) -> retval);

      assert newSchema.equals(retval) : "Duplicate JSON definition name: "
          + retval.getDefinitionName();
    }
    return retval;
  }

  private static class GroupedDefinition {
    private final IModelDefinition definition;
    private final String disciminatorProperty;
    private final String disciminatorValue;

    public GroupedDefinition(@NonNull INamedModelInstanceGrouped instance) {
      this.definition = instance.getDefinition();
      this.disciminatorProperty = instance.getParentContainer().getJsonDiscriminatorProperty();
      this.disciminatorValue = instance.getEffectiveDisciminatorValue();
    }

    @Override
    public int hashCode() {
      return Objects.hash(definition, disciminatorProperty, disciminatorValue);
    }

    @SuppressWarnings("PMD.OnlyOneReturn")
    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      GroupedDefinition other = (GroupedDefinition) obj;
      return Objects.equals(definition, other.definition)
          && Objects.equals(disciminatorProperty, other.disciminatorProperty)
          && Objects.equals(disciminatorValue, other.disciminatorValue);
    }
  }

  @Override
  public IJsonSchemaDefinitionAssembly getAssemblyDefinition(
      IAssemblyDefinition definition,
      IEnhancedQName jsonKeyName) {
    return addToCache(definition, jsonKeyName, () -> new JsonSchemaDefinitionAssembly(definition, jsonKeyName, this));
  }

  @Override
  public IJsonSchemaDefinitionField getFieldDefinition(IFieldDefinition definition, IEnhancedQName jsonKeyName) {
    return addToCache(definition, jsonKeyName, () -> new JsonSchemaDefinitionField(definition, jsonKeyName, this));
  }

  @Override
  public IJsonSchemaDefinition getFlagDefinition(IFlagDefinition definition) {
    return addToCache(definition, null, () -> new JsonSchemaDefinitionFlag(definition, this));
  }

  @Override
  public IJsonSchemaPropertyFlag getJsonSchemaPropertyFlag(IFlagInstance instance) {
    return new JsonSchemaPropertyFlag(instance, this);
  }

  @Override
  public IJsonSchemaPropertyNamed getJsonSchemaPropertyModel(@NonNull IModelInstanceAbsolute instance) {
    IJsonSchemaPropertyNamed retval;
    if (instance instanceof IAssemblyInstanceAbsolute) {
      retval = new JsonSchemaPropertyAssembly((IAssemblyInstanceAbsolute) instance, this);
    } else if (instance instanceof IFieldInstanceAbsolute) {
      retval = new JsonSchemaPropertyField((IFieldInstanceAbsolute) instance, this);
    } else if (instance instanceof IChoiceGroupInstance) {
      retval = new JsonSchemaPropertyChoiceGroup((IChoiceGroupInstance) instance, this);
    } else {
      throw new UnsupportedOperationException("Unsupported property type: " + instance.getClass());
    }
    return retval;
  }

  @Override
  public IJsonSchemaPropertyGrouped getJsonSchemaPropertyGrouped(INamedModelInstanceGrouped instance) {
    return addToCache(instance, null, () -> newJsonSchemaPropertyGrouped(instance));
  }

  private IJsonSchemaPropertyGrouped newJsonSchemaPropertyGrouped(INamedModelInstanceGrouped instance) {
    IJsonSchemaPropertyGrouped retval;
    if (instance instanceof IAssemblyInstanceGrouped) {
      retval = new JsonSchemaPropertyGroupedAssembly((IAssemblyInstanceGrouped) instance, this);
    } else if (instance instanceof IFieldInstanceGrouped) {
      retval = new JsonSchemaPropertyGroupedField((IFieldInstanceGrouped) instance, this);
    } else {
      throw new UnsupportedOperationException("Unsupported property type: " + instance.getClass());
    }
    return retval;
  }

  @Override
  @NonNull
  public IDataTypeJsonSchema getSchema(@NonNull IDataTypeAdapter<?> datatype) {
    IDataTypeJsonSchema retval = dataTypeToSchemaMap.get(datatype);
    if (retval == null) {
      retval = new DataTypeJsonSchema(
          getDatatypeManager().getTypeNameForDatatype(datatype),
          datatype);
      dataTypeToSchemaMap.put(datatype, retval);
    }
    return retval;
  }

  @Override
  public void generateDataTypeDefinitions(@NonNull ObjectNode definitionsNode) {
    getDatatypeManager().generateDatatypeDefinitions(definitionsNode);
  }

  @Override
  public JsonNodeFactory getJsonNodeFactory() {
    return jsonNodeFactory;
  }

  @Override
  @NonNull
  public IDataTypeJsonSchema getDataTypeSchemaForDefinition(@NonNull IValuedDefinition definition) {
    IDataTypeJsonSchema retval = definitionValueToDataTypeSchemaMap.get(definition);
    if (retval == null) {
      AllowedValueCollection allowedValuesCollection = getContextIndependentEnumeratedValues(definition);
      List<IAllowedValue> allowedValues = allowedValuesCollection.getValues();

      IDataTypeAdapter<?> dataTypeAdapter = definition.getJavaTypeAdapter();

      // register data type use
      retval = getSchema(dataTypeAdapter);
      if (!allowedValues.isEmpty()) {
        // create restriction
        retval = new DataTypeRestrictionDefinitionJsonSchema(definition, allowedValuesCollection, this);
      }
      definitionValueToDataTypeSchemaMap.put(definition, retval);
    }
    return retval;
  }

  @Override
  @SuppressWarnings("PMD.UseObjectForClearerAPI")
  public String generateJsonSchemaDefinitionName(
      @NonNull IDefinition definition,
      @Nullable String jsonKeyFlagName,
      @Nullable String discriminatorProperty,
      @Nullable String discriminatorValue,
      @Nullable String suffix) {
    StringBuilder builder = new StringBuilder();
    if (jsonKeyFlagName != null) {
      builder
          .append(IGenerationState.toCamelCase(jsonKeyFlagName))
          .append("JsonKey");
    }

    if (discriminatorProperty != null || discriminatorValue != null) {
      builder
          .append(IGenerationState.toCamelCase(ObjectUtils.requireNonNull(discriminatorProperty)))
          .append(IGenerationState.toCamelCase(ObjectUtils.requireNonNull(discriminatorValue)))
          .append("Choice");
    }

    if (suffix != null) {
      builder.append(suffix);
    }
    return getTypeNameForDefinition(definition, builder.toString());
  }

  /**
   * Writes an object node to the JSON output.
   *
   * @param schemaNode
   *          the object node to write
   * @throws IOException
   *           if an I/O error occurs during writing
   */
  public void writeObject(ObjectNode schemaNode) throws IOException {
    getWriter().writeObject(schemaNode);
  }

  /**
   * Writes the start of a JSON object to the output.
   *
   * @throws IOException
   *           if an I/O error occurs during writing
   */
  public void writeStartObject() throws IOException {
    getWriter().writeStartObject();
  }

  /**
   * Writes the end of a JSON object to the output.
   *
   * @throws IOException
   *           if an I/O error occurs during writing
   */
  public void writeEndObject() throws IOException {
    getWriter().writeEndObject();
  }

  /**
   * Writes a field with a string value to the JSON output.
   *
   * @param fieldName
   *          the name of the field to write
   * @param value
   *          the string value of the field
   * @throws IOException
   *           if an I/O error occurs during writing
   */
  public void writeField(String fieldName, String value) throws IOException {
    getWriter().writeStringField(fieldName, value);

  }

  /**
   * Writes a field with an object node value to the JSON output.
   *
   * @param fieldName
   *          the name of the field to write
   * @param obj
   *          the object node value of the field
   * @throws IOException
   *           if an I/O error occurs during writing
   */
  public void writeField(String fieldName, ObjectNode obj) throws IOException {
    JsonGenerator writer = getWriter(); // NOPMD not closable here

    writer.writeFieldName(fieldName);
    writer.writeTree(obj);
  }

  @Override
  public void flushWriter() throws IOException {
    getWriter().flush();
  }
}
