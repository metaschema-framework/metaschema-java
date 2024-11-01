/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.impl;

import org.codehaus.stax2.XMLStreamWriter2;

import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IDatatypeProvider {
  @NonNull
  Map<String, IDatatypeContent> getDatatypes();

  @NonNull
  Set<String> generateDatatypes(Set<String> requiredTypes, @NonNull XMLStreamWriter2 writer)
      throws XMLStreamException;

}
