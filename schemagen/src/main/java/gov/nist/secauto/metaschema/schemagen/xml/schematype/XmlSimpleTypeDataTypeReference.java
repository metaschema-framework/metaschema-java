/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.schematype;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.schemagen.xml.impl.XmlGenerationState;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

// TODO: remove, since this doesn't represent a type
public class XmlSimpleTypeDataTypeReference
    extends AbstractXmlType
    implements IXmlSimpleType {
  @NonNull
  private final IDataTypeAdapter<?> dataTypeAdapter;

  public XmlSimpleTypeDataTypeReference(
      @NonNull QName typeName,
      @NonNull IDataTypeAdapter<?> dataTypeAdapter) {
    super(typeName);
    this.dataTypeAdapter = dataTypeAdapter;
  }

  @Override
  public IDataTypeAdapter<?> getDataTypeAdapter() {
    return dataTypeAdapter;
  }

  @Override
  public void generate(XmlGenerationState state) {
    // do nothing, this is a direct reference to the underlying Module data type
    // the type is generated for the built-in type by the data type manager
  }

  @Override
  public boolean isInline(XmlGenerationState state) {
    // these types are never inlined
    return false;
  }

  @Override
  public boolean isGeneratedType(XmlGenerationState state) {
    // these types are not generated, since they are handled by the datatype manager
    return false;
  }

}
