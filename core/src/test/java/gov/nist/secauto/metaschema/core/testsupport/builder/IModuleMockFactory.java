/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.testsupport.builder;

import gov.nist.secauto.metaschema.core.testsupport.mocking.IMockFactory;

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

  /**
   * Get a new module builder.
   *
   * @return the builder
   */
  @NonNull
  default IModuleBuilder module() {
    return IModuleBuilder.builder();
  }

  /**
   * Create a reference to an assembly definition by name. The reference will be
   * resolved when the module is built, allowing recursive assembly structures.
   *
   * @param name
   *          the local name of the referenced assembly definition
   * @return a builder that represents the reference
   */
  @NonNull
  default IModelBuilder<?> assemblyRef(@NonNull String name) {
    return new AssemblyReference(name);
  }

  /**
   * Create a reference to a field definition by name. The reference will be
   * resolved when the module is built.
   *
   * @param name
   *          the local name of the referenced field definition
   * @return a builder that represents the reference
   */
  @NonNull
  default IModelBuilder<?> fieldRef(@NonNull String name) {
    return new FieldReference(name);
  }

  /**
   * Get a new constraint set builder.
   *
   * @return the builder
   */
  @NonNull
  default IConstraintSetBuilder constraintSet() {
    return IConstraintSetBuilder.builder();
  }
}
