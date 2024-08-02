/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen;

import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.codegen.typeinfo.IMetaschemaClassFactory;

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

class ProductionImpl implements IProduction {

  @NonNull
  private final Map<IModule, IGeneratedModuleClass> moduleToProductionMap // NOPMD - immutable
      = new HashMap<>();
  @NonNull
  private final Map<String, IPackageProduction> packageNameToProductionMap // NOPMD - immutable
      = new HashMap<>();

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
