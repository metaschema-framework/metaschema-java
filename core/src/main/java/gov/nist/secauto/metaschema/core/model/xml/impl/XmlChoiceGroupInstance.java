/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml.impl;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.model.AbstractChoiceGroupInstance;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstanceGrouped;
import gov.nist.secauto.metaschema.core.model.IChoiceGroupInstance;
import gov.nist.secauto.metaschema.core.model.IFieldInstanceGrouped;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceGrouped;
import gov.nist.secauto.metaschema.core.model.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.core.model.XmlGroupAsBehavior;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.GroupedAssemblyReferenceType;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.GroupedChoiceType;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.GroupedFieldReferenceType;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.GroupedInlineAssemblyDefinitionType;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.GroupedInlineFieldDefinitionType;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;

import java.util.Map;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import nl.talsmasoftware.lazy4j.Lazy;

class XmlChoiceGroupInstance
    extends AbstractChoiceGroupInstance<
        IAssemblyDefinition,
        INamedModelInstanceGrouped,
        IFieldInstanceGrouped,
        IAssemblyInstanceGrouped> {
  @NonNull
  private final GroupedChoiceType xmlObject;
  @NonNull
  private final Lazy<XmlModelContainer> modelContainer;

  /**
   * Constructs a mutually exclusive choice between two possible objects.
   *
   * @param xmlObject
   *          the XML for the choice definition bound to Java objects
   * @param parent
   *          the parent container, either a choice or assembly
   */
  public XmlChoiceGroupInstance(
      @NonNull GroupedChoiceType xmlObject,
      @NonNull IAssemblyDefinition parent) {
    super(parent);
    this.xmlObject = xmlObject;
    this.modelContainer = ObjectUtils.notNull(Lazy.lazy(() -> new XmlModelContainer(xmlObject, this)));
  }

  @Override
  public XmlModelContainer getModelContainer() {
    return ObjectUtils.notNull(modelContainer.get());
  }

  @Override
  public IAssemblyDefinition getOwningDefinition() {
    return getParentContainer();
  }

  // ----------------------------------------
  // - Start XmlBeans driven code - CPD-OFF -
  // ----------------------------------------

  /**
   * Get the underlying XML data.
   *
   * @return the underlying XML data
   */
  @NonNull
  protected GroupedChoiceType getXmlObject() {
    return xmlObject;
  }

  @Override
  public String getJsonDiscriminatorProperty() {
    return getXmlObject().isSetDiscriminator()
        ? ObjectUtils.requireNonNull(getXmlObject().getDiscriminator())
        : DEFAULT_JSON_DISCRIMINATOR_PROPERTY_NAME;
  }

  @Override
  public String getJsonKeyFlagInstanceName() {
    return getXmlObject().isSetJsonKey() ? getXmlObject().getJsonKey().getFlagRef() : null;
  }

  @Override
  public String getGroupAsName() {
    return getXmlObject().getGroupAs().getName();
  }

  @Override
  public int getMinOccurs() {
    return XmlModelParser.getMinOccurs(getXmlObject().getMinOccurs());
  }

  @Override
  public int getMaxOccurs() {
    return XmlModelParser.getMaxOccurs(getXmlObject().getMaxOccurs());
  }

  @Override
  public JsonGroupAsBehavior getJsonGroupAsBehavior() {
    return XmlModelParser.getJsonGroupAsBehavior(getXmlObject().getGroupAs());
  }

  @Override
  public XmlGroupAsBehavior getXmlGroupAsBehavior() {
    return XmlModelParser.getXmlGroupAsBehavior(getXmlObject().getGroupAs());
  }

  @Override
  public MarkupMultiline getRemarks() {
    // remarks not supported
    return null;
  }

  // -------------------------------------
  // - End XmlBeans driven code - CPD-ON -
  // -------------------------------------

  @SuppressWarnings("PMD.UseConcurrentHashMap")
  @NonNull
  private static final XmlObjectParser<Pair<IChoiceGroupInstance, XmlModelContainer>> XML_MODEL_PARSER
      = new XmlObjectParser<>(ObjectUtils.notNull(
          Map.ofEntries(
              Map.entry(new QName(IModule.XML_NAMESPACE, "assembly"),
                  XmlChoiceGroupInstance::handleAssembly),
              Map.entry(new QName(IModule.XML_NAMESPACE, "define-assembly"),
                  XmlChoiceGroupInstance::handleDefineAssembly),
              Map.entry(new QName(IModule.XML_NAMESPACE, "field"),
                  XmlChoiceGroupInstance::handleField),
              Map.entry(new QName(IModule.XML_NAMESPACE, "define-field"),
                  XmlChoiceGroupInstance::handleDefineField)))) {

        @Override
        protected Handler<Pair<IChoiceGroupInstance, XmlModelContainer>>
            identifyHandler(XmlCursor cursor, XmlObject obj) {
          Handler<Pair<IChoiceGroupInstance, XmlModelContainer>> retval;
          if (obj instanceof GroupedFieldReferenceType) {
            retval = XmlChoiceGroupInstance::handleField;
          } else if (obj instanceof GroupedInlineFieldDefinitionType) {
            retval = XmlChoiceGroupInstance::handleDefineField;
          } else if (obj instanceof GroupedAssemblyReferenceType) {
            retval = XmlChoiceGroupInstance::handleAssembly;
          } else if (obj instanceof GroupedInlineAssemblyDefinitionType) {
            retval = XmlChoiceGroupInstance::handleDefineAssembly;
          } else {
            retval = super.identifyHandler(cursor, obj);
          }
          return retval;
        }
      };

  private static void handleField( // NOPMD false positive
      @NonNull XmlObject obj,
      Pair<IChoiceGroupInstance, XmlModelContainer> state) {
    IFieldInstanceGrouped instance = new XmlGroupedFieldInstance(
        (GroupedFieldReferenceType) obj,
        ObjectUtils.notNull(state.getLeft()));
    ObjectUtils.notNull(state.getRight()).append(instance);
  }

  private static void handleDefineField( // NOPMD false positive
      @NonNull XmlObject obj,
      Pair<IChoiceGroupInstance, XmlModelContainer> state) {
    IFieldInstanceGrouped instance = new XmlGroupedInlineFieldDefinition(
        (GroupedInlineFieldDefinitionType) obj,
        ObjectUtils.notNull(state.getLeft()));
    ObjectUtils.notNull(state.getRight()).append(instance);
  }

  private static void handleAssembly( // NOPMD false positive
      @NonNull XmlObject obj,
      Pair<IChoiceGroupInstance, XmlModelContainer> state) {
    IAssemblyInstanceGrouped instance = new XmlGroupedAssemblyInstance(
        (GroupedAssemblyReferenceType) obj,
        ObjectUtils.notNull(state.getLeft()));
    ObjectUtils.notNull(state.getRight()).append(instance);
  }

  private static void handleDefineAssembly( // NOPMD false positive
      @NonNull XmlObject obj,
      Pair<IChoiceGroupInstance, XmlModelContainer> state) {
    IAssemblyInstanceGrouped instance = new XmlGroupedInlineAssemblyDefinition(
        (GroupedInlineAssemblyDefinitionType) obj,
        ObjectUtils.notNull(state.getLeft()));
    ObjectUtils.notNull(state.getRight()).append(instance);
  }

  private static class XmlModelContainer
      extends DefaultGroupedModelContainerSupport<
          INamedModelInstanceGrouped,
          IFieldInstanceGrouped,
          IAssemblyInstanceGrouped> {

    /**
     * Parse a choice group XMLBeans object.
     *
     * @param xmlObject
     *          the XMLBeans object
     * @param parent
     *          the parent Metaschema node, either an assembly definition or choice
     */
    public XmlModelContainer(
        @NonNull GroupedChoiceType xmlObject,
        @NonNull IChoiceGroupInstance parent) {
      XML_MODEL_PARSER.parse(xmlObject, Pair.of(parent, this));
    }

    public void append(@NonNull IFieldInstanceGrouped instance) {
      QName key = instance.getXmlQName();
      getFieldInstanceMap().put(key, instance);
      getNamedModelInstanceMap().put(key, instance);
    }

    public void append(@NonNull IAssemblyInstanceGrouped instance) {
      QName key = instance.getXmlQName();
      getAssemblyInstanceMap().put(key, instance);
      getNamedModelInstanceMap().put(key, instance);
    }
  }
}
