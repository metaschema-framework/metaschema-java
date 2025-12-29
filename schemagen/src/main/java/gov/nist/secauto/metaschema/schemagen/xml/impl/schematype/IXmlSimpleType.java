/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.impl.schematype;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.schemagen.xml.impl.XmlGenerationState;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents an XML Schema simple type derived from a Metaschema data type.
 * <p>
 * Simple types are used to represent scalar values in XML Schema, such as
 * strings, numbers, and dates. They may include restrictions like enumerated
 * values or pattern constraints.
 */
public interface IXmlSimpleType extends IXmlType {
  /**
   * Get the data type adapter that handles value conversion for this simple type.
   *
   * @return the data type adapter
   */
  @NonNull
  IDataTypeAdapter<?> getDataTypeAdapter();

  @Override
  default boolean isReferenced(XmlGenerationState state) {
    // simple types are always referenced, since they are generated on demand
    return true;
  }
}
