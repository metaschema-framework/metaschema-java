/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * This marker interface indicates that the instance has a flag, field, or
 * assembly name associated with it which will be used in JSON/YAML or XML to
 * identify the data.
 *
 */
public interface INamedInstance extends INamedModelElement, IAttributable, IInstance {
  /**
   * Retrieve the definition of this instance.
   *
   * @return the corresponding definition
   */
  @NonNull
  IDefinition getDefinition();

  /**
   * This represents the qualified name of a referenced definition.
   *
   * @return the qualified name
   * @see IDefinition#getDefinitionQName()
   */
  @NonNull
  QName getReferencedDefinitionQName();
}
