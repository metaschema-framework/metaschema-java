/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen;

import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.databind.codegen.config.IBindingConfiguration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides methods for generating Java classes based on a single or a
 * collection of Metaschemas.
 */
public final class JavaGenerator {
  private static final Logger LOGGER = LogManager.getLogger(JavaGenerator.class);

  private JavaGenerator() {
    // disable construction
  }

  /**
   * Generate Java sources for the provided Metaschema module.
   *
   * @param module
   *          the Metaschema module to generate Java sources for
   * @param targetDir
   *          the directory to generate sources in
   * @param bindingConfiguration
   *          the binding customizations to use when generating the Java classes
   * @return information about all the produced classes
   * @throws IOException
   *           if an error occurred while generating the class
   */
  public static IProduction generate(
      @NonNull IModule module,
      @NonNull Path targetDir,
      @NonNull IBindingConfiguration bindingConfiguration) throws IOException {
    return generate(CollectionUtil.singletonList(module), targetDir, bindingConfiguration);
  }

  /**
   * Generates Java classes for Module fields and flags.
   *
   * @param modules
   *          the Metaschema modules to build classes for
   * @param targetDirectory
   *          the directory to generate classes in
   * @param bindingConfiguration
   *          binding customizations that can be used to set namespaces, class
   *          names, and other aspects of generated classes
   * @return information about all the produced classes
   * @throws IOException
   *           if a build error occurred while generating the class
   */
  @NonNull
  public static IProduction generate(
      @NonNull Collection<? extends IModule> modules,
      @NonNull Path targetDirectory,
      @NonNull IBindingConfiguration bindingConfiguration) throws IOException {
    Objects.requireNonNull(modules, "metaschemas");
    Objects.requireNonNull(targetDirectory, "generationTargetDirectory");
    Objects.requireNonNull(bindingConfiguration, "bindingConfiguration");
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Generating Java classes in: {}", targetDirectory);
    }

    return IProduction.of(modules, bindingConfiguration, targetDirectory);
  }
}
