/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IModuleLoader<M extends IModuleExtended<M, ?, ?, ?, ?>> extends ILoader<M> {
  /**
   * Used to define a post-processing operation to perform on a module.
   */
  @FunctionalInterface
  interface IModulePostProcessor {
    /**
     * Perform a post-processing operation on the provided module.
     *
     * @param module
     *          the Metaschema module to post-process
     */
    void processModule(@NonNull IModule module);
  }
}
