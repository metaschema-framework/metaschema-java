/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.testsupport.builder;

import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraintSet;

import java.util.function.Consumer;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A builder for creating {@link IConstraintSet} instances programmatically.
 * <p>
 * This builder is intended for test support, allowing constraint sets to be
 * constructed without loading XML files.
 */
public interface IConstraintSetBuilder {

  /**
   * Set the source for the constraint set.
   *
   * @param source
   *          the source information
   * @return this builder
   */
  @NonNull
  IConstraintSetBuilder source(@NonNull ISource source);

  /**
   * Add an imported constraint set.
   *
   * @param imports
   *          the constraint sets to import
   * @return this builder
   */
  @NonNull
  IConstraintSetBuilder imports(@NonNull IConstraintSet... imports);

  /**
   * Add a context to the constraint set.
   *
   * @param contextConfigurer
   *          a consumer that configures the context
   * @return this builder
   */
  @NonNull
  IConstraintSetBuilder context(@NonNull Consumer<IContextBuilder> contextConfigurer);

  /**
   * Build the constraint set.
   *
   * @return the constructed constraint set
   */
  @NonNull
  IConstraintSet build();

  /**
   * Create a new constraint set builder.
   *
   * @return the builder
   */
  @NonNull
  static IConstraintSetBuilder builder() {
    return new ConstraintSetBuilder();
  }
}
