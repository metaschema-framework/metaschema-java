/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io.json;

import com.fasterxml.jackson.core.JsonGenerator;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelAssembly;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelComplex;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelFieldComplex;
import gov.nist.secauto.metaschema.databind.model.IBoundFieldValue;
import gov.nist.secauto.metaschema.databind.model.IBoundInstance;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceFlag;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModel;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelAssembly;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelChoiceGroup;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelFieldComplex;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelFieldScalar;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelGroupedAssembly;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelGroupedField;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelGroupedNamed;
import gov.nist.secauto.metaschema.databind.model.IBoundProperty;
import gov.nist.secauto.metaschema.databind.model.info.AbstractModelInstanceWriteHandler;
import gov.nist.secauto.metaschema.databind.model.info.IFeatureComplexItemValueHandler;
import gov.nist.secauto.metaschema.databind.model.info.IFeatureScalarItemValueHandler;
import gov.nist.secauto.metaschema.databind.model.info.IItemWriteHandler;
import gov.nist.secauto.metaschema.databind.model.info.IModelInstanceCollectionInfo;

import java.io.IOException;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

@SuppressWarnings("PMD.CouplingBetweenObjects")
public class MetaschemaJsonWriter implements IJsonWritingContext, IItemWriteHandler {
  @NonNull
  private final JsonGenerator generator;

  /**
   * Construct a new Module-aware JSON writer.
   *
   * @param generator
   *          the JSON generator to write with
   * @see DefaultJsonProblemHandler
   */
  public MetaschemaJsonWriter(@NonNull JsonGenerator generator) {
    this.generator = generator;
  }

  @Override
  public JsonGenerator getWriter() {
    return generator;
  }

  // =====================================
  // Entry point for top-level-definitions
  // =====================================

  @Override
  public void write(
      @NonNull IBoundDefinitionModelComplex definition,
      @NonNull IBoundObject item) throws IOException {
    definition.writeItem(item, this);
  }

  // ================
  // Instance writers
  // ================

  private <T> void writeInstance(
      @NonNull IBoundProperty<T> instance,
      @NonNull IBoundObject parentItem) throws IOException {
    @SuppressWarnings("unchecked") T value = (T) instance.getValue(parentItem);
    if (value != null && !value.equals(instance.getResolvedDefaultValue())) {
      generator.writeFieldName(instance.getJsonName());
      instance.writeItem(value, this);
    }
  }

  private <T> void writeModelInstance(
      @NonNull IBoundInstanceModel<T> instance,
      @NonNull Object parentItem) throws IOException {
    Object value = instance.getValue(parentItem);
    if (value != null) {
      // this if is not strictly needed, since isEmpty will return false on a null
      // value
      // checking null here potentially avoids the expensive operation of instatiating
      IModelInstanceCollectionInfo<T> collectionInfo = instance.getCollectionInfo();
      if (!collectionInfo.isEmpty(value)) {
        generator.writeFieldName(instance.getJsonName());
        collectionInfo.writeItems(new ModelInstanceWriteHandler<>(instance), value);
      }
    }
  }

  @SuppressWarnings("PMD.NullAssignment")
  private void writeFieldValue(@NonNull IBoundFieldValue fieldValue, @NonNull Object parentItem) throws IOException {
    Object item = fieldValue.getValue(parentItem);

    // handle json value key
    IBoundInstanceFlag jsonValueKey = fieldValue.getParentFieldDefinition().getJsonValueKeyFlagInstance();
    if (item == null) {
      if (jsonValueKey != null) {
        item = fieldValue.getDefaultValue();
      }
    } else if (item.equals(fieldValue.getResolvedDefaultValue())) {
      // same as default
      item = null;
    }

    if (item != null) {
      // There are two modes:
      // 1) use of a JSON value key, or
      // 2) a simple value named "value"

      String valueKeyName;
      if (jsonValueKey != null) {
        Object keyValue = jsonValueKey.getValue(parentItem);
        if (keyValue == null) {
          throw new IOException(String.format("Null value for json-value-key for definition '%s'",
              jsonValueKey.getContainingDefinition().toCoordinates()));
        }
        try {
          // this is the JSON value key case
          valueKeyName = jsonValueKey.getJavaTypeAdapter().asString(keyValue);
        } catch (IllegalArgumentException ex) {
          throw new IOException(
              String.format("Invalid value '%s' for json-value-key for definition '%s'",
                  keyValue,
                  jsonValueKey.getContainingDefinition().toCoordinates()),
              ex);
        }
      } else {
        valueKeyName = fieldValue.getParentFieldDefinition().getEffectiveJsonValueKeyName();
      }
      generator.writeFieldName(valueKeyName);
      // LOGGER.info("FIELD: {}", valueKeyName);

      writeItemFieldValue(item, fieldValue);
    }
  }

  @Override
  public void writeItemFlag(Object item, IBoundInstanceFlag instance) throws IOException {
    writeScalarItem(item, instance);
  }

  @Override
  public void writeItemField(Object item, IBoundInstanceModelFieldScalar instance) throws IOException {
    writeScalarItem(item, instance);
  }

  @Override
  public void writeItemField(IBoundObject item, IBoundInstanceModelFieldComplex instance) throws IOException {
    writeModelObject(
        instance,
        item,
        this::writeObjectProperties);
  }

  @Override
  public void writeItemField(IBoundObject item, IBoundInstanceModelGroupedField instance) throws IOException {
    writeGroupedModelObject(
        instance,
        item,
        (parent, handler) -> {
          writeDiscriminatorProperty(handler);
          writeObjectProperties(parent, handler);
        });
  }

  @Override
  public void writeItemField(IBoundObject item, IBoundDefinitionModelFieldComplex definition) throws IOException {
    writeDefinitionObject(
        definition,
        item,
        (ObjectWriter<IBoundDefinitionModelFieldComplex>) this::writeObjectProperties);
  }

  @Override
  public void writeItemFieldValue(Object item, IBoundFieldValue fieldValue) throws IOException {
    fieldValue.getJavaTypeAdapter().writeJsonValue(item, generator);
  }

  @Override
  public void writeItemAssembly(IBoundObject item, IBoundInstanceModelAssembly instance) throws IOException {
    writeModelObject(instance, item, this::writeObjectProperties);
  }

  @Override
  public void writeItemAssembly(IBoundObject item, IBoundInstanceModelGroupedAssembly instance) throws IOException {
    writeGroupedModelObject(
        instance,
        item,
        (parent, handler) -> {
          writeDiscriminatorProperty(handler);
          writeObjectProperties(parent, handler);
        });
  }

  @Override
  public void writeItemAssembly(IBoundObject item, IBoundDefinitionModelAssembly definition) throws IOException {
    writeDefinitionObject(definition, item, this::writeObjectProperties);
  }

  @Override
  public void writeChoiceGroupItem(IBoundObject item, IBoundInstanceModelChoiceGroup instance) throws IOException {
    IBoundInstanceModelGroupedNamed actualInstance = instance.getItemInstance(item);
    assert actualInstance != null;
    actualInstance.writeItem(item, this);
  }

  /**
   * Writes a scalar item.
   *
   * @param item
   *          the item to write
   * @param handler
   *          the value handler
   * @throws IOException
   *           if an error occurred while writing the scalar value
   */
  private void writeScalarItem(@NonNull Object item, @NonNull IFeatureScalarItemValueHandler handler)
      throws IOException {
    handler.getJavaTypeAdapter().writeJsonValue(item, generator);
  }

  private <T extends IBoundInstanceModelGroupedNamed> void writeDiscriminatorProperty(
      @NonNull T instance) throws IOException {

    IBoundInstanceModelChoiceGroup choiceGroup = instance.getParentContainer();

    // write JSON object discriminator
    String discriminatorProperty = choiceGroup.getJsonDiscriminatorProperty();
    String discriminatorValue = instance.getEffectiveDisciminatorValue();

    generator.writeStringField(discriminatorProperty, discriminatorValue);
  }

  private <T extends IFeatureComplexItemValueHandler> void writeObjectProperties(
      @NonNull IBoundObject parent,
      @NonNull T handler) throws IOException {
    for (IBoundProperty<?> property : handler.getJsonProperties().values()) {
      assert property != null;

      if (property instanceof IBoundInstanceModel) {
        writeModelInstance((IBoundInstanceModel<?>) property, parent);
      } else if (property instanceof IBoundInstance) {
        writeInstance(property, parent);
      } else { // IBoundFieldValue
        writeFieldValue((IBoundFieldValue) property, parent);
      }
    }
  }

  private <T extends IFeatureComplexItemValueHandler> void writeDefinitionObject(
      @NonNull T handler,
      @NonNull IBoundObject parent,
      @NonNull ObjectWriter<T> propertyWriter) throws IOException {
    generator.writeStartObject();

    propertyWriter.accept(parent, handler);
    generator.writeEndObject();
  }

  private <T extends IFeatureComplexItemValueHandler & IBoundInstanceModel<IBoundObject>>
      void writeModelObject(
          @NonNull T handler,
          @NonNull IBoundObject parent,
          @NonNull ObjectWriter<T> propertyWriter) throws IOException {
    generator.writeStartObject();

    IBoundInstanceFlag jsonKey = handler.getItemJsonKey(parent);
    if (jsonKey != null) {
      Object keyValue = jsonKey.getValue(parent);
      if (keyValue == null) {
        throw new IOException(
            String.format("Null value for json-key for definition '%s'",
                jsonKey.getContainingDefinition().toCoordinates()));
      }

      // the field will be the JSON key value
      String key;
      try {
        key = jsonKey.getJavaTypeAdapter().asString(keyValue);
      } catch (IllegalArgumentException ex) {
        throw new IOException(
            String.format("Illegal value '%s' for json-key for definition '%s'",
                keyValue,
                jsonKey.getContainingDefinition().toCoordinates()),
            ex);
      }
      generator.writeFieldName(key);

      // next the value will be a start object
      generator.writeStartObject();
    }

    propertyWriter.accept(parent, handler);

    if (jsonKey != null) {
      // next the value will be a start object
      generator.writeEndObject();
    }
    generator.writeEndObject();
  }

  private <T extends IFeatureComplexItemValueHandler & IBoundInstanceModelGroupedNamed> void writeGroupedModelObject(
      @NonNull T handler,
      @NonNull IBoundObject parent,
      @NonNull ObjectWriter<T> propertyWriter) throws IOException {
    generator.writeStartObject();

    IBoundInstanceModelChoiceGroup choiceGroup = handler.getParentContainer();
    IBoundInstanceFlag jsonKey = choiceGroup.getItemJsonKey(parent);
    if (jsonKey != null) {
      Object keyValue = jsonKey.getValue(parent);
      if (keyValue == null) {
        throw new IOException(String.format("Null value for json-key for definition '%s'",
            jsonKey.getContainingDefinition().toCoordinates()));
      }

      // the field will be the JSON key value
      String key;
      try {
        key = jsonKey.getJavaTypeAdapter().asString(keyValue);
      } catch (IllegalArgumentException ex) {
        throw new IOException(
            String.format("Invalid value '%s' for json-key for definition '%s'",
                keyValue,
                jsonKey.getContainingDefinition().toCoordinates()),
            ex);
      }
      generator.writeFieldName(key);

      // next the value will be a start object
      generator.writeStartObject();
    }

    propertyWriter.accept(parent, handler);

    if (jsonKey != null) {
      // next the value will be a start object
      generator.writeEndObject();
    }
    generator.writeEndObject();
  }

  /**
   * Supports writing items that are {@link IBoundInstanceModel}-based.
   *
   * @param <ITEM>
   *          the Java type of the item
   */
  private class ModelInstanceWriteHandler<ITEM>
      extends AbstractModelInstanceWriteHandler<ITEM> {
    public ModelInstanceWriteHandler(
        @NonNull IBoundInstanceModel<ITEM> instance) {
      super(instance);
    }

    @Override
    public void writeList(List<ITEM> items) throws IOException {
      IBoundInstanceModel<ITEM> instance = getCollectionInfo().getInstance();

      boolean writeArray = false;
      if (JsonGroupAsBehavior.LIST.equals(instance.getJsonGroupAsBehavior())
          || JsonGroupAsBehavior.SINGLETON_OR_LIST.equals(instance.getJsonGroupAsBehavior())
              && items.size() > 1) {
        // write array, then items
        writeArray = true;
        generator.writeStartArray();
      } // only other option is a singleton value, write item

      super.writeList(items);

      if (writeArray) {
        // write the end array
        generator.writeEndArray();
      }
    }

    @Override
    public void writeItem(ITEM item) throws IOException {
      IBoundInstanceModel<ITEM> instance = getInstance();
      instance.writeItem(item, MetaschemaJsonWriter.this);
    }
  }
}
