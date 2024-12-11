/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.testing;

import org.jmock.Mockery;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IModuleMockFactory extends IMockFactory {
  @Override
  @NonNull
  Mockery getContext();

  /**
   * Get a new flag builder.
   *
   * @return the builder
   */
  @NonNull
  default FlagBuilder flag() {
    return FlagBuilder.builder(getContext());
  }

  /**
   * Get a new field builder.
   *
   * @return the builder
   */
  @NonNull
  default FieldBuilder field() {
    return FieldBuilder.builder(getContext());
  }

  /**
   * Get a new assembly builder.
   *
   * @return the builder
   */
  @NonNull
  default AssemblyBuilder assembly() {
    return AssemblyBuilder.builder(getContext());
  }
}
