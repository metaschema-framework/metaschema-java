/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml.impl;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.model.AbstractFieldInstance;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IAttributable;
import gov.nist.secauto.metaschema.core.model.IContainerModel;
import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.core.model.XmlGroupAsBehavior;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.FieldReferenceType;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.UseNameType;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.Map;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

class XmlFieldInstance
    extends AbstractFieldInstance<IContainerModel, IFieldDefinition, IFieldInstanceAbsolute, IAssemblyDefinition>
    implements IFieldInstanceAbsolute {
  @NonNull
  private final FieldReferenceType xmlObject;
  @Nullable
  private final Object defaultValue;

  /**
   * Constructs a new Metaschema field instance definition from an XML
   * representation bound to Java objects.
   *
   * @param xmlObject
   *          the XML representation bound to Java objects
   * @param container
   *          the parent container, either a choice or assembly
   */
  @SuppressWarnings({ "PMD.NullAssignment", "PMD.ConstructorCallsOverridableMethod" })
  public XmlFieldInstance(
      @NonNull FieldReferenceType xmlObject,
      @NonNull IContainerModel container) {
    super(container);
    this.xmlObject = xmlObject;
    this.defaultValue = xmlObject.isSetDefault()
        ? getDefinition().getJavaTypeAdapter().parse(ObjectUtils.requireNonNull(xmlObject.getDefault()))
        : null;
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
  protected final FieldReferenceType getXmlObject() {
    return xmlObject;
  }

  @Override
  public boolean isInXmlWrapped() {
    return getXmlObject().getInXml();
  }

  @Override
  public String getFormalName() {
    return getXmlObject().isSetFormalName() ? getXmlObject().getFormalName() : null;
  }

  @Override
  public MarkupLine getDescription() {
    return getXmlObject().isSetDescription()
        ? MarkupStringConverter.toMarkupString(ObjectUtils.notNull(getXmlObject().getDescription()))
        : null;
  }

  @Override
  public Map<IAttributable.Key, Set<String>> getProperties() {
    return ModelFactory.toProperties(CollectionUtil.listOrEmpty(getXmlObject().getPropList()));
  }

  @Override
  public final String getName() {
    return ObjectUtils.requireNonNull(getXmlObject().getRef());
  }

  @Override
  public String getUseName() {
    return getXmlObject().isSetUseName() ? getXmlObject().getUseName().getStringValue() : null;
  }

  @Override
  public Integer getUseIndex() {
    Integer retval = null;
    if (getXmlObject().isSetUseName()) {
      UseNameType useName = getXmlObject().getUseName();
      if (useName.isSetIndex()) {
        retval = useName.getIndex().intValue();
      }
    }
    return retval;
  }

  @Override
  public Object getDefaultValue() {
    return defaultValue;
  }

  @Override
  public String getGroupAsName() {
    return getXmlObject().isSetGroupAs() ? getXmlObject().getGroupAs().getName() : null;
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

  @SuppressWarnings("null")
  @Override
  public MarkupMultiline getRemarks() {
    return getXmlObject().isSetRemarks() ? MarkupStringConverter.toMarkupString(getXmlObject().getRemarks()) : null;
  }

  // -------------------------------------
  // - End XmlBeans driven code - CPD-ON -
  // -------------------------------------
}
