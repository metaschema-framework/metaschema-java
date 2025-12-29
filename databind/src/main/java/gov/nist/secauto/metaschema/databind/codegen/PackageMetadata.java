/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Tracks metadata about a Java package during code generation.
 * <p>
 * This class aggregates information about generated module classes that share
 * the same package name, ensuring consistent XML namespace association.
 */
class PackageMetadata {
  @NonNull
  private final String packageName;
  @NonNull
  private final URI xmlNamespace;
  @NonNull
  private final List<IGeneratedModuleClass> moduleProductions = new LinkedList<>();

  /**
   * Construct package metadata based on an initial module production.
   *
   * @param moduleProduction
   *          the first module production for this package
   */
  public PackageMetadata(@NonNull IGeneratedModuleClass moduleProduction) {
    packageName = moduleProduction.getPackageName();
    xmlNamespace = moduleProduction.getModule().getXmlNamespace();
    moduleProductions.add(moduleProduction);
  }

  /**
   * Get the Java package name.
   *
   * @return the package name
   */
  @NonNull
  protected String getPackageName() {
    return packageName;
  }

  /**
   * Get the XML namespace associated with this package.
   *
   * @return the XML namespace URI
   */
  @NonNull
  protected URI getXmlNamespace() {
    return xmlNamespace;
  }

  /**
   * Get the module productions associated with this package.
   *
   * @return the list of module productions
   */
  @NonNull
  protected List<IGeneratedModuleClass> getModuleProductions() {
    return moduleProductions;
  }

  /**
   * Add a module production to this package.
   *
   * @param moduleProduction
   *          the module production to add
   * @throws IllegalStateException
   *           if the module's XML namespace does not match the package's
   *           namespace
   */
  public void addModule(@NonNull IGeneratedModuleClass moduleProduction) {
    URI nextXmlNamespace = moduleProduction.getModule().getXmlNamespace();
    if (!xmlNamespace.equals(nextXmlNamespace)) {
      throw new IllegalStateException(String.format(
          "The package %s is associated with the XML namespaces '%s' and '%s'."
              + " A package must be associated with a single XML namespace.",
          getPackageName(), getXmlNamespace().toASCIIString(), nextXmlNamespace.toASCIIString()));
    }
    moduleProductions.add(moduleProduction);
  }
}
