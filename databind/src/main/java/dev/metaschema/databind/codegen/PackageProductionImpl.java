/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.codegen;

import dev.metaschema.databind.codegen.typeinfo.IMetaschemaClassFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Default implementation of {@link IPackageProduction} representing a generated
 * package-info.java class.
 */
class PackageProductionImpl implements IPackageProduction {
  @NonNull
  private final URI xmlNamespace;
  @NonNull
  private final IGeneratedClass packageInfoClass;

  /**
   * Construct a new package production.
   *
   * @param metadata
   *          the package metadata
   * @param classFactory
   *          the class factory to use for generating the package-info class
   * @param targetDirectory
   *          the directory to generate the class in
   * @throws IOException
   *           if an error occurs during class generation
   */
  @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Use of final fields")
  public PackageProductionImpl(
      @NonNull PackageMetadata metadata,
      @NonNull IMetaschemaClassFactory classFactory,
      @NonNull Path targetDirectory)
      throws IOException {
    this.xmlNamespace = metadata.getXmlNamespace();
    this.packageInfoClass = classFactory.generatePackageInfoClass(
        metadata.getPackageName(),
        this.xmlNamespace,
        metadata.getModuleProductions(),
        targetDirectory);
  }

  @Override
  public URI getXmlNamespace() {
    return xmlNamespace;
  }

  /**
   * Get the generated package-info class associated with this package.
   *
   * @return the package-info class
   */
  @Override
  public IGeneratedClass getGeneratedClass() {
    return packageInfoClass;
  }
}
