/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint.impl;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.model.IAttributable;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.model.constraint.IMatchesConstraint;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents a matches constraint.
 * <p>
 * Enforces a value pattern and/or data type.
 */
public final class DefaultMatchesConstraint
    extends AbstractConstraint
    implements IMatchesConstraint {
  private final Pattern pattern;
  private final IDataTypeAdapter<?> dataType;

  /**
   * Construct a new matches constraint.
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
   * @param pattern
   *          the value pattern to match or {@code null} if there is no match
   *          pattern
   * @param dataType
   *          the value data type to match or {@code null} if there is no match
   *          data type
   * @param message
   *          an optional message to emit when the constraint is violated
   * @param remarks
   *          optional remarks describing the intent of the constraint
   */
  @SuppressWarnings("PMD.ExcessiveParameterList")
  public DefaultMatchesConstraint(
      @Nullable String id,
      @Nullable String formalName,
      @Nullable MarkupLine description,
      @NonNull ISource source,
      @NonNull Level level,
      @NonNull String target,
      @NonNull Map<IAttributable.Key, Set<String>> properties,
      @Nullable Pattern pattern,
      @Nullable IDataTypeAdapter<?> dataType,
      @Nullable String message,
      @Nullable MarkupMultiline remarks) {
    super(id, formalName, description, source, level, target, properties, message, remarks);
    if (pattern == null && dataType == null) {
      throw new IllegalArgumentException("a pattern or data type must be provided");
    }
    this.pattern = pattern;
    this.dataType = dataType;
  }

  @Override
  public Pattern getPattern() {
    return pattern;
  }

  @Override
  public IDataTypeAdapter<?> getDataType() {
    return dataType;
  }

}
