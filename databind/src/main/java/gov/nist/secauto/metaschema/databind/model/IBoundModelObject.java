/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model;

import gov.nist.secauto.metaschema.databind.model.info.IItemValueHandler;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a binding of Java data to a Metaschema module construct, i.e.
 * definition, instance, field value.
 *
 * @param <ITEM>
 *          the Java type for associated bound objects
 */
public interface IBoundModelObject<ITEM> extends IItemValueHandler<ITEM> {
  /**
   * Determine if the provided XML qualified name is associated with this
   * property.
   *
   * @param qname
   *          the XML qualified name of the property being parsed
   * @return {@code true} if the instance will handle this name, or {@code false}
   *         otherwise
   */
  boolean canHandleXmlQName(@NonNull QName qname);
}
