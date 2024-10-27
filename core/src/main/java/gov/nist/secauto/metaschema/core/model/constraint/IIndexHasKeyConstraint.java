/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.model.constraint.impl.DefaultIndexHasKeyConstraint;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a rule that checks that a key generated for a Metaschema data
 * object exists in a named index that was generated using an
 * {@link IIndexConstraint}.
 */
public interface IIndexHasKeyConstraint extends IKeyConstraint {
  /**
   * The name of the index used to verify cross references.
   *
   * @return the index name
   */
  @NonNull
  String getIndexName();

  @Override
  default <T, R> R accept(IConstraintVisitor<T, R> visitor, T state) {
    return visitor.visitIndexHasKeyConstraint(this, state);
  }

  /**
   * Create a new constraint builder.
   *
   * @param useIndex
   *          the index name
   * @return the builder
   */
  @NonNull
  static Builder builder(@NonNull String useIndex) {
    return new Builder(useIndex);
  }

  /**
   * Provides a builder pattern for constructing a new
   * {@link IIndexHasKeyConstraint}.
   */
  final class Builder
      extends AbstractKeyConstraintBuilder<Builder, IIndexHasKeyConstraint> {
    @NonNull
    private final String indexName;

    private Builder(@NonNull String useIndex) {
      this.indexName = useIndex;
    }

    @Override
    protected Builder getThis() {
      return this;
    }

    @NonNull
    private String getIndexName() {
      return indexName;
    }

    @Override
    protected IIndexHasKeyConstraint newInstance() {
      return new DefaultIndexHasKeyConstraint(
          getId(),
          getFormalName(),
          getDescription(),
          ObjectUtils.notNull(getSource()),
          getLevel(),
          getTarget(),
          getProperties(),
          getIndexName(),
          getKeyFields(),
          getMessage(),
          getRemarks());
    }
  }
}
