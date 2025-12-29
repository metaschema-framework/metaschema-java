/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.impl.schematype;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides a common base implementation for XML schema type elements.
 * <p>
 * This abstract class serves as the foundation for all XML type
 * representations, maintaining the qualified name that identifies the type.
 */
public abstract class AbstractXmlType implements IXmlType {
  @NonNull
  private final QName qname;

  /**
   * Construct a new XML type.
   *
   * @param qname
   *          the qualified name for the type
   */
  public AbstractXmlType(@NonNull QName qname) {
    this.qname = qname;
  }

  @Override
  @NonNull
  public QName getQName() {
    return qname;
  }
}
