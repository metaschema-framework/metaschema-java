/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.codegen.impl;

import com.squareup.javapoet.ClassName;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

import dev.metaschema.core.model.IModelDefinition;
import dev.metaschema.core.model.IModule;
import dev.metaschema.core.util.CollectionUtil;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.codegen.IGeneratedDefinitionClass;
import dev.metaschema.databind.codegen.IGeneratedModuleClass;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Contains information about a generated class representing a Metaschema
 * module.
 */
public class DefaultGeneratedModuleClass
    extends DefaultGeneratedClass
    implements IGeneratedModuleClass {
  @NonNull
  private final IModule module;
  @NonNull
  private final Map<IModelDefinition, IGeneratedDefinitionClass> definitionClassMap;
  @NonNull
  private final String packageName;

  /**
   * Construct a new generated module class.
   *
   * @param module
   *          the Metaschema module this class represents
   * @param className
   *          the type info for the generated class
   * @param classFile
   *          the file the class was written to
   * @param definitionClassMap
   *          a map of definitions to their generated classes
   * @param packageName
   *          the Java package name for this module
   */
  public DefaultGeneratedModuleClass(
      @NonNull IModule module,
      @NonNull ClassName className,
      @NonNull Path classFile,
      @NonNull Map<IModelDefinition, IGeneratedDefinitionClass> definitionClassMap,
      @NonNull String packageName) {
    super(classFile, className);
    this.module = module;
    this.definitionClassMap = CollectionUtil.unmodifiableMap(definitionClassMap);
    this.packageName = packageName;
  }

  @Override
  public IModule getModule() {
    return module;
  }

  @Override
  public Collection<IGeneratedDefinitionClass> getGeneratedDefinitionClasses() {
    return ObjectUtils.notNull(definitionClassMap.values());
  }

  @Override
  public String getPackageName() {
    return packageName;
  }
}
