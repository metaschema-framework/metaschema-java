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

public abstract class AbstractXmlSimpleType
    extends AbstractXmlType
    implements IXmlSimpleType {

  @NonNull
  private final IValuedDefinition definition;

  public AbstractXmlSimpleType(@NonNull QName qname, @NonNull IValuedDefinition definition) {
    super(qname);
    this.definition = definition;
  }

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
