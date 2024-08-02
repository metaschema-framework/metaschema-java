/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.impl;

import com.squareup.javapoet.ClassName;

import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.codegen.IGeneratedDefinitionClass;
import gov.nist.secauto.metaschema.databind.codegen.IGeneratedModuleClass;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

public class DefaultGeneratedModuleClass
    extends DefaultGeneratedClass
    implements IGeneratedModuleClass {
  @NonNull
  private final IModule module;
  @NonNull
  private final Map<IModelDefinition, IGeneratedDefinitionClass> definitionClassMap;
  @NonNull
  private final String packageName;

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
