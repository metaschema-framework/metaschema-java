/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.datatype;

import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IDatatypeContent {
  @NonNull
  String getTypeName();

  @NonNull
  List<String> getDependencies();

  void write(@NonNull XMLStreamWriter writer) throws XMLStreamException;
}
