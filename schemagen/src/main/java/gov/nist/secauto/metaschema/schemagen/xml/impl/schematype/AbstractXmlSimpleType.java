/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.impl.schematype;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.model.IValuedDefinition;
import gov.nist.secauto.metaschema.schemagen.xml.impl.XmlGenerationState;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides a common base implementation for XML simple type schema elements.
 * <p>
 * This class represents a simple type in an XML schema that corresponds to a
 * Metaschema valued definition (field or flag).
 */
public abstract class AbstractXmlSimpleType
    extends AbstractXmlType
    implements IXmlSimpleType {

  @NonNull
  private final IValuedDefinition definition;

  /**
   * Construct a new simple type.
   *
   * @param qname
   *          the qualified name for the type
   * @param definition
   *          the valued definition this type represents
   */
  public AbstractXmlSimpleType(@NonNull QName qname, @NonNull IValuedDefinition definition) {
    super(qname);
    this.definition = definition;
  }

  /**
   * Get the valued definition this type represents.
   *
   * @return the valued definition
   */
  @NonNull
  public IValuedDefinition getDefinition() {
    return definition;
  }

  @Override
  public IDataTypeAdapter<?> getDataTypeAdapter() {
    return getDefinition().getJavaTypeAdapter();
  }

  @Override
  public boolean isInline(XmlGenerationState state) {
    return state.isInline(getDefinition());
  }

  @Override
  public boolean isGeneratedType(XmlGenerationState state) {
    // these types are a restriction on a base type
    return true;
  }
}
