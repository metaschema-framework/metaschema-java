/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.codegen;

import com.squareup.javapoet.ClassName;

import java.nio.file.Path;

/**
 * Provides information about a generated Java class file.
 * <p>
 * This interface represents a Java class that has been generated during
 * Metaschema processing, providing access to both the physical file location
 * and the type information for the generated class.
 */
public interface IGeneratedClass {
  /**
   * Get the file the class was written to.
   *
   * @return the class file path
   */
  Path getClassFile();

  /**
   * Get the type info for the class.
   *
   * @return the class's type info
   */
  ClassName getClassName();
}
