/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint.impl;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.model.IAttributable;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.model.constraint.IIndexConstraint;
import gov.nist.secauto.metaschema.core.model.constraint.IKeyField;

import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Represents an index constraint.
 * <p>
 * Uses a set of key fields to build a key and check that that key is unique
 * compared to other keys.
 */
public final class DefaultIndexConstraint
    extends AbstractKeyConstraint
    implements IIndexConstraint {
  @NonNull
  private final String name;

  /**
   * Construct a new index constraint.
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
   * @param name
   *          the name of the index
   * @param keyFields
   *          a list of key fields associated with the constraint
   * @param message
   *          an optional message to emit when the constraint is violated
   * @param remarks
   *          optional remarks describing the intent of the constraint
   */
  @SuppressWarnings("PMD.ExcessiveParameterList")
  @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Use of final fields")
  public DefaultIndexConstraint(
      @Nullable String id,
      @Nullable String formalName,
      @Nullable MarkupLine description,
      @NonNull ISource source,
      @NonNull Level level,
      @NonNull String target,
      @NonNull Map<IAttributable.Key, Set<String>> properties,
      @NonNull String name,
      @NonNull List<IKeyField> keyFields,
      @Nullable String message,
      @Nullable MarkupMultiline remarks) {
    super(id, formalName, description, source, level, target, properties, keyFields, message, remarks);
    if (name.isBlank()) {
      throw new IllegalArgumentException("The index name must be a non-blank string");
    }
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }
}
