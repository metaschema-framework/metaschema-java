/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model.constraint.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import dev.metaschema.core.datatype.markup.MarkupLine;
import dev.metaschema.core.datatype.markup.MarkupMultiline;
import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.model.IAttributable;
import dev.metaschema.core.model.ISource;
import dev.metaschema.core.model.constraint.ConstraintInitializationException;
import dev.metaschema.core.model.constraint.IConstraint;
import dev.metaschema.core.model.constraint.IKeyConstraint;
import dev.metaschema.core.model.constraint.IKeyField;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

abstract class AbstractKeyConstraint
    extends AbstractConfigurableMessageConstraint
    implements IKeyConstraint {
  @NonNull
  private final List<IKeyField> keyFields;

  /**
   * Create a new key-based constraint, which uses a set of key fields to build a
   * key.
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
   * @param keyFields
   *          a list of key fields associated with the constraint
   * @param message
   *          an optional message to emit when the constraint is violated
   * @param remarks
   *          optional remarks describing the intent of the constraint
   */
  @SuppressFBWarnings("CT_CONSTRUCTOR_THROW")
  protected AbstractKeyConstraint(
      @Nullable String id,
      @Nullable String formalName,
      @Nullable MarkupLine description,
      @NonNull ISource source,
      @NonNull Level level,
      @NonNull IMetapathExpression target,
      @NonNull Map<IAttributable.Key, Set<String>> properties,
      @NonNull List<IKeyField> keyFields,
      @Nullable String message,
      @Nullable MarkupMultiline remarks) {
    super(id, formalName, description, source, level, target, properties, message, remarks);
    if (keyFields.isEmpty()) {
      throw new ConstraintInitializationException(
          String.format("An empty list of key fields is not allowed in the constraint %s in '%s'.",
              IConstraint.getConstraintIdentity(this),
              source.getLocationHint()));
    }
    this.keyFields = keyFields;
  }

  @Override
  public List<IKeyField> getKeyFields() {
    return keyFields;
  }
}
