/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml.impl;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.model.AbstractAssemblyInstance;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.IAttributable;
import gov.nist.secauto.metaschema.core.model.IContainerModel;
import gov.nist.secauto.metaschema.core.model.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.core.model.XmlGroupAsBehavior;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.AssemblyReferenceType;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.MarkupLineDatatype;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.UseNameType;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import java.util.Map;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;

class XmlAssemblyInstance
    extends AbstractAssemblyInstance<
        IContainerModel,
        IAssemblyDefinition,
        IAssemblyInstanceAbsolute,
        IAssemblyDefinition>
    implements IAssemblyInstanceAbsolute {
  @NonNull
  private final AssemblyReferenceType xmlObject;

  /**
   * Constructs a new Metaschema Assembly instance definition from an XML
   * representation bound to Java objects.
   *
   * @param xmlObject
   *          the XML representation bound to Java objects
   * @param parent
   *          the parent container, either a choice or assembly
   */
  public XmlAssemblyInstance(
      @NonNull AssemblyReferenceType xmlObject,
      @NonNull IContainerModel parent) {
    super(parent);
    this.xmlObject = xmlObject;
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
  protected AssemblyReferenceType getXmlObject() {
    return xmlObject;
  }

  @Override
  public String getFormalName() {
    return getXmlObject().isSetFormalName() ? getXmlObject().getFormalName() : null;
  }

  @Override
  public Map<IAttributable.Key, Set<String>> getProperties() {
    return ModelFactory.toProperties(CollectionUtil.listOrEmpty(getXmlObject().getPropList()));
  }

  @Override
  public MarkupLine getDescription() {
    MarkupLine retval = null;
    if (getXmlObject().isSetDescription()) {
      MarkupLineDatatype description = getXmlObject().getDescription();
      assert description != null;
      retval = MarkupStringConverter.toMarkupString(description);
    }
    return retval;
  }

  @SuppressWarnings("null")
  @Override
  public String getName() {
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
