/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.model.constraint.impl.DefaultKeyField;

import java.util.regex.Pattern;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents a component of a key used in a key-based index.
 * <p>
 * A key field is targeted at a Metaschema field or flag data object's value. An
 * optional pattern can be used to extract a portion of the value for use in
 * generating an index key.
 */
public interface IKeyField {
  /**
   * Construct a new key field based on the provided target. An optional pattern
   * can be used to extract a portion of the resulting key value.
   *
   * @param target
   *          a Metapath expression identifying the target of the key field
   * @param pattern
   *          an optional used to extract a portion of the resulting key value
   * @param remarks
   *          optional remarks describing the intent of the constraint
   * @return the new key field
   */
  @SuppressWarnings("PMD.ShortMethodName")
  @NonNull
  static IKeyField of(
      @NonNull IMetapathExpression target,
      @Nullable Pattern pattern,
      @Nullable MarkupMultiline remarks) {
    return new DefaultKeyField(target, pattern, remarks);
  }

  /**
   * Get the Metapath expression that identifies the node item whose value will be
   * used as the key value.
   *
   * @return the Metapath expression identifying the key value target
   */
  @NonNull
  IMetapathExpression getTarget();

  /**
   * A pattern to use to retrieve the value. If non-{@code null}, the first
   * capturing group is used to retrieve the value.
   *
   * @return a pattern to use to get the value or {@code null} if the full value
   *         is to be used
   */
  @Nullable
  Pattern getPattern();

  /**
   * Any remarks about the key field as markup text.
   *
   * @return markup text or {@code null} if no text is provided
   */
  @Nullable
  MarkupMultiline getRemarks();
}
