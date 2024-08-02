/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.impl;

import com.squareup.javapoet.ClassName;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.codegen.IGeneratedClass;

import java.nio.file.Path;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Contains information about a generated class.
 */
public class DefaultGeneratedClass implements IGeneratedClass {
  @NonNull
  private final Path classFile;
  @NonNull
  private final ClassName className;

  /**
   * Construct a new class information object for a generated class.
   *
   * @param classFile
   *          the file the class was written to
   * @param className
   *          the type info for the class
   */
  public DefaultGeneratedClass(@NonNull Path classFile, @NonNull ClassName className) {
    this.classFile = ObjectUtils.requireNonNull(classFile, "classFile");
    this.className = ObjectUtils.requireNonNull(className, "className");
  }

  @Override
  @NonNull
  public Path getClassFile() {
    return classFile;
  }

  @Override
  @NonNull
  public ClassName getClassName() {
    return className;
  }
}
