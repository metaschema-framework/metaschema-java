/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint.impl;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.metapath.MetapathExpression;
import gov.nist.secauto.metaschema.core.model.constraint.IKeyField;
import gov.nist.secauto.metaschema.core.model.constraint.ISource;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.regex.Pattern;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import nl.talsmasoftware.lazy4j.Lazy;
import nl.talsmasoftware.lazy4j.LazyEvaluationException;

public class DefaultKeyField implements IKeyField {

  @Nullable
  private final Pattern pattern;
  @NonNull
  private final Lazy<MetapathExpression> target;
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
   * @param source
   *          the descriptor for the resource containing the constraint
   */
  public DefaultKeyField(
      @NonNull String target,
      @Nullable Pattern pattern,
      @Nullable MarkupMultiline remarks,
      @NonNull ISource source) {
    this.pattern = pattern;
    this.target = ObjectUtils.notNull(Lazy.lazy(() -> MetapathExpression.compile(target, source.getStaticContext())));
    this.remarks = remarks;
  }

  @Override
  public Pattern getPattern() {
    return pattern;
  }

  @Override
  public String getTarget() {
    return getTargetMetapath().getPath();
  }

  @Override
  public MetapathExpression getTargetMetapath() {
    try {
      return ObjectUtils.notNull(target.get());
    } catch (LazyEvaluationException ex) {
      Throwable cause = ex.getCause();
      cause.addSuppressed(ex);
      if (cause instanceof RuntimeException) {
        throw (RuntimeException) cause;
      }
      throw ex;
    }
  }

  @Override
  public MarkupMultiline getRemarks() {
    return remarks;
  }
}
