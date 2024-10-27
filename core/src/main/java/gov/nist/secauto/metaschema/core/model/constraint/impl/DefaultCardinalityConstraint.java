/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint.impl;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.model.IAttributable;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.model.constraint.ICardinalityConstraint;

import java.util.Map;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents a cardinality constraint.
 * <p>
 * Enforces that the number of items matching the target fall within the
 * inclusive range described by the {@code minOccurs} or {@code maxOccurs}
 * values.
 */
public final class DefaultCardinalityConstraint
    extends AbstractConstraint
    implements ICardinalityConstraint {
  @Nullable
  private final Integer minOccurs;
  @Nullable
  private final Integer maxOccurs;

  /**
   * Construct a new cardinality constraint.
   *
   * @param id
   *          the optional identifier for the constraint
   * @param formalName
   *          the constraint's formal name or {@code null} if not provided
   * @param description
   *          the constraint's semantic description or {@code null} if not
   *          provided
   * @param source
   *          information about the constraint source
   * @param level
   *          the significance of a violation of this constraint
   * @param target
   *          the Metapath expression identifying the nodes the constraint targets
   * @param properties
   *          a collection of associated properties
   * @param minOccurs
   *          if provided, the constraint ensures that the count of targets is at
   *          least this value
   * @param maxOccurs
   *          if provided, the constraint ensures that the count of targets is at
   *          most this value
   * @param message
   *          an optional message to emit when the constraint is violated
   * @param remarks
   *          optional remarks describing the intent of the constraint
   */
  @SuppressWarnings("PMD.ExcessiveParameterList")
  public DefaultCardinalityConstraint(
      @Nullable String id,
      @Nullable String formalName,
      @Nullable MarkupLine description,
      @NonNull ISource source,
      @NonNull Level level,
      @NonNull String target,
      @NonNull Map<IAttributable.Key, Set<String>> properties,
      @Nullable Integer minOccurs,
      @Nullable Integer maxOccurs,
      @Nullable String message,
      @Nullable MarkupMultiline remarks) {
    super(id, formalName, description, source, level, target, properties, message, remarks);
    if (minOccurs == null && maxOccurs == null) {
      throw new IllegalArgumentException("at least one of minOccurs or maxOccurs must be provided");
    }
    this.minOccurs = minOccurs;
    this.maxOccurs = maxOccurs;
  }

  @Override
  public Integer getMinOccurs() {
    return minOccurs;
  }

  @Override
  public Integer getMaxOccurs() {
    return maxOccurs;
  }

}
