/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint.impl;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.model.constraint.IKeyField;

import java.util.regex.Pattern;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class DefaultKeyField implements IKeyField {

  @Nullable
  private final Pattern pattern;
  @NonNull
  private final IMetapathExpression target;
  @Nullable
  private final MarkupMultiline remarks;

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
   */
  public DefaultKeyField(
      @NonNull IMetapathExpression target,
      @Nullable Pattern pattern,
      @Nullable MarkupMultiline remarks) {
    this.pattern = pattern;
    this.target = target;
    this.remarks = remarks;
  }

  @Override
  public Pattern getPattern() {
    return pattern;
  }

  @Override
  public IMetapathExpression getTarget() {
    return target;
  }

  @Override
  public MarkupMultiline getRemarks() {
    return remarks;
  }
}
