/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.configuration.IConfiguration;
import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstanceGrouped;
import gov.nist.secauto.metaschema.core.model.IChoiceGroupInstance;
import gov.nist.secauto.metaschema.core.model.IDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.IFieldInstanceGrouped;
import gov.nist.secauto.metaschema.core.model.IFlagDefinition;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.core.model.IModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceGrouped;
import gov.nist.secauto.metaschema.core.model.IValuedDefinition;
import gov.nist.secauto.metaschema.core.model.constraint.IAllowedValue;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.schemagen.AbstractGenerationState;
import gov.nist.secauto.metaschema.schemagen.IGenerationState;
import gov.nist.secauto.metaschema.schemagen.SchemaGenerationFeature;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

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

  public void writeObject(ObjectNode schemaNode) throws IOException {
    getWriter().writeObject(schemaNode);
  }

  @SuppressWarnings("resource")
  public void writeStartObject() throws IOException {
    getWriter().writeStartObject();
  }

  @SuppressWarnings("resource")
  public void writeEndObject() throws IOException {
    getWriter().writeEndObject();
  }

  @SuppressWarnings("resource")
  public void writeField(String fieldName, String value) throws IOException {
    getWriter().writeStringField(fieldName, value);

  }

  @SuppressWarnings("resource")
  public void writeField(String fieldName, ObjectNode obj) throws IOException {
    JsonGenerator writer = getWriter(); // NOPMD not closable here

    writer.writeFieldName(fieldName);
    writer.writeTree(obj);
  }

  @SuppressWarnings("resource")
  @Override
  public void flushWriter() throws IOException {
    getWriter().flush();
  }
}
