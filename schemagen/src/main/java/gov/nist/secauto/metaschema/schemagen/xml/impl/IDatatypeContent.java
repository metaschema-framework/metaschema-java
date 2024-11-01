/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.impl;

import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IDatatypeContent {
  /**
   * Get the name of the data type.
   *
   * @return the name
   */
  @NonNull
  String getTypeName();

  /**
   * Get the data type names this type depends on.
   *
   * @return the names
   */
  @NonNull
  List<String> getDependencies();

  /**
   * Write the data type to the XML stream.
   *
   * @param writer
   *          the XML stream
   * @throws XMLStreamException
   *           if an error occurred while writing to the XML stream
   */
  void write(@NonNull XMLStreamWriter writer) throws XMLStreamException;
}
