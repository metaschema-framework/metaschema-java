/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.impl;

import com.squareup.javapoet.ClassName;

import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.databind.codegen.IGeneratedDefinitionClass;

import java.nio.file.Path;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Contains information about a generated class for a Module definition.
 */
public class DefaultGeneratedDefinitionClass
    extends DefaultGeneratedClass
    implements IGeneratedDefinitionClass {
  @NonNull
  private final IModelDefinition definition;

  /**
   * Construct a new class information object for a generated class.
   *
   * @param classFile
   *          the file the class was written to
   * @param className
   *          the type info for the class
   * @param definition
   *          the definition on which the class was based
   */
  public DefaultGeneratedDefinitionClass(@NonNull Path classFile, @NonNull ClassName className,
      @NonNull IModelDefinition definition) {
    super(classFile, className);
    this.definition = definition;
  }

  @Override
  public IModelDefinition getDefinition() {
    return definition;
  }

  @Override
  public boolean isRootClass() {
    return definition instanceof IAssemblyDefinition && ((IAssemblyDefinition) definition).isRoot();
  }
}
