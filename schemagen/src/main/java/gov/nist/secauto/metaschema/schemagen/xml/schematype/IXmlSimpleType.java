/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.schematype;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.schemagen.xml.impl.XmlGenerationState;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IXmlSimpleType extends IXmlType {
  @NonNull
  IDataTypeAdapter<?> getDataTypeAdapter();

  @Override
  default boolean isReferenced(XmlGenerationState state) {
    // simple types are always referenced, since they are generated on demand
    return true;
  }
}
