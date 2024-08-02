/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides information about a generated package-info.java class, that
 * represents a collection of Module constructs generated from one or more
 * Module modules.
 */
public interface IPackageProduction {
  /**
   * Get the XML namespace associated with the package-info.java class.
   *
   * @return the namespace
   */
  @NonNull
  URI getXmlNamespace();

  /**
   * Get information about the generated package-info.java class associated with
   * this package.
   *
   * @return the package-info.java class information
   */
  @NonNull
  IGeneratedClass getGeneratedClass();
}
