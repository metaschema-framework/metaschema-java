/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml.impl;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.model.AbstractFlagInstance;
import gov.nist.secauto.metaschema.core.model.IAttributable;
import gov.nist.secauto.metaschema.core.model.IFeatureDefinitionReferenceInstance;
import gov.nist.secauto.metaschema.core.model.IFlagDefinition;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.FlagReferenceType;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.UseNameType;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.Map;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

class XmlFlagInstance
    extends AbstractFlagInstance<IModelDefinition, IFlagDefinition, IFlagInstance>
    implements IFeatureDefinitionReferenceInstance<IFlagDefinition, IFlagInstance> {
  @NonNull
  private final FlagReferenceType xmlFlag;
  @Nullable
  private final Object defaultValue;

  /**
   * Constructs a new Metaschema flag instance from an XML representation bound to
   * Java objects.
   *
   * @param xmlObject
   *          the XML representation bound to Java objects
   * @param parent
   *          the field definition this object is an instance of
   */
  @SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
  public XmlFlagInstance(@NonNull FlagReferenceType xmlObject, @NonNull IModelDefinition parent) {
    super(parent);
    this.xmlFlag = xmlObject;
    this.defaultValue = xmlObject.isSetDefault()
        ? getDefinition().getJavaTypeAdapter().parse(ObjectUtils.requireNonNull(xmlObject.getDefault()))
        : null; // NOPMD needed for final variable
  }

  // ----------------------------------------
  // - Start XmlBeans driven code - CPD-OFF -
  // ----------------------------------------

  /**
   * Get the underlying XML data.
   *
   * @return the underlying XML data
   */
  protected final FlagReferenceType getXmlObject() {
    return xmlFlag;
  }

  @Override
  public String getFormalName() {
    return getXmlObject().isSetFormalName() ? getXmlObject().getFormalName() : null;
  }

  @SuppressWarnings("null")
  @Override
  public MarkupLine getDescription() {
    return getXmlObject().isSetDescription() ? MarkupStringConverter.toMarkupString(getXmlObject().getDescription())
        : null;
  }

  @Override
  public Map<IAttributable.Key, Set<String>> getProperties() {
    return ModelFactory.toProperties(CollectionUtil.listOrEmpty(getXmlObject().getPropList()));
  }

  @SuppressWarnings("null")
  @Override
  public final String getName() {
    return getXmlObject().getRef();
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

  @SuppressWarnings("null")
  @Override
  public MarkupMultiline getRemarks() {
    return getXmlObject().isSetRemarks() ? MarkupStringConverter.toMarkupString(getXmlObject().getRemarks()) : null;
  }

  @Override
  public boolean isRequired() {
    return getXmlObject().isSetRequired() ? getXmlObject().getRequired()
        : DEFAULT_FLAG_REQUIRED;
  }

  // -------------------------------------
  // - End XmlBeans driven code - CPD-ON -
  // -------------------------------------
}
