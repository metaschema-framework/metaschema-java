/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io.xml;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.io.json.DefaultJsonProblemHandler;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModel;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelAssembly;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelComplex;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelFieldComplex;
import gov.nist.secauto.metaschema.databind.model.IBoundFieldValue;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceFlag;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModel;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelAssembly;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelChoiceGroup;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelFieldComplex;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelFieldScalar;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelGroupedAssembly;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelGroupedField;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelGroupedNamed;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelNamed;
import gov.nist.secauto.metaschema.databind.model.info.AbstractModelInstanceWriteHandler;
import gov.nist.secauto.metaschema.databind.model.info.IFeatureComplexItemValueHandler;
import gov.nist.secauto.metaschema.databind.model.info.IItemWriteHandler;
import gov.nist.secauto.metaschema.databind.model.info.IModelInstanceCollectionInfo;

import org.codehaus.stax2.XMLStreamWriter2;

import java.io.IOException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import edu.umd.cs.findbugs.annotations.NonNull;

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

    QName qname = definition.getXmlQName();

    definition.writeItem(item, new ItemWriter(qname));
  }

  @Override
  public void writeRoot(
      @NonNull IBoundDefinitionModelAssembly definition,
      @NonNull IBoundObject item) throws IOException {
    definition.writeItem(item, new ItemWriter(ObjectUtils.requireNonNull(definition.getRootXmlQName())));
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
      QName currentQName = itemWriter.getObjectQName();
      QName groupAsQName = instance.getEffectiveXmlGroupAsQName();
      try {
        if (groupAsQName != null) {
          // write the grouping element
          writer.writeStartElement(groupAsQName.getNamespaceURI(), groupAsQName.getLocalPart());
          currentQName = groupAsQName;
        }

        collectionInfo.writeItems(
            new ModelInstanceWriteHandler<>(instance, new ItemWriter(currentQName)),
            value);

        if (groupAsQName != null) {
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

    public ItemWriter(@NonNull QName qname) {
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
        QName wrapperQName = instance.getXmlQName();
        writer.writeStartElement(wrapperQName.getNamespaceURI(), wrapperQName.getLocalPart());

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
        QName wrapperQName = instance.getXmlQName();
        writer.writeStartElement(wrapperQName.getNamespaceURI(), wrapperQName.getLocalPart());

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
        QName qname = getObjectQName();
        NamespaceContext nsContext = writer.getNamespaceContext();
        String prefix = nsContext.getPrefix(qname.getNamespaceURI());
        if (prefix == null) {
          prefix = "";
        }

        writer.writeStartElement(prefix, qname.getLocalPart(), qname.getNamespaceURI());

        propertyWriter.accept(parentItem, definition);

        writer.writeEndElement();
      } catch (XMLStreamException ex) {
        throw new IOException(ex);
      }
    }

    @Override
    public void writeItemFlag(Object item, IBoundInstanceFlag instance) throws IOException {
      String itemString = instance.getJavaTypeAdapter().asString(item);
      QName name = instance.getXmlQName();
      try {
        if (name.getNamespaceURI().isEmpty()) {
          writer.writeAttribute(name.getLocalPart(), itemString);
        } else {
          writer.writeAttribute(name.getNamespaceURI(), name.getLocalPart(), itemString);
        }
      } catch (XMLStreamException ex) {
        throw new IOException(ex);
      }
    }

    @Override
    public void writeItemField(Object item, IBoundInstanceModelFieldScalar instance) throws IOException {
      try {
        if (instance.isEffectiveValueWrappedInXml()) {
          QName wrapperQName = instance.getXmlQName();
          writer.writeStartElement(wrapperQName.getNamespaceURI(), wrapperQName.getLocalPart());
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
      ItemWriter itemWriter = new ItemWriter(instance.getXmlQName());
      writeModelObject(
          instance,
          item,
          ((ObjectWriter<IBoundInstanceModelFieldComplex>) this::writeFlags)
              .andThen(itemWriter::writeFieldValue));
    }

    @Override
    public void writeItemField(IBoundObject item, IBoundInstanceModelGroupedField instance) throws IOException {
      ItemWriter itemWriter = new ItemWriter(instance.getXmlQName());
      writeGroupedModelObject(
          instance,
          item,
          ((ObjectWriter<IBoundInstanceModelGroupedField>) this::writeFlags)
              .andThen(itemWriter::writeFieldValue));
    }

    @Override
    public void writeItemField(IBoundObject item, IBoundDefinitionModelFieldComplex definition) throws IOException {
      ItemWriter itemWriter = new ItemWriter(definition.getXmlQName());
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
        try {
          fieldValue.getJavaTypeAdapter().writeXmlValue(item, getObjectQName(), writer);
        } catch (XMLStreamException ex) {
          throw new IOException(ex);
        }
      }
    }

    @Override
    public void writeItemAssembly(IBoundObject item, IBoundInstanceModelAssembly instance) throws IOException {
      ItemWriter itemWriter = new ItemWriter(instance.getXmlQName());
      writeModelObject(
          instance,
          item,
          ((ObjectWriter<IBoundInstanceModelAssembly>) this::writeFlags)
              .andThen(itemWriter::writeAssemblyModel));
    }

    @Override
    public void writeItemAssembly(IBoundObject item, IBoundInstanceModelGroupedAssembly instance) throws IOException {
      ItemWriter itemWriter = new ItemWriter(instance.getXmlQName());
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
    private final QName objectQName;

    protected AbstractItemWriter(@NonNull QName qname) {
      this.objectQName = qname;
    }

    /**
     * Get the qualified name of the item's parent.
     *
     * @return the qualified name
     */
    @NonNull
    protected QName getObjectQName() {
      return objectQName;
    }
  }
}
