/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.model.constraint.impl.DefaultExpectConstraint;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a rule requiring a Metaschema assembly, field, or flag data
 * instance to pass a Metapath-based test.
 * <p>
 * A custom message can be used to indicate what a test failure signifies.
 */
public interface IExpectConstraint extends IConfigurableMessageConstraint {
  /**
   * Get the test to use to validate selected nodes.
   *
   * @return the test metapath expression to use
   */
  @NonNull
  String getTest();

  @Override
  default <T, R> R accept(IConstraintVisitor<T, R> visitor, T state) {
    return visitor.visitExpectConstraint(this, state);
  }

  /**
   * Create a new constraint builder.
   *
   * @return the builder
   */
  @NonNull
  static Builder builder() {
    return new Builder();
  }

  /**
   * Provides a builder pattern for constructing a new {@link IExpectConstraint}.
   */
  final class Builder
      extends AbstractConfigurableMessageConstraintBuilder<Builder, IExpectConstraint> {
    private String test;

    private Builder() {
      // disable construction
    }

    /**
     * Use the provided test to validate selected nodes.
     *
     * @param test
     *          the test metapath expression to use
     * @return this builder
     */
    @NonNull
    public Builder test(@NonNull String test) {
      this.test = test;
      return this;
    }

    @Override
    protected Builder getThis() {
      return this;
    }

    @Override
    protected void validate() {
      super.validate();

      ObjectUtils.requireNonNull(getTest());
    }

    private String getTest() {
      return test;
    }

    @Override
    protected IExpectConstraint newInstance() {
      return new DefaultExpectConstraint(
          getId(),
          getFormalName(),
          getDescription(),
          ObjectUtils.notNull(getSource()),
          getLevel(),
          getTarget(),
          getProperties(),
          ObjectUtils.requireNonNull(getTest()),
          getMessage(),
          getRemarks());
    }
  }
}
