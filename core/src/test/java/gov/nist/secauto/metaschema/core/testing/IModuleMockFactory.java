/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.testing;

import gov.nist.secauto.metaschema.core.testing.mocking.IMockFactory;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IModuleMockFactory extends IMockFactory {
  /**
   * Get a new flag builder.
   *
   * @return the builder
   */
  @NonNull
  default FlagBuilder flag() {
    return FlagBuilder.builder();
  }

  /**
   * Get a new field builder.
   *
   * @return the builder
   */
  @NonNull
  default FieldBuilder field() {
    return FieldBuilder.builder();
  }

  /**
   * Get a new assembly builder.
   *
   * @return the builder
   */
  @NonNull
  default AssemblyBuilder assembly() {
    return AssemblyBuilder.builder();
  }
}
