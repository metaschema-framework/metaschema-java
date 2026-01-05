/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model.constraint;

import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.model.constraint.impl.DefaultReportConstraint;
import dev.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a rule reporting a condition when a Metaschema assembly, field, or
 * flag data instance matches a Metapath-based test.
 * <p>
 * Unlike {@link IExpectConstraint} which generates a finding when the test is
 * FALSE, a report constraint generates a finding when the test is TRUE. This is
 * useful for:
 * <ul>
 * <li>Reporting deprecated usage patterns</li>
 * <li>Flagging known issues or limitations</li>
 * <li>Providing informational messages about content</li>
 * </ul>
 * <p>
 * A custom message can be used to indicate what a matching condition signifies.
 * The default severity level is {@link Level#INFORMATIONAL}.
 *
 * @since 2.0.0
 */
public interface IReportConstraint extends IConfigurableMessageConstraint {
  /**
   * The default severity level for report constraints.
   */
  @NonNull
  Level DEFAULT_LEVEL = Level.INFORMATIONAL;

  @Override
  default Type getType() {
    return Type.REPORT;
  }

  /**
   * Get the test to use to identify reportable conditions in selected nodes.
   * <p>
   * A finding is generated when this test evaluates to {@code true}.
   *
   * @return the test metapath expression to use
   */
  @NonNull
  IMetapathExpression getTest();

  @Override
  default <T, R> R accept(IConstraintVisitor<T, R> visitor, T state) {
    return visitor.visitReportConstraint(this, state);
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
   * Provides a builder pattern for constructing a new {@link IReportConstraint}.
   */
  final class Builder
      extends AbstractConfigurableMessageConstraintBuilder<Builder, IReportConstraint> {
    private IMetapathExpression test;
    private boolean levelSet;

    private Builder() {
      // disable construction
    }

    /**
     * Use the provided test to identify reportable conditions in selected nodes.
     * <p>
     * A finding is generated when this test evaluates to {@code true}.
     *
     * @param test
     *          the test metapath expression to use
     * @return this builder
     */
    @NonNull
    public Builder test(@NonNull IMetapathExpression test) {
      this.test = test;
      return this;
    }

    @Override
    protected Builder getThis() {
      return this;
    }

    /**
     * {@inheritDoc}
     * <p>
     * For report constraints, the default level is {@link Level#INFORMATIONAL} if
     * no level is explicitly set.
     */
    @Override
    @NonNull
    public Builder level(@NonNull Level level) {
      this.levelSet = true;
      return super.level(level);
    }

    @Override
    @NonNull
    protected Level getLevel() {
      return levelSet ? super.getLevel() : DEFAULT_LEVEL;
    }

    @Override
    protected void validate() {
      super.validate();

      ObjectUtils.requireNonNull(getTest());
    }

    private IMetapathExpression getTest() {
      return test;
    }

    @Override
    protected IReportConstraint newInstance() {
      return new DefaultReportConstraint(
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
