/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen;

import com.squareup.javapoet.ClassName;

import java.nio.file.Path;

public interface IGeneratedClass {
  /**
   * The file the class was written to.
   *
   * @return the class file
   */
  Path getClassFile();

  /**
   * The type info for the class.
   *
   * @return the class's type info
   */
  ClassName getClassName();
}
