/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;

import edu.umd.cs.findbugs.annotations.Nullable;

public interface IDescribable {
  /**
   * The formal display name.
   *
   * @return the formal name or {@code null} if not defined
   */
  // from INamedModelElement
  @Nullable
  String getFormalName();

  /**
   * Get the text that describes the basic use of the element.
   *
   * @return a line of markup text or {@code null} if not defined
   */
  // from INamedModelElement
  @Nullable
  MarkupLine getDescription();

  /**
   * The resolved formal display name, which allows an instance to override a
   * definition's name.
   *
   * @return the formal name or {@code null} if not defined
   */
  // from INamedModelElement
  @Nullable
  default String getEffectiveFormalName() {
    return getFormalName();
  }

  /**
   * Get the text that describes the basic use of the element, which allows an
   * instance to override a definition's description.
   *
   * @return a line of markup text or {@code null} if not defined
   */
  // from INamedModelElement
  @Nullable
  default MarkupLine getEffectiveDescription() {
    return getDescription();
  }
}
