/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.model.constraint.impl.DefaultAllowedValuesConstraint;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents a rule requiring the value of a field or flag to match the name of
 * one entry in a set of enumerated values.
 */
public interface IAllowedValuesConstraint extends IConstraint {
  /**
   * The default allow other value.
   */
  boolean ALLOW_OTHER_DEFAULT = false;
  /**
   * The default extensible value.
   */
  @NonNull
  Extensible EXTENSIBLE_DEFAULT = Extensible.EXTERNAL;

  /**
   * Indicates how an allowed values constraint can be extended, or if it can be.
   */
  enum Extensible {
    /**
     * Can be extended by external constraints. The most permissive level.
     */
    EXTERNAL,
    /**
     * Can be extended by constraints in the same model.
     */
    MODEL,
    /**
     * Cannot be extended. The most restrictive level.
     */
    NONE;
  }

  /**
   * Get the collection allowed values associated with this constraint.
   *
   * @return a mapping of value to the associated {@link IAllowedValue} item
   */
  @NonNull
  Map<String, ? extends IAllowedValue> getAllowedValues();

  /**
   * Get a specific allowed value by name, if it is defined for this constraint.
   *
   * @param name
   *          the value name
   * @return the allowed value or {@code null} if the value is not defined
   */
  @Nullable
  default IAllowedValue getAllowedValue(String name) {
    return getAllowedValues().get(name);
  }

  /**
   * Determines if this allowed value constraint is open-ended ({@code true}) or
   * closed. If "open-ended", the constraint allows the target's value to by any
   * additional unspecified value. If "closed", the constraint requries the
   * target's value to be one of the specified values.
   *
   * @return {@code true} if the constraint is "open-ended", or {@code false}
   *         otherwise
   */
  boolean isAllowedOther();

  /**
   * Determines the degree to which this constraint can be extended by other
   * constraints applied to the same value.
   *
   * @return the enumeration value
   */
  @NonNull
  Extensible getExtensible();

  @Override
  default <T, R> R accept(IConstraintVisitor<T, R> visitor, T state) {
    return visitor.visitAllowedValues(this, state);
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
   * {@link IAllowedValuesConstraint}.
   */
  final class Builder
      extends AbstractConstraintBuilder<Builder, IAllowedValuesConstraint> {
    @NonNull
    private final Map<String, IAllowedValue> allowedValues = new LinkedHashMap<>(); // NOPMD not thread safe
    private boolean allowedOther = ALLOW_OTHER_DEFAULT;
    @NonNull
    private Extensible extensible = EXTENSIBLE_DEFAULT;

    private Builder() {
      // disable construction
    }

    /**
     * Use the provided allowed value to validate associated values.
     *
     * @param allowedValue
     *          an expected allowed value
     * @return this builder
     */
    @NonNull
    public Builder allowedValue(@NonNull IAllowedValue allowedValue) {
      this.allowedValues.put(allowedValue.getValue(), allowedValue);
      return this;
    }

    /**
     * Use the provided allowed values to validate associated values.
     *
     * @param allowedValues
     *          an expected allowed values
     * @return this builder
     */
    @NonNull
    public Builder allowedValues(@NonNull Map<String, IAllowedValue> allowedValues) {
      this.allowedValues.putAll(allowedValues);
      return this;
    }

    /**
     * Determine if unspecified values are allowed and will result in the constraint
     * always passing.
     *
     * @param bool
     *          {@code true} if other values are allowed or {@code false} otherwise
     * @return this builder
     */
    @NonNull
    public Builder allowsOther(boolean bool) {
      this.allowedOther = bool;
      return this;
    }

    /**
     * Determine the allowed scope of extension for other constraints matching this
     * constraint's target.
     *
     * @param extensible
     *          the degree of allowed extension
     * @return this builder
     */
    @NonNull
    public Builder extensible(@NonNull Extensible extensible) {
      this.extensible = extensible;
      return this;
    }

    @Override
    protected Builder getThis() {
      return this;
    }

    @NonNull
    private Map<String, IAllowedValue> getAllowedValues() {
      return allowedValues;
    }

    private boolean isAllowedOther() {
      return allowedOther;
    }

    @NonNull
    private Extensible getExtensible() {
      return extensible;
    }

    @Override
    protected IAllowedValuesConstraint newInstance() {
      return new DefaultAllowedValuesConstraint(
          getId(),
          getFormalName(),
          getDescription(),
          ObjectUtils.notNull(getSource()),
          getLevel(),
          getTarget(),
          getProperties(),
          getAllowedValues(),
          isAllowedOther(),
          getExtensible(),
          getMessage(),
          getRemarks());
    }
  }
}
