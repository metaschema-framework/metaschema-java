/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen;

import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.databind.codegen.config.IBindingConfiguration;
import gov.nist.secauto.metaschema.databind.codegen.typeinfo.IMetaschemaClassFactory;
import gov.nist.secauto.metaschema.databind.codegen.typeinfo.ITypeResolver;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Information about Java classes generated for a collection of Module modules.
 */
public interface IProduction {

  /**
   * Get information about the Java classes generated for each Module module in
   * the collection.
   *
   * @return the Java class information for each module
   */
  @NonNull
  Collection<IGeneratedModuleClass> getModuleProductions();

  /**
   * Get information about the Java classes generated for the provided Module
   * {@code module}.
   *
   * @param module
   *          the Module module to get information for
   * @return the Java class information for the module or {@code null} if this
   *         production did not involve generating classes for the provided module
   */
  @Nullable
  IGeneratedModuleClass getModuleProduction(@NonNull IModule module);

  /**
   * Get a stream of all definition Java classes generated as part of this
   * production.
   * <p>
   * This will include each unique class generated for all Module modules
   * associated with this production.
   *
   * @return the stream of generated Java classes
   */
  @NonNull
  Collection<IGeneratedDefinitionClass> getGlobalDefinitionClasses();

  /**
   * Get a stream of all Java classes generated as part of this production,
   * including module, definition, and package-info classes.
   *
   * @return the stream of generated Java classes
   */
  @NonNull
  Stream<? extends IGeneratedClass> getGeneratedClasses();

  /**
   * Create a new production for the provided set of Module {@code modules}.
   *
   * @param modules
   *          the Module modules to generate and compile classes for
   * @param bindingConfiguration
   *          binding customizations that can be used to set namespaces, class
   *          names, and other aspects of generated classes
   * @param classDir
   *          the directory to generate and compile classes in
   * @return the production information
   * @throws IOException
   *           if an error occurred while generating or compiling the classes
   */
  @NonNull
  static IProduction of( // NOPMD - intentional
      @NonNull Collection<? extends IModule> modules,
      @NonNull IBindingConfiguration bindingConfiguration,
      @NonNull Path classDir) throws IOException {

    ITypeResolver typeResolver = ITypeResolver.newTypeResolver(bindingConfiguration);

    IMetaschemaClassFactory classFactory = IMetaschemaClassFactory.newInstance(typeResolver);

    ProductionImpl retval = new ProductionImpl();
    for (IModule module : modules) {
      assert module != null;
      retval.addModule(module, classFactory, classDir);
    }

    Map<String, PackageMetadata> packageNameToPackageMetadataMap = new HashMap<>(); // NOPMD - no concurrency
    for (IGeneratedModuleClass moduleProduction : retval.getModuleProductions()) {
      String packageName = moduleProduction.getPackageName();

      PackageMetadata metadata = packageNameToPackageMetadataMap.get(packageName);
      if (metadata == null) {
        metadata = new PackageMetadata(moduleProduction); // NOPMD - intentional
        packageNameToPackageMetadataMap.put(metadata.getPackageName(), metadata);
      } else {
        metadata.addModule(moduleProduction);
      }
    }

    for (PackageMetadata metadata : packageNameToPackageMetadataMap.values()) {
      assert metadata != null;
      retval.addPackage(
          metadata,
          classFactory,
          classDir);
    }
    return retval;
  }
}
