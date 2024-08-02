/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

class PackageMetadata {
  @NonNull
  private final String packageName;
  @NonNull
  private final URI xmlNamespace;
  @NonNull
  private final List<IGeneratedModuleClass> moduleProductions = new LinkedList<>();

  public PackageMetadata(@NonNull IGeneratedModuleClass moduleProduction) {
    packageName = moduleProduction.getPackageName();
    xmlNamespace = moduleProduction.getModule().getXmlNamespace();
    moduleProductions.add(moduleProduction);
  }

  @NonNull
  protected String getPackageName() {
    return packageName;
  }

  @NonNull
  protected URI getXmlNamespace() {
    return xmlNamespace;
  }

  @NonNull
  protected List<IGeneratedModuleClass> getModuleProductions() {
    return moduleProductions;
  }

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
