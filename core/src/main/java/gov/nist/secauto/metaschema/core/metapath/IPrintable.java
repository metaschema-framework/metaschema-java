/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IPrintable {
  /**
   * Get the string value.
   *
   * @return the string value
   */
  @NonNull
  String asString();
}
