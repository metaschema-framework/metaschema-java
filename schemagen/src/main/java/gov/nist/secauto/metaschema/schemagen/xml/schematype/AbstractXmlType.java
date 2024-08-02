/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.schematype;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractXmlType implements IXmlType {
  @NonNull
  private final QName qname;

  public AbstractXmlType(@NonNull QName qname) {
    this.qname = qname;
  }

  @Override
  @NonNull
  public QName getQName() {
    return qname;
  }
}
