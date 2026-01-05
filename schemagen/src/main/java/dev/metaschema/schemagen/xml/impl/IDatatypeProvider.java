/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen.xml.impl;

import org.codehaus.stax2.XMLStreamWriter2;

import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides XML Schema datatype definitions for schema generation.
 * <p>
 * Implementations supply datatype content that can be written to an XML Schema
 * document.
 */
public interface IDatatypeProvider {
  /**
   * Retrieves all datatypes provided by this provider.
   *
   * @return a map of datatype names to their content definitions
   */
  @NonNull
  Map<String, IDatatypeContent> getDatatypes();

  /**
   * Generates XML Schema datatype definitions for the specified required types.
   * <p>
   * Only datatypes that are both provided by this provider and included in the
   * required types set will be generated.
   *
   * @param requiredTypes
   *          the set of datatype names that are required
   * @param writer
   *          the XML stream writer to write datatype definitions to
   * @return the set of datatype names that were actually generated
   * @throws XMLStreamException
   *           if an error occurs while writing XML content
   */
  @NonNull
  Set<String> generateDatatypes(Set<String> requiredTypes, @NonNull XMLStreamWriter2 writer)
      throws XMLStreamException;

}
