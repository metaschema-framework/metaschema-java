/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.model.constraint.impl.DefaultUniqueConstraint;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a rule that requires all matching data items found in a Metaschema
 * data instance to have a unique key.
 * <p>
 * This rule is similar to the {@link IIndexConstraint} in how the keys are
 * generated, but this constraint type does not persist a named index.
 */
public interface IUniqueConstraint extends IKeyConstraint {

  @Override
  default <T, R> R accept(IConstraintVisitor<T, R> visitor, T state) {
    return visitor.visitUniqueConstraint(this, state);
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
   * Provides a builder pattern for constructing a new {@link IUniqueConstraint}.
   */
  final class Builder
      extends AbstractKeyConstraintBuilder<Builder, IUniqueConstraint> {
    private Builder() {
      // disable construction
    }

    @Override
    protected Builder getThis() {
      return this;
    }

    @Override
    protected IUniqueConstraint newInstance() {
      return new DefaultUniqueConstraint(
          getId(),
          getFormalName(),
          getDescription(),
          ObjectUtils.notNull(getSource()),
          getLevel(),
          getTarget(),
          getProperties(),
          getKeyFields(),
          getMessage(),
          getRemarks());
    }
  }
}
