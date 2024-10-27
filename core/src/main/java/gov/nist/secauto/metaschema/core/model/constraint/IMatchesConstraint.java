/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.model.constraint.impl.DefaultMatchesConstraint;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.regex.Pattern;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents a rule requiring the value of a field or flag to match a pattern
 * and/or conform to an identified data type.
 */
public interface IMatchesConstraint extends IConstraint {
  /**
   * Get the expected pattern.
   *
   * @return the expected pattern or {@code null} if there is no expected pattern
   */
  @Nullable
  Pattern getPattern();

  /**
   * Get the expected data type.
   *
   * @return the expected data type or {@code null} if there is no expected data
   *         type
   */
  @Nullable
  IDataTypeAdapter<?> getDataType();

  @Override
  default <T, R> R accept(IConstraintVisitor<T, R> visitor, T state) {
    return visitor.visitMatchesConstraint(this, state);
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
   * Provides a builder pattern for constructing a new {@link IMatchesConstraint}.
   */
  final class Builder
      extends AbstractConstraintBuilder<Builder, IMatchesConstraint> {
    private Pattern pattern;
    private IDataTypeAdapter<?> datatype;

    private Builder() {
      // disable construction
    }

    /**
     * Use the provided pattern to validate associated values.
     *
     * @param pattern
     *          the pattern to use
     * @return this builder
     */
    public Builder regex(@NonNull String pattern) {
      return regex(ObjectUtils.notNull(Pattern.compile(pattern)));
    }

    /**
     * Use the provided pattern to validate associated values.
     *
     * @param pattern
     *          the expected pattern
     * @return this builder
     */
    public Builder regex(@NonNull Pattern pattern) {
      this.pattern = pattern;
      return this;
    }

    /**
     * Use the provided data type to validate associated values.
     *
     * @param datatype
     *          the expected data type
     * @return this builder
     */
    public Builder datatype(@NonNull IDataTypeAdapter<?> datatype) {
      this.datatype = datatype;
      return this;
    }

    @Override
    protected Builder getThis() {
      return this;
    }

    @Override
    protected void validate() {
      super.validate();

      if (getPattern() == null && getDatatype() == null) {
        throw new IllegalStateException("A pattern or data type must be provided at minimum.");
      }
    }

    private Pattern getPattern() {
      return pattern;
    }

    private IDataTypeAdapter<?> getDatatype() {
      return datatype;
    }

    @Override
    protected IMatchesConstraint newInstance() {
      return new DefaultMatchesConstraint(
          getId(),
          getFormalName(),
          getDescription(),
          ObjectUtils.notNull(getSource()),
          getLevel(),
          getTarget(),
          getProperties(),
          getPattern(),
          getDatatype(),
          getMessage(),
          getRemarks());
    }
  }
}
