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
import gov.nist.secauto.metaschema.core.model.IDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IFlagDefinition;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.IValuedDefinition;
import gov.nist.secauto.metaschema.core.model.constraint.IAllowedValue;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.schemagen.AbstractGenerationState;
import gov.nist.secauto.metaschema.schemagen.SchemaGenerationFeature;
import gov.nist.secauto.metaschema.schemagen.json.IDataTypeJsonSchema;
import gov.nist.secauto.metaschema.schemagen.json.IDefineableJsonSchema.IKey;
import gov.nist.secauto.metaschema.schemagen.json.IDefinitionJsonSchema;
import gov.nist.secauto.metaschema.schemagen.json.IJsonGenerationState;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class JsonGenerationState
    extends AbstractGenerationState<JsonGenerator, JsonDatatypeManager>
    implements IJsonGenerationState {

  @NonNull
  private final JsonNodeFactory jsonNodeFactory = new JsonNodeFactory(true);
  @NonNull
  private final Map<IKey, IDefinitionJsonSchema<?>> schemaDefinitions = new HashMap<>();
  @NonNull
  private final Map<IValuedDefinition, IDataTypeJsonSchema> definitionValueToDataTypeSchemaMap
      = new ConcurrentHashMap<>();
  @NonNull
  private final Map<IDataTypeAdapter<?>, IDataTypeJsonSchema> dataTypeToSchemaMap = new ConcurrentHashMap<>();

  public JsonGenerationState(
      @NonNull IModule module,
      @NonNull JsonGenerator writer,
      @NonNull IConfiguration<SchemaGenerationFeature<?>> configuration) {
    super(module, writer, configuration, new JsonDatatypeManager());

    // // seed definition schema mapping
    // this.schemaDefinitions =
    // ObjectUtils.notNull(getMetaschemaIndex().getDefinitions().stream()
    // .filter(entry -> !isInline(entry.getDefinition()) &&
    // entry.isUsedWithoutJsonKey()
    // && !entry.isChoiceGroupMember())
    // .map(entry -> newJsonSchema(entry.getDefinition(), null, null, null, this))
    // .collect(Collectors.toMap(
    // schema -> schema.getKey(),
    // Function.identity(),
    // (v1, v2) -> v2,
    // ConcurrentHashMap::new)));
  }

  @Override
  @NonNull
  public <DEF extends IDefinition> IDefinitionJsonSchema<DEF> getSchema(@NonNull IKey key) {
    IDefinitionJsonSchema<?> retval = getDefinitionSchema(key, this);
    return ObjectUtils.asType(ObjectUtils.requireNonNull(retval));
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

  /**
   * Get the JSON schema info for the provided definition.
   *
   * @param key
   *          the key to use to lookup the definition schema info
   * @return the definition's schema info
   */
  private IDefinitionJsonSchema<?> getDefinitionSchema(
      @NonNull IKey key,
      @NonNull IJsonGenerationState state) {
    synchronized (schemaDefinitions) {
      return schemaDefinitions.computeIfAbsent(key, (k) -> {
        IDefinitionJsonSchema<?> retval = newJsonSchema(
            k.getDefinition(),
            k.getJsonKeyFlagName(),
            k.getDiscriminatorProperty(),
            k.getDiscriminatorValue(),
            state);
        assert key.equals(retval.getKey());
        return retval;
      });
    }
  }

  @Override
  public boolean isDefinitionRegistered(IDefinitionJsonSchema<?> schema) {
    return schemaDefinitions.containsKey(schema.getKey());
  }

  @Override
  public void registerDefinitionSchema(IDefinitionJsonSchema<?> schema) {
    IDefinitionJsonSchema<?> old = schemaDefinitions.put(schema.getKey(), schema);
    assert old == null;
  }

  /**
   * Get the JSON schema info for the provided definition.
   *
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
  private static IDefinitionJsonSchema<?> newJsonSchema(
      @NonNull IDefinition definition,
      @Nullable String jsonKeyFlagName,
      @Nullable String discriminatorProperty,
      @Nullable String discriminatorValue,
      @NonNull IJsonGenerationState state) {
    IDefinitionJsonSchema<?> retval;
    if (definition instanceof IFlagDefinition) {
      retval = new FlagDefinitionJsonSchema((IFlagDefinition) definition, state);
    } else if (definition instanceof IAssemblyDefinition) {
      retval = new AssemblyDefinitionJsonSchema(
          (IAssemblyDefinition) definition,
          jsonKeyFlagName,
          discriminatorProperty,
          discriminatorValue,
          state);
    } else if (definition instanceof IFieldDefinition) {
      retval = new FieldDefinitionJsonSchema(
          (IFieldDefinition) definition,
          jsonKeyFlagName,
          discriminatorProperty,
          discriminatorValue,
          state);
    } else {
      throw new IllegalArgumentException("Unsupported definition type" + definition.getClass().getName());
    }
    return retval;
  }

  public ObjectNode generateDefinitions() {
    @NonNull Map<IKey, IDefinitionJsonSchema<?>> gatheredDefinitions = new HashMap<>();

    getMetaschemaIndex().getDefinitions().stream()
        .filter(entry -> entry.isRoot())
        .map(entry -> entry.getDefinition())
        .forEachOrdered(def -> {
          IDefinitionJsonSchema<?> definitionSchema = getSchema(IKey.of(def));
          assert definitionSchema != null;
          definitionSchema.gatherDefinitions(gatheredDefinitions, this);
        });

    ObjectNode definitionsObject = ObjectUtils.notNull(JsonNodeFactory.instance.objectNode());

    gatheredDefinitions.values().stream()
        .filter(schema -> !isInline(schema.getDefinition()))
        .sorted(Comparator.comparing(schema -> schema.getDefinitionName(this)))
        .forEachOrdered(schema -> {
          schema.generateDefinition(this, definitionsObject);
        });

    getDatatypeManager().generateDatatypes(definitionsObject);

    return definitionsObject;
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
        retval = new DataTypeRestrictionDefinitionJsonSchema(definition, allowedValuesCollection);
      }
      definitionValueToDataTypeSchemaMap.put(definition, retval);
    }
    return retval;
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

  @Override
  public void flushWriter() throws IOException {
    getWriter().flush();
  }

}
