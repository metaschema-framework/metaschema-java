/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml.impl;

import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.IChoiceGroupInstance;
import gov.nist.secauto.metaschema.core.model.IChoiceInstance;
import gov.nist.secauto.metaschema.core.model.IContainerModelAssemblySupport;
import gov.nist.secauto.metaschema.core.model.IFieldInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.IModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.xml.XmlModuleConstants;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.AssemblyModelType;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.AssemblyReferenceType;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.ChoiceType;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.FieldReferenceType;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.GroupedChoiceType;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.InlineAssemblyDefinitionType;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.InlineFieldDefinitionType;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;

import java.util.Map;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Used to construct the model of an assmebly defintion based on XMLBeans-based
 * data.
 */
public class XmlAssemblyModelContainerSupport
    extends DefaultContainerModelAssemblySupport<
        IModelInstanceAbsolute,
        INamedModelInstanceAbsolute,
        IFieldInstanceAbsolute,
        IAssemblyInstanceAbsolute,
        IChoiceInstance,
        IChoiceGroupInstance> {
  @SuppressWarnings("PMD.UseConcurrentHashMap")
  @NonNull
  private static final XmlObjectParser<Pair<IAssemblyDefinition, XmlAssemblyModelContainerSupport>> XML_MODEL_PARSER
      = new XmlObjectParser<>(ObjectUtils.notNull(
          Map.ofEntries(
              Map.entry(XmlModuleConstants.ASSEMBLY_QNAME, XmlAssemblyModelContainerSupport::handleAssemmbly),
              Map.entry(XmlModuleConstants.DEFINE_ASSEMBLY_QNAME,
                  XmlAssemblyModelContainerSupport::handleDefineAssembly),
              Map.entry(XmlModuleConstants.FIELD_QNAME, XmlAssemblyModelContainerSupport::handleField),
              Map.entry(XmlModuleConstants.DEFINE_FIELD_QNAME, XmlAssemblyModelContainerSupport::handleDefineField),
              Map.entry(XmlModuleConstants.CHOICE_QNAME, XmlAssemblyModelContainerSupport::handleChoice),
              Map.entry(XmlModuleConstants.CHOICE_GROUP_QNAME, XmlAssemblyModelContainerSupport::handleChoiceGroup)))) {

        @Override
        protected Handler<Pair<IAssemblyDefinition, XmlAssemblyModelContainerSupport>>
            identifyHandler(XmlCursor cursor, XmlObject obj) {
          Handler<Pair<IAssemblyDefinition, XmlAssemblyModelContainerSupport>> retval;
          if (obj instanceof FieldReferenceType) {
            retval = XmlAssemblyModelContainerSupport::handleField;
          } else if (obj instanceof InlineFieldDefinitionType) {
            retval = XmlAssemblyModelContainerSupport::handleDefineField;
          } else if (obj instanceof AssemblyReferenceType) {
            retval = XmlAssemblyModelContainerSupport::handleAssemmbly;
          } else if (obj instanceof InlineAssemblyDefinitionType) {
            retval = XmlAssemblyModelContainerSupport::handleDefineAssembly;
          } else if (obj instanceof ChoiceType) {
            retval = XmlAssemblyModelContainerSupport::handleChoice;
          } else if (obj instanceof GroupedChoiceType) {
            retval = XmlAssemblyModelContainerSupport::handleChoiceGroup;
          } else {
            retval = super.identifyHandler(cursor, obj);
          }
          return retval;
        }
      };

  /**
   * Parse an assembly XMLBeans object.
   *
   * @param xmlObject
   *          the XMLBeans assembly model object, which may be {@code null}
   * @param parent
   *          the parent assembly definition, either an assembly definition or
   *          choice
   * @return the model container
   */
  @SuppressWarnings("PMD.ShortMethodName")
  public static IContainerModelAssemblySupport<
      IModelInstanceAbsolute,
      INamedModelInstanceAbsolute,
      IFieldInstanceAbsolute,
      IAssemblyInstanceAbsolute,
      IChoiceInstance,
      IChoiceGroupInstance> of(
          @Nullable AssemblyModelType xmlObject,
          @NonNull IAssemblyDefinition parent) {
    return xmlObject == null
        ? IContainerModelAssemblySupport.empty()
        : XML_MODEL_PARSER
            .parse(ObjectUtils.notNull(xmlObject), Pair.of(parent, new XmlAssemblyModelContainerSupport()))
            .getRight();
  }

  private static void handleField( // NOPMD false positive
      @NonNull XmlObject obj,
      Pair<IAssemblyDefinition, XmlAssemblyModelContainerSupport> state) {
    IFieldInstanceAbsolute instance = new XmlFieldInstance(
        (FieldReferenceType) obj,
        ObjectUtils.notNull(state.getLeft()));
    ObjectUtils.notNull(state.getRight()).append(instance);
  }

  private static void handleDefineField( // NOPMD false positive
      @NonNull XmlObject obj,
      Pair<IAssemblyDefinition, XmlAssemblyModelContainerSupport> state) {
    IFieldInstanceAbsolute instance = new XmlInlineFieldDefinition(
        (InlineFieldDefinitionType) obj,
        ObjectUtils.notNull(state.getLeft()));
    ObjectUtils.notNull(state.getRight()).append(instance);
  }

  private static void handleAssemmbly( // NOPMD false positive
      @NonNull XmlObject obj,
      Pair<IAssemblyDefinition, XmlAssemblyModelContainerSupport> state) {
    IAssemblyInstanceAbsolute instance = new XmlAssemblyInstance(
        (AssemblyReferenceType) obj,
        ObjectUtils.notNull(state.getLeft()));
    ObjectUtils.notNull(state.getRight()).append(instance);
  }

  private static void handleDefineAssembly( // NOPMD false positive
      @NonNull XmlObject obj,
      Pair<IAssemblyDefinition, XmlAssemblyModelContainerSupport> state) {
    IAssemblyInstanceAbsolute instance = new XmlInlineAssemblyDefinition(
        (InlineAssemblyDefinitionType) obj,
        ObjectUtils.notNull(state.getLeft()));
    ObjectUtils.notNull(state.getRight()).append(instance);
  }

  private static void handleChoice( // NOPMD false positive
      @NonNull XmlObject obj,
      Pair<IAssemblyDefinition, XmlAssemblyModelContainerSupport> state) {
    XmlChoiceInstance instance = new XmlChoiceInstance(
        (ChoiceType) obj,
        ObjectUtils.notNull(state.getLeft()));
    ObjectUtils.notNull(state.getRight()).append(instance);
  }

  private static void handleChoiceGroup( // NOPMD false positive
      @NonNull XmlObject obj,
      Pair<IAssemblyDefinition, XmlAssemblyModelContainerSupport> state) {
    XmlChoiceGroupInstance instance = new XmlChoiceGroupInstance(
        (GroupedChoiceType) obj,
        ObjectUtils.notNull(state.getLeft()));
    XmlAssemblyModelContainerSupport container = ObjectUtils.notNull(state.getRight());

    String groupAsName = instance.getGroupAsName();
    if (groupAsName == null) {
      String location = XmlObjectParser.toLocation(obj);
      String locationCtx = location == null ? "" : " at location " + location;
      throw new IllegalArgumentException(
          String.format("Missing group-as for a choice group within the definition '%s'%s.",
              instance.getContainingDefinition().getName(),
              locationCtx));
    }
    container.getChoiceGroupInstanceMap().put(groupAsName, instance);
    container.getModelInstances().add(instance);
  }

  /**
   * Adds the provided instance to the tail of the model.
   *
   * @param instance
   *          the instance to append
   */
  public void append(@NonNull IFieldInstanceAbsolute instance) {
    QName key = instance.getXmlQName();
    getFieldInstanceMap().put(key, instance);
    getNamedModelInstanceMap().put(key, instance);
    getModelInstances().add(instance);
  }

  /**
   * Adds the provided instance to the tail of the model.
   *
   * @param instance
   *          the instance to append
   */
  public void append(@NonNull IAssemblyInstanceAbsolute instance) {
    QName key = instance.getXmlQName();
    getAssemblyInstanceMap().put(key, instance);
    getNamedModelInstanceMap().put(key, instance);
    getModelInstances().add(instance);
  }

  /**
   * Adds the provided instance to the tail of the model.
   *
   * @param instance
   *          the instance to append
   */
  public void append(@NonNull IChoiceInstance instance) {
    getChoiceInstances().add(instance);
    getModelInstances().add(instance);
  }
}
