/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint.impl;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.model.IAttributable;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.model.constraint.IAllowedValue;
import gov.nist.secauto.metaschema.core.model.constraint.IAllowedValuesConstraint;

import java.util.Map;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents an allowed values constraint.
 * <p>
 * Ensures that a target instance's value matches one of the allowed values.
 * This match is required if {@link #isAllowedOther()} is {@code false},
 * otherwise the constraint will generate a validation warning message if the
 * target instance's value does not match any of the associated allowed value
 * constraints targeting it.
 */
public final class DefaultAllowedValuesConstraint
    extends AbstractConstraint
    implements IAllowedValuesConstraint {
  private final boolean allowedOther;
  @NonNull
  private final Extensible extensible;
  @NonNull
  private final Map<String, IAllowedValue> allowedValues;

  /**
   * Construct a new allowed values constraint.
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
   * @param allowedValues
   *          the list of allowed values for this constraint
   * @param allowedOther
   *          when {@code true} values other than the values specified by
   *          {@code allowedValues} are allowed, or disallowed if {@code false}
   * @param extensible
   *          indicates the degree to which extended values should be allowed
   * @param message
   *          an optional message to emit when the constraint is violated
   * @param remarks
   *          optional remarks describing the intent of the constraint
   */
  public DefaultAllowedValuesConstraint( // NOPMD necessary
      @Nullable String id,
      @Nullable String formalName,
      @Nullable MarkupLine description,
      @NonNull ISource source,
      @NonNull Level level,
      @NonNull String target,
      @NonNull Map<IAttributable.Key, Set<String>> properties,
      @NonNull Map<String, IAllowedValue> allowedValues,
      boolean allowedOther,
      @NonNull Extensible extensible,
      @Nullable String message,
      @Nullable MarkupMultiline remarks) {
    super(id, formalName, description, source, level, target, properties, message, remarks);
    this.allowedValues = allowedValues;
    this.allowedOther = allowedOther;
    this.extensible = extensible;
  }

  @Override
  public Map<String, IAllowedValue> getAllowedValues() {
    return allowedValues;
  }

  @Override
  public boolean isAllowedOther() {
    return allowedOther;
  }

  @Override
  public Extensible getExtensible() {
    return extensible;
  }
}
