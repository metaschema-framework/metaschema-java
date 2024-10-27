/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.model.constraint.impl.DefaultIndexConstraint;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a rule that generates a key-based index containing references to
 * data items found in a Metaschema data instance.
 * <p>
 * The generated index can be used to check cross-references between Metaschema
 * data objects using the {@link IIndexHasKeyConstraint}.
 */
public interface IIndexConstraint extends IKeyConstraint {
  /**
   * Get the name of the index, which is used to refer to the index by an
   * {@link IIndexHasKeyConstraint}.
   *
   * @return the name of the index
   */
  @NonNull
  String getName();

  @Override
  default <T, R> R accept(IConstraintVisitor<T, R> visitor, T state) {
    return visitor.visitIndexConstraint(this, state);
  }

  /**
   * Create a new constraint builder.
   *
   * @param name
   *          the identifier for the index
   *
   * @return the builder
   */
  @NonNull
  static Builder builder(@NonNull String name) {
    return new Builder(name);
  }

  /**
   * Provides a builder pattern for constructing a new {@link IIndexConstraint}.
   */
  final class Builder
      extends AbstractKeyConstraintBuilder<Builder, IIndexConstraint> {
    @NonNull
    private final String name;

    private Builder(@NonNull String name) {
      this.name = name;
    }

    @Override
    protected Builder getThis() {
      return this;
    }

    @NonNull
    private String getName() {
      return name;
    }

    @Override
    protected DefaultIndexConstraint newInstance() {
      return new DefaultIndexConstraint(
          getId(),
          getFormalName(),
          getDescription(),
          ObjectUtils.notNull(getSource()),
          getLevel(),
          getTarget(),
          getProperties(),
          getName(),
          getKeyFields(),
          getMessage(),
          getRemarks());
    }
  }
}
