/*
 * Portions of this software was developed by employees of the National Institute
 * of Standards and Technology (NIST), an agency of the Federal Government and is
 * being made available as a public service. Pursuant to title 17 United States
 * Code Section 105, works of NIST employees are not subject to copyright
 * protection in the United States. This software may be subject to foreign
 * copyright. Permission in the United States and in foreign countries, to the
 * extent that NIST may hold copyright, to use, copy, modify, create derivative
 * works, and distribute this software and its documentation without fee is hereby
 * granted on a non-exclusive basis, provided that this notice and disclaimer
 * of warranty appears in all copies.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS' WITHOUT ANY WARRANTY OF ANY KIND, EITHER
 * EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT LIMITED TO, ANY WARRANTY
 * THAT THE SOFTWARE WILL CONFORM TO SPECIFICATIONS, ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND FREEDOM FROM
 * INFRINGEMENT, AND ANY WARRANTY THAT THE DOCUMENTATION WILL CONFORM TO THE
 * SOFTWARE, OR ANY WARRANTY THAT THE SOFTWARE WILL BE ERROR FREE.  IN NO EVENT
 * SHALL NIST BE LIABLE FOR ANY DAMAGES, INCLUDING, BUT NOT LIMITED TO, DIRECT,
 * INDIRECT, SPECIAL OR CONSEQUENTIAL DAMAGES, ARISING OUT OF, RESULTING FROM,
 * OR IN ANY WAY CONNECTED WITH THIS SOFTWARE, WHETHER OR NOT BASED UPON WARRANTY,
 * CONTRACT, TORT, OR OTHERWISE, WHETHER OR NOT INJURY WAS SUSTAINED BY PERSONS OR
 * PROPERTY OR OTHERWISE, AND WHETHER OR NOT LOSS WAS SUSTAINED FROM, OR AROSE OUT
 * OF THE RESULTS OF, OR USE OF, THE SOFTWARE OR SERVICES PROVIDED HEREUNDER.
 */

package gov.nist.secauto.metaschema.core.model.xml.impl;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.model.AbstractFieldInstance;
import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IModelContainer;
import gov.nist.secauto.metaschema.core.model.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.core.model.MetaschemaModelConstants;
import gov.nist.secauto.metaschema.core.model.XmlGroupAsBehavior;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.FieldReferenceType;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.UseNameType;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

class XmlFieldInstance
    extends AbstractFieldInstance {
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
  @SuppressWarnings("PMD.NullAssignment")
  public XmlFieldInstance(
      @NonNull FieldReferenceType xmlObject,
      @NonNull IModelContainer container) {
    super(container);
    this.xmlObject = xmlObject;
    this.defaultValue = xmlObject.isSetDefault()
        ? getDefinition().getJavaTypeAdapter().parse(ObjectUtils.requireNonNull(xmlObject.getDefault()))
        : null;
  }

  @Override
  public IFieldDefinition getDefinition() {
    // this will always be not null
    return ObjectUtils.notNull(getContainingModule().getScopedFieldDefinitionByName(getName()));
  }

  // ----------------------------------------
  // - Start XmlBeans driven code - CPD-OFF -
  // ----------------------------------------

  /**
   * Get the underlying XML data.
   *
   * @return the underlying XML data
   */
  protected FieldReferenceType getXmlObject() {
    return xmlObject;
  }

  @Override
  public boolean isInXmlWrapped() {
    boolean retval;
    if (getDefinition().getJavaTypeAdapter().isUnrappedValueAllowedInXml()) {
      // default value
      retval = MetaschemaModelConstants.DEFAULT_FIELD_IN_XML_WRAPPED;
      if (getXmlObject().isSetInXml()) {
        retval = getXmlObject().getInXml().booleanValue();
      }
    } else {
      // All other data types get "wrapped"
      retval = true;
    }
    return retval;
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
  public Map<QName, Set<String>> getProperties() {
    return ModelFactory.toProperties(CollectionUtil.listOrEmpty(getXmlObject().getPropList()));
  }

  @Override
  public String getName() {
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