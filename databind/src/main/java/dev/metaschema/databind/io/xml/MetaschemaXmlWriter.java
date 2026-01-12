/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.io.xml;

import org.codehaus.stax2.XMLStreamWriter2;

import java.io.IOException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;

import dev.metaschema.core.model.IBoundObject;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.databind.io.json.DefaultJsonProblemHandler;
import dev.metaschema.databind.model.IBoundDefinitionModel;
import dev.metaschema.databind.model.IBoundDefinitionModelAssembly;
import dev.metaschema.databind.model.IBoundDefinitionModelComplex;
import dev.metaschema.databind.model.IBoundDefinitionModelFieldComplex;
import dev.metaschema.databind.model.IBoundFieldValue;
import dev.metaschema.databind.model.IBoundInstanceFlag;
import dev.metaschema.databind.model.IBoundInstanceModel;
import dev.metaschema.databind.model.IBoundInstanceModelAssembly;
import dev.metaschema.databind.model.IBoundInstanceModelChoiceGroup;
import dev.metaschema.databind.model.IBoundInstanceModelFieldComplex;
import dev.metaschema.databind.model.IBoundInstanceModelFieldScalar;
import dev.metaschema.databind.model.IBoundInstanceModelGroupedAssembly;
import dev.metaschema.databind.model.IBoundInstanceModelGroupedField;
import dev.metaschema.databind.model.IBoundInstanceModelGroupedNamed;
import dev.metaschema.databind.model.IBoundInstanceModelNamed;
import dev.metaschema.databind.model.info.AbstractModelInstanceWriteHandler;
import dev.metaschema.databind.model.info.IFeatureComplexItemValueHandler;
import dev.metaschema.databind.model.info.IItemWriteHandler;
import dev.metaschema.databind.model.info.IModelInstanceCollectionInfo;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides support for writing Metaschema-bound Java objects to XML format.
 * <p>
 * This class implements the {@link IXmlWritingContext} interface to serialize
 * bound objects to XML using StAX's {@link XMLStreamWriter2}. It handles flags
 * as attributes and fields/assemblies as child elements according to the
 * Metaschema XML serialization rules.
 *
 * @see IXmlWritingContext
 * @see XMLStreamWriter2
 */
public class MetaschemaXmlWriter implements IXmlWritingContext {
  @NonNull
  private final XMLStreamWriter2 writer;

  /**
   * Construct a new Module-aware JSON writer.
   *
   * @param writer
   *          the XML stream writer to write with
   * @see DefaultJsonProblemHandler
   */
  public MetaschemaXmlWriter(
      @NonNull XMLStreamWriter2 writer) {
    this.writer = writer;
  }

  @Override
  public XMLStreamWriter2 getWriter() {
    return writer;
  }

  // =====================================
  // Entry point for top-level-definitions
  // =====================================

  @Override
  public void write(
      @NonNull IBoundDefinitionModelComplex definition,
      @NonNull IBoundObject item) throws IOException {

    IEnhancedQName qname = definition.getQName();

    definition.writeItem(item, new ItemWriter(qname));
  }

  @Override
  public void writeRoot(
      @NonNull IBoundDefinitionModelAssembly definition,
      @NonNull IBoundObject item) throws IOException {
    IEnhancedQName rootEQName = definition.getRootQName();
    if (rootEQName == null) {
      throw new IllegalArgumentException(
          String.format("The assembly definition '%s' does not have a root QName.",
              definition.getQName()));
    }

    definition.writeItem(item, new ItemWriter(rootEQName));
  }

  // ================
  // Instance writers
  // ================

  private <T> void writeModelInstance(
      @NonNull IBoundInstanceModel<T> instance,
      @NonNull Object parentItem,
      @NonNull ItemWriter itemWriter) throws IOException {
    Object value = instance.getValue(parentItem);
    if (value == null) {
      return;
    }

    // this if is not strictly needed, since isEmpty will return false on a null
    // value
    // checking null here potentially avoids the expensive operation of
    // instantiating
    IModelInstanceCollectionInfo<T> collectionInfo = instance.getCollectionInfo();
    if (!collectionInfo.isEmpty(value)) {
      IEnhancedQName currentQName = itemWriter.getObjectQName();
      IEnhancedQName groupAsEQName = instance.getEffectiveXmlGroupAsQName();
      try {
        if (groupAsEQName != null) {
          // write the grouping element
          writer.writeStartElement(groupAsEQName.getNamespace(), groupAsEQName.getLocalName());
          currentQName = groupAsEQName;
        }

        collectionInfo.writeItems(
            new ModelInstanceWriteHandler<>(instance, new ItemWriter(currentQName)),
            value);

        if (groupAsEQName != null) {
          writer.writeEndElement();
        }
      } catch (XMLStreamException ex) {
        throw new IOException(ex);
      }
    }
  }

  private static class ModelInstanceWriteHandler<ITEM>
      extends AbstractModelInstanceWriteHandler<ITEM> {
    @NonNull
    private final ItemWriter itemWriter;

    public ModelInstanceWriteHandler(
        @NonNull IBoundInstanceModel<ITEM> instance,
        @NonNull ItemWriter itemWriter) {
      super(instance);
      this.itemWriter = itemWriter;
    }

    @Override
    public void writeItem(ITEM item) throws IOException {
      IBoundInstanceModel<ITEM> instance = getInstance();
      instance.writeItem(item, itemWriter);
    }
  }

  private class ItemWriter
      extends AbstractItemWriter {

    public ItemWriter(@NonNull IEnhancedQName qname) {
      super(qname);
    }

    private <T extends IBoundInstanceModelNamed<IBoundObject> & IFeatureComplexItemValueHandler> void writeFlags(
        @NonNull IBoundObject parentItem,
        @NonNull T instance) throws IOException {
      writeFlags(parentItem, instance.getDefinition());
    }

    private <T extends IBoundInstanceModelGroupedNamed & IFeatureComplexItemValueHandler> void writeFlags(
        @NonNull IBoundObject parentItem,
        @NonNull T instance) throws IOException {
      writeFlags(parentItem, instance.getDefinition());
    }

    private void writeFlags(
        @NonNull IBoundObject parentItem,
        @NonNull IBoundDefinitionModel<?> definition) throws IOException {
      for (IBoundInstanceFlag flag : definition.getFlagInstances()) {
        assert flag != null;

        Object value = flag.getValue(parentItem);
        if (value != null) {
          writeItemFlag(value, flag);
        }
      }
    }

    private <T extends IBoundInstanceModelAssembly & IFeatureComplexItemValueHandler> void writeAssemblyModel(
        @NonNull IBoundObject parentItem,
        @NonNull T instance) throws IOException {
      writeAssemblyModel(parentItem, instance.getDefinition());
    }

    private <T extends IBoundInstanceModelGroupedAssembly & IFeatureComplexItemValueHandler> void writeAssemblyModel(
        @NonNull IBoundObject parentItem,
        @NonNull T instance) throws IOException {
      writeAssemblyModel(parentItem, instance.getDefinition());
    }

    private void writeAssemblyModel(
        @NonNull IBoundObject parentItem,
        @NonNull IBoundDefinitionModelAssembly definition) throws IOException {
      for (IBoundInstanceModel<?> modelInstance : definition.getModelInstances()) {
        assert modelInstance != null;
        writeModelInstance(modelInstance, parentItem, this);
      }
    }

    private void writeFieldValue(
        @NonNull IBoundObject parentItem,
        @NonNull IBoundInstanceModelFieldComplex instance) throws IOException {
      writeFieldValue(parentItem, instance.getDefinition());
    }

    private void writeFieldValue(
        @NonNull IBoundObject parentItem,
        @NonNull IBoundInstanceModelGroupedField instance) throws IOException {
      writeFieldValue(parentItem, instance.getDefinition());
    }

    private void writeFieldValue(
        @NonNull IBoundObject parentItem,
        @NonNull IBoundDefinitionModelFieldComplex definition) throws IOException {
      definition.getFieldValue().writeItem(parentItem, this);
    }

    private <T extends IFeatureComplexItemValueHandler & IBoundInstanceModelNamed<IBoundObject>> void writeModelObject(
        @NonNull T instance,
        @NonNull IBoundObject parentItem,
        @NonNull ObjectWriter<T> propertyWriter) throws IOException {
      try {
        IEnhancedQName wrapperQName = instance.getQName();
        writer.writeStartElement(wrapperQName.getNamespace(), wrapperQName.getLocalName());

        propertyWriter.accept(parentItem, instance);

        writer.writeEndElement();
      } catch (XMLStreamException ex) {
        throw new IOException(ex);
      }
    }

    private <T extends IFeatureComplexItemValueHandler & IBoundInstanceModelGroupedNamed> void writeGroupedModelObject(
        @NonNull T instance,
        @NonNull IBoundObject parentItem,
        @NonNull ObjectWriter<T> propertyWriter) throws IOException {
      try {
        IEnhancedQName wrapperQName = instance.getQName();
        writer.writeStartElement(wrapperQName.getNamespace(), wrapperQName.getLocalName());

        propertyWriter.accept(parentItem, instance);

        writer.writeEndElement();
      } catch (XMLStreamException ex) {
        throw new IOException(ex);
      }
    }

    private <T extends IFeatureComplexItemValueHandler & IBoundDefinitionModelComplex> void writeDefinitionObject(
        @NonNull T definition,
        @NonNull IBoundObject parentItem,
        @NonNull ObjectWriter<T> propertyWriter) throws IOException {

      try {
        IEnhancedQName qname = getObjectQName();
        NamespaceContext nsContext = writer.getNamespaceContext();
        String prefix = nsContext.getPrefix(qname.getNamespace());
        if (prefix == null) {
          prefix = "";
        }

        writer.writeStartElement(prefix, qname.getLocalName(), qname.getNamespace());

        propertyWriter.accept(parentItem, definition);

        writer.writeEndElement();
      } catch (XMLStreamException ex) {
        throw new IOException(ex);
      }
    }

    @Override
    public void writeItemFlag(Object item, IBoundInstanceFlag instance) throws IOException {
      String itemString;
      try {
        itemString = instance.getJavaTypeAdapter().asString(item);
      } catch (IllegalArgumentException ex) {
        throw new IOException(ex);
      }
      IEnhancedQName name = instance.getQName();
      try {
        if (name.getNamespace().isEmpty()) {
          writer.writeAttribute(name.getLocalName(), itemString);
        } else {
          writer.writeAttribute(name.getNamespace(), name.getLocalName(), itemString);
        }
      } catch (XMLStreamException ex) {
        throw new IOException(ex);
      }
    }

    @Override
    public void writeItemField(Object item, IBoundInstanceModelFieldScalar instance) throws IOException {
      try {
        if (instance.isEffectiveValueWrappedInXml()) {
          IEnhancedQName wrapperQName = instance.getQName();
          writer.writeStartElement(wrapperQName.getNamespace(), wrapperQName.getLocalName());
          instance.getJavaTypeAdapter().writeXmlValue(item, wrapperQName, writer);
          writer.writeEndElement();
        } else {
          instance.getJavaTypeAdapter().writeXmlValue(item, getObjectQName(), writer);
        }
      } catch (XMLStreamException ex) {
        throw new IOException(ex);
      }
    }

    @Override
    public void writeItemField(IBoundObject item, IBoundInstanceModelFieldComplex instance) throws IOException {
      ItemWriter itemWriter = new ItemWriter(instance.getQName());
      writeModelObject(
          instance,
          item,
          ((ObjectWriter<IBoundInstanceModelFieldComplex>) this::writeFlags)
              .andThen(itemWriter::writeFieldValue));
    }

    @Override
    public void writeItemField(IBoundObject item, IBoundInstanceModelGroupedField instance) throws IOException {
      ItemWriter itemWriter = new ItemWriter(instance.getQName());
      writeGroupedModelObject(
          instance,
          item,
          ((ObjectWriter<IBoundInstanceModelGroupedField>) this::writeFlags)
              .andThen(itemWriter::writeFieldValue));
    }

    @Override
    public void writeItemField(IBoundObject item, IBoundDefinitionModelFieldComplex definition) throws IOException {
      ItemWriter itemWriter = new ItemWriter(definition.getQName());
      writeDefinitionObject(
          definition,
          item,
          ((ObjectWriter<IBoundDefinitionModelFieldComplex>) this::writeFlags)
              .andThen(itemWriter::writeFieldValue));
    }

    @Override
    public void writeItemFieldValue(Object parentItem, IBoundFieldValue fieldValue) throws IOException {
      Object item = fieldValue.getValue(parentItem);
      if (item != null) {
        fieldValue.getJavaTypeAdapter().writeXmlValue(item, getObjectQName(), writer);
      }
    }

    @Override
    public void writeItemAssembly(IBoundObject item, IBoundInstanceModelAssembly instance) throws IOException {
      ItemWriter itemWriter = new ItemWriter(instance.getQName());
      writeModelObject(
          instance,
          item,
          ((ObjectWriter<IBoundInstanceModelAssembly>) this::writeFlags)
              .andThen(itemWriter::writeAssemblyModel));
    }

    @Override
    public void writeItemAssembly(IBoundObject item, IBoundInstanceModelGroupedAssembly instance) throws IOException {
      ItemWriter itemWriter = new ItemWriter(instance.getQName());
      writeGroupedModelObject(
          instance,
          item,
          ((ObjectWriter<IBoundInstanceModelGroupedAssembly>) this::writeFlags)
              .andThen(itemWriter::writeAssemblyModel));
    }

    @Override
    public void writeItemAssembly(IBoundObject item, IBoundDefinitionModelAssembly definition) throws IOException {
      // this is a special case where we are writing a top-level, potentially root,
      // element. Need to take the object qname passed in
      writeDefinitionObject(
          definition,
          item,
          ((ObjectWriter<IBoundDefinitionModelAssembly>) this::writeFlags)
              .andThen(this::writeAssemblyModel));
    }

    @Override
    public void writeChoiceGroupItem(IBoundObject item, IBoundInstanceModelChoiceGroup instance) throws IOException {
      IBoundInstanceModelGroupedNamed actualInstance = instance.getItemInstance(item);
      assert actualInstance != null;
      actualInstance.writeItem(item, this);
    }
  }

  private abstract static class AbstractItemWriter implements IItemWriteHandler {
    @NonNull
    private final IEnhancedQName objectQName;

    protected AbstractItemWriter(@NonNull IEnhancedQName qname) {
      this.objectQName = qname;
    }

    /**
     * Get the qualified name of the item's parent.
     *
     * @return the qualified name
     */
    @NonNull
    protected IEnhancedQName getObjectQName() {
      return objectQName;
    }
  }
}
