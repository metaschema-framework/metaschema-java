/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.model.constraint.impl.DefaultExpectConstraint;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a rule requiring a Metaschema assembly, field, or flag data
 * instance to pass a Metapath-based test.
 * <p>
 * A custom message can be used to indicate what a test failure signifies.
 */
public interface IExpectConstraint extends IConstraint {
  /**
   * Get the test to use to validate selected nodes.
   *
   * @return the test metapath expression to use
   */
  @NonNull
  String getTest();

  /**
   * A message to emit when the constraint is violated. Allows embedded Metapath
   * expressions using the syntax {@code \{ metapath \}}.
   *
   * @return the message if defined or {@code null} otherwise
   */
  String getMessage();

  /**
   * Generate a violation message using the provide item and dynamic context for
   * inline Metapath value insertion.
   *
   * @param item
   *          the target Metapath item to use as the focus for Metapath evaluation
   * @param context
   *          the dynamic context for Metapath evaluation
   * @return the message
   */
  String generateMessage(@NonNull INodeItem item, @NonNull DynamicContext context);

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

  final class Builder
      extends AbstractConstraintBuilder<Builder, IExpectConstraint> {
    private String test;
    private String message;

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

    /**
     * A message to emit when the constraint is violated. Allows embedded Metapath
     * expressions using the syntax {@code \{ metapath \}}.
     *
     * @param message
     *          the message if defined or {@code null} otherwise
     * @return this builder
     */
    @NonNull
    public Builder message(@NonNull String message) {
      this.message = message;
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

    private String getMessage() {
      return message;
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
