/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml.impl;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.model.AbstractChoiceInstance;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.IChoiceInstance;
import gov.nist.secauto.metaschema.core.model.IFieldInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.IModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.AssemblyReferenceType;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.ChoiceType;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.FieldReferenceType;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.InlineAssemblyDefinitionType;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.InlineFieldDefinitionType;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;

import java.util.Map;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import nl.talsmasoftware.lazy4j.Lazy;

class XmlChoiceInstance
    extends AbstractChoiceInstance<
        IAssemblyDefinition,
        IModelInstanceAbsolute,
        INamedModelInstanceAbsolute,
        IFieldInstanceAbsolute,
        IAssemblyInstanceAbsolute> {
  @NonNull
  private final ChoiceType xmlChoice;
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
  public XmlChoiceInstance(
      @NonNull ChoiceType xmlObject,
      @NonNull IAssemblyDefinition parent) {
    super(parent);
    this.xmlChoice = xmlObject;
    this.modelContainer = ObjectUtils.notNull(Lazy.lazy(() -> new XmlModelContainer(xmlObject, this)));
  }

  @Override
  public XmlModelContainer getModelContainer() {
    return ObjectUtils.notNull(modelContainer.get());
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
  protected ChoiceType getXmlObject() {
    return xmlChoice;
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
  private static final XmlObjectParser<Pair<IChoiceInstance,
      XmlModelContainer>> XML_MODEL_PARSER
          = new XmlObjectParser<>(ObjectUtils.notNull(
              Map.ofEntries(Map.entry(new QName(IModule.XML_NAMESPACE, "assembly"), XmlChoiceInstance::handleAssembly),
                  Map.entry(new QName(IModule.XML_NAMESPACE, "define-assembly"),
                      XmlChoiceInstance::handleDefineAssembly),
                  Map.entry(new QName(IModule.XML_NAMESPACE, "field"), XmlChoiceInstance::handleField),
                  Map.entry(new QName(IModule.XML_NAMESPACE, "define-field"), XmlChoiceInstance::handleDefineField)))) {

            @Override
            protected Handler<Pair<IChoiceInstance, XmlModelContainer>> identifyHandler(XmlCursor cursor,
                XmlObject obj) {
              Handler<Pair<IChoiceInstance, XmlModelContainer>> retval;
              if (obj instanceof FieldReferenceType) {
                retval = XmlChoiceInstance::handleField;
              } else if (obj instanceof InlineFieldDefinitionType) {
                retval = XmlChoiceInstance::handleDefineField;
              } else if (obj instanceof AssemblyReferenceType) {
                retval = XmlChoiceInstance::handleAssembly;
              } else if (obj instanceof InlineAssemblyDefinitionType) {
                retval = XmlChoiceInstance::handleDefineAssembly;
              } else {
                retval = super.identifyHandler(cursor, obj);
              }
              return retval;
            }
          };

  private static void handleField( // NOPMD false positive
      @NonNull XmlObject obj,
      Pair<IChoiceInstance, XmlModelContainer> state) {
    IFieldInstanceAbsolute instance = new XmlFieldInstance(
        (FieldReferenceType) obj,
        ObjectUtils.notNull(state.getLeft()));
    ObjectUtils.notNull(state.getRight()).append(instance);
  }

  private static void handleDefineField( // NOPMD false positive
      @NonNull XmlObject obj,
      Pair<IChoiceInstance, XmlModelContainer> state) {
    IFieldInstanceAbsolute instance = new XmlInlineFieldDefinition(
        (InlineFieldDefinitionType) obj,
        ObjectUtils.notNull(state.getLeft()));
    ObjectUtils.notNull(state.getRight()).append(instance);
  }

  private static void handleAssembly( // NOPMD false positive
      @NonNull XmlObject obj,
      Pair<IChoiceInstance, XmlModelContainer> state) {
    IAssemblyInstanceAbsolute instance = new XmlAssemblyInstance(
        (AssemblyReferenceType) obj,
        ObjectUtils.notNull(state.getLeft()));
    ObjectUtils.notNull(state.getRight()).append(instance);
  }

  private static void handleDefineAssembly( // NOPMD false positive
      @NonNull XmlObject obj,
      Pair<IChoiceInstance, XmlModelContainer> state) {
    IAssemblyInstanceAbsolute instance = new XmlInlineAssemblyDefinition(
        (InlineAssemblyDefinitionType) obj,
        ObjectUtils.notNull(state.getLeft()));
    ObjectUtils.notNull(state.getRight()).append(instance);
  }

  private static class XmlModelContainer
      extends DefaultContainerModelSupport<
          IModelInstanceAbsolute,
          INamedModelInstanceAbsolute,
          IFieldInstanceAbsolute,
          IAssemblyInstanceAbsolute> {

    /**
     * Parse a choice group XMLBeans object.
     *
     * @param xmlObject
     *          the XMLBeans object
     * @param parent
     *          the parent Metaschema node, either an assembly definition or choice
     */
    public XmlModelContainer(
        @NonNull ChoiceType xmlObject,
        @NonNull IChoiceInstance parent) {
      XML_MODEL_PARSER.parse(xmlObject, Pair.of(parent, this));
    }

    public void append(@NonNull IFieldInstanceAbsolute instance) {
      QName key = instance.getXmlQName();
      getFieldInstanceMap().put(key, instance);
      getNamedModelInstanceMap().put(key, instance);
      getModelInstances().add(instance);
    }

    public void append(@NonNull IAssemblyInstanceAbsolute instance) {
      QName key = instance.getXmlQName();
      getAssemblyInstanceMap().put(key, instance);
      getNamedModelInstanceMap().put(key, instance);
      getModelInstances().add(instance);
    }
  }
}
