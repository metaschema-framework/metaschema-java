/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema;

import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.databind.IBindingContext;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Performs post-processing on a loaded module.
 *
 * @since 2.0.0
 */
public interface ModuleLoadingPostProcessor {
  /**
   * Post-processes the provided Metaschema module.
   *
   * @param module
   *          the Module module to generate classes for
   * @param bindingContext
   *          the Metaschema binding context used to load bound resources
   * @since 2.0.0
   */
  void postProcessModule(
      @NonNull IModule module,
      @NonNull IBindingContext bindingContext);
}
