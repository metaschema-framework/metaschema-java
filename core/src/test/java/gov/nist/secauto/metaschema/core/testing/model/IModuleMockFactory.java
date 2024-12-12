/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.testing.model;

import gov.nist.secauto.metaschema.core.testing.model.mocking.IMockFactory;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a factory used to produce Metaschema module-based definitions and
 * instances.
 */
public interface IModuleMockFactory extends IMockFactory {
  /**
   * Get a new flag builder.
   *
   * @return the builder
   */
  @NonNull
  default IFlagBuilder flag() {
    return IFlagBuilder.builder();
  }

  /**
   * Get a new field builder.
   *
   * @return the builder
   */
  @NonNull
  default IFieldBuilder field() {
    return IFieldBuilder.builder();
  }

  /**
   * Get a new assembly builder.
   *
   * @return the builder
   */
  @NonNull
  default IAssemblyBuilder assembly() {
    return IAssemblyBuilder.builder();
  }
}
