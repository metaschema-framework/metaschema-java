/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.testsupport.builder;

import gov.nist.secauto.metaschema.core.model.constraint.AbstractConstraintBuilder;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraint;

import java.util.function.Consumer;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A builder for creating constraint contexts within a constraint set.
 * <p>
 * A context defines a metapath expression that targets specific elements, and
 * the constraints that apply to those targets.
 */
public interface IContextBuilder {

  /**
   * Set the metapath expression that defines what this context targets.
   *
   * @param target
   *          the metapath expression
   * @return this builder
   */
  @NonNull
  IContextBuilder metapath(@NonNull String target);

  /**
   * Add a constraint to this context using a constraint builder.
   * <p>
   * The builder's {@code build()} method will be called to create the constraint.
   *
   * @param <B>
   *          the constraint builder type
   * @param <C>
   *          the constraint type
   * @param constraintBuilder
   *          the constraint builder
   * @return this builder
   */
  @NonNull
  <B extends AbstractConstraintBuilder<B, C>, C extends IConstraint> IContextBuilder constraint(
      @NonNull AbstractConstraintBuilder<B, C> constraintBuilder);

  /**
   * Add a child context nested within this context.
   *
   * @param childConfigurer
   *          a consumer that configures the child context
   * @return this builder
   */
  @NonNull
  IContextBuilder childContext(@NonNull Consumer<IContextBuilder> childConfigurer);
}
