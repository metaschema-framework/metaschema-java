/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.model.IModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.constraint.impl.DefaultCardinalityConstraint;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents a rule requiring a Metaschema assembly data instance to have
 * elements with a minimum and/or maximum occurrence.
 */
public interface ICardinalityConstraint extends IConfigurableMessageConstraint {
  /**
   * Retrieve the required minimum occurrence of the target instance. If
   * specified, this value must be less than or equal to the value of
   * {@link IModelInstanceAbsolute#getMaxOccurs()} and greater than
   * {@link IModelInstanceAbsolute#getMinOccurs()}.
   *
   * @return a non-negative integer or {@code null} if not defined
   */
  @Nullable
  Integer getMinOccurs();

  /**
   * Retrieve the required maximum occurrence of the target instance. If
   * specified, this value must be less than the value of
   * {@link IModelInstanceAbsolute#getMaxOccurs()} and greater than or equal to
   * {@link IModelInstanceAbsolute#getMinOccurs()}.
   *
   * @return a non-negative integer or {@code null} if not defined
   */
  @Nullable
  Integer getMaxOccurs();

  @Override
  default <T, R> R accept(IConstraintVisitor<T, R> visitor, T state) {
    return visitor.visitCardinalityConstraint(this, state);
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
   * Provides a builder pattern for constructing a new
   * {@link ICardinalityConstraint}.
   */
  final class Builder
      extends AbstractConfigurableMessageConstraintBuilder<Builder, ICardinalityConstraint> {
    private Integer minOccurs;
    private Integer maxOccurs;

    private Builder() {
      // disable construction
    }

    /**
     * Use the provided minimum occurrence to validate associated targets.
     *
     * @param value
     *          the expected occurrence
     * @return this builder
     */
    public Builder minOccurs(int value) {
      this.minOccurs = value;
      return this;
    }

    /**
     * Use the provided maximum occurrence to validate associated targets.
     *
     * @param value
     *          the expected occurrence
     * @return this builder
     */
    public Builder maxOccurs(int value) {
      this.maxOccurs = value;
      return this;
    }

    @Override
    protected Builder getThis() {
      return this;
    }

    @Override
    protected void validate() {
      super.validate();

      if (getMinOccurs() == null && getMaxOccurs() == null) {
        throw new IllegalStateException("At least one of minOccurs or maxOccurs must be provided.");
      }
    }

    private Integer getMinOccurs() {
      return minOccurs;
    }

    private Integer getMaxOccurs() {
      return maxOccurs;
    }

    @Override
    protected ICardinalityConstraint newInstance() {
      return new DefaultCardinalityConstraint(
          getId(),
          getFormalName(),
          getDescription(),
          ObjectUtils.notNull(getSource()),
          getLevel(),
          getTarget(),
          getProperties(),
          getMinOccurs(),
          getMaxOccurs(),
          getMessage(),
          getRemarks());
    }
  }
}
