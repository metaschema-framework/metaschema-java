/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor.command;

public interface ExtraArgument {
  String getName();

  boolean isRequired();

  /**
   * The allowed number of arguments of this type.
   *
   * @return a positive integer value
   */
  int getNumber();
}
