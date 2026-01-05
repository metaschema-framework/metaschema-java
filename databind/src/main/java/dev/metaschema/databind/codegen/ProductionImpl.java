/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.codegen;

import dev.metaschema.core.model.IModule;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.codegen.typeinfo.IMetaschemaClassFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Default implementation of {@link IProduction} that tracks generated classes
 * for modules and packages.
 */
class ProductionImpl implements IProduction {

  @NonNull
  private final Map<IModule, IGeneratedModuleClass> moduleToProductionMap // NOPMD - immutable
      = new HashMap<>();
  @NonNull
  private final Map<String, IPackageProduction> packageNameToProductionMap // NOPMD - immutable
      = new HashMap<>();

  /**
   * Add a module and its imports to this production.
   *
   * @param module
   *          the module to add
   * @param classFactory
   *          the class factory to use for generation
   * @param targetDirectory
   *          the target directory for generated classes
   * @throws IOException
   *           if an error occurs during generation
   */
  public void addModule(
      @NonNull IModule module,
      @NonNull IMetaschemaClassFactory classFactory,
      @NonNull Path targetDirectory) throws IOException {
    for (IModule importedModule : module.getImportedModules()) {
      assert importedModule != null;
      addModule(importedModule, classFactory, targetDirectory);
    }

    if (moduleToProductionMap.get(module) == null) {
      IGeneratedModuleClass metaschemaClass = classFactory.generateClass(module, targetDirectory);
      moduleToProductionMap.put(module, metaschemaClass);
    }
  }

  /**
   * Add a package to this production.
   *
   * @param metadata
   *          the package metadata
   * @param classFactory
   *          the class factory to use for generation
   * @param targetDirectory
   *          the target directory for generated classes
   * @return the generated package production
   * @throws IOException
   *           if an error occurs during generation
   */
  protected IPackageProduction addPackage(
      @NonNull PackageMetadata metadata,
      @NonNull IMetaschemaClassFactory classFactory,
      @NonNull Path targetDirectory)
      throws IOException {
    String javaPackage = metadata.getPackageName();

    IPackageProduction retval
        = new PackageProductionImpl(
            metadata,
            classFactory,
            targetDirectory);
    packageNameToProductionMap.put(javaPackage, retval);
    return retval;
  }

  @Override
  @SuppressWarnings("null")
  public Collection<IGeneratedModuleClass> getModuleProductions() {
    return Collections.unmodifiableCollection(moduleToProductionMap.values());
  }

  /**
   * Get all package productions in this production.
   *
   * @return an unmodifiable collection of package productions
   */
  @SuppressWarnings("null")
  @NonNull
  protected Collection<IPackageProduction> getPackageProductions() {
    return Collections.unmodifiableCollection(packageNameToProductionMap.values());
  }

  @Override
  public IGeneratedModuleClass getModuleProduction(IModule module) {
    return moduleToProductionMap.get(module);
  }

  @Override
  public List<IGeneratedDefinitionClass> getGlobalDefinitionClasses() {
    return ObjectUtils.notNull(getModuleProductions().stream()
        .flatMap(metaschema -> metaschema.getGeneratedDefinitionClasses().stream())
        .collect(Collectors.toUnmodifiableList()));
  }

  @Override
  public Stream<? extends IGeneratedClass> getGeneratedClasses() {
    return ObjectUtils.notNull(Stream.concat(
        // generated definitions and Metaschema module
        getModuleProductions().stream()
            .flatMap(module -> Stream.concat(
                Stream.of(module),
                module.getGeneratedDefinitionClasses().stream())),
        // generated package-info.java
        getPackageProductions().stream()
            .flatMap(javaPackage -> Stream.of(javaPackage.getGeneratedClass()))));
  }

}
