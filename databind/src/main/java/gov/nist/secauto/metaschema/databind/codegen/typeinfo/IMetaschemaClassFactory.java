/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.typeinfo;

import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.databind.codegen.IGeneratedClass;
import gov.nist.secauto.metaschema.databind.codegen.IGeneratedDefinitionClass;
import gov.nist.secauto.metaschema.databind.codegen.IGeneratedModuleClass;
import gov.nist.secauto.metaschema.databind.codegen.typeinfo.def.IModelDefinitionTypeInfo;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Collection;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IMetaschemaClassFactory {
  /**
   * Get a new instance of the default class generation factory that uses the
   * provided {@code typeResolver}.
   *
   * @param typeResolver
   *          the resolver used to generate type information for Metasschema
   *          constructs
   * @return the new class factory
   */
  @NonNull
  static IMetaschemaClassFactory newInstance(@NonNull ITypeResolver typeResolver) {
    return DefaultMetaschemaClassFactory.newInstance(typeResolver);
  }

  /**
   * Get the type resolver used to generate type information for Metasschema
   * constructs represented as Java classes, fields, and methods.
   *
   * @return the type resolver
   */
  @NonNull
  ITypeResolver getTypeResolver();

  /**
   * Generate a class in the provided {@code targetDirectory} that represents the
   * provided Module {@code module}.
   *
   * @param module
   *          the Module module to generate the class for
   * @param targetDirectory
   *          the directory to generate the Java class in
   * @return information about the generated class
   * @throws IOException
   *           if an error occurred while generating the Java class
   */
  @NonNull
  IGeneratedModuleClass generateClass(
      @NonNull IModule module,
      @NonNull Path targetDirectory) throws IOException;

  /**
   * Generate a class in the provided {@code targetDirectory} that represents the
   * provided Module definition's {@code typeInfo}.
   *
   * @param typeInfo
   *          the type information for the class to generate
   * @param targetDirectory
   *          the directory to generate the Java class in
   * @return the generated class details
   * @throws IOException
   *           if an error occurred while generating the Java class
   */
  @NonNull
  IGeneratedDefinitionClass generateClass(
      @NonNull IModelDefinitionTypeInfo typeInfo,
      @NonNull Path targetDirectory) throws IOException;

  /**
   * Generate a package-info.java class in the provided {@code targetDirectory}
   * that represents a collection of Module modules.
   *
   * @param javaPackage
   *          the Java package name to use
   * @param xmlNamespace
   *          the default XML namespace for all bound Module information elements
   *          in the generated package
   * @param metaschemaProductions
   *          a collection of previously generated Module modules and definition
   *          classes
   * @param targetDirectory
   *          the directory to generate the Java class in
   * @return the generated class details
   * @throws IOException
   *           if an error occurred while generating the Java class
   */
  @NonNull
  IGeneratedClass generatePackageInfoClass(
      @NonNull String javaPackage,
      @NonNull URI xmlNamespace,
      @NonNull Collection<IGeneratedModuleClass> metaschemaProductions,
      @NonNull Path targetDirectory) throws IOException;
}
