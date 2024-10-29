/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint.impl;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.MetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBooleanItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.model.IAttributable;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.model.constraint.IExpectConstraint;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.core.util.ReplacementScanner;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import nl.talsmasoftware.lazy4j.Lazy;

public final class DefaultExpectConstraint
    extends AbstractConstraint
    implements IExpectConstraint {
  @SuppressWarnings("null")
  @NonNull
  private static final Pattern METAPATH_VALUE_TEMPLATE_PATTERN
      = Pattern.compile("(?<!\\\\)(\\{\\s*((?:(?:\\\\})|[^}])*)\\s*\\})");
  @NonNull
  private final Lazy<MetapathExpression> testMetapath;
  private final String message;

  /**
   * Construct a new expect constraint which requires that the associated test
   * evaluates to {@link IBooleanItem#TRUE} against the target.
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
   * @param test
   *          a Metapath expression that is evaluated against the target node to
   *          determine if the constraint passes
   * @param message
   *          an optional message to emit when the constraint is violated
   * @param remarks
   *          optional remarks describing the intent of the constraint
   */
  @SuppressWarnings("PMD.ExcessiveParameterList")
  public DefaultExpectConstraint(
      @Nullable String id,
      @Nullable String formalName,
      @Nullable MarkupLine description,
      @NonNull ISource source,
      @NonNull Level level,
      @NonNull String target,
      @NonNull Map<IAttributable.Key, Set<String>> properties,
      @NonNull String test,
      @Nullable String message,
      @Nullable MarkupMultiline remarks) {
    super(id, formalName, description, source, level, target, properties, remarks);
    this.testMetapath = ObjectUtils.notNull(
        Lazy.lazy(() -> MetapathExpression.compile(
            test,
            source.getStaticContext())));
    this.message = message;
  }

  /**
   * Get the compiled Metapath expression for the test.
   *
   * @return the compiled Metapath expression
   */
  @NonNull
  public MetapathExpression getTestMetapath() {
    return ObjectUtils.notNull(testMetapath.get());
  }

  @Override
  public String getTest() {
    return getTestMetapath().getPath();
  }

  @Override
  public String getMessage() {
    return message;
  }

  @Override
  public String generateMessage(@NonNull INodeItem item, @NonNull DynamicContext context) {
    String message = getMessage();

    return message == null ? null
        : ReplacementScanner.replaceTokens(message, METAPATH_VALUE_TEMPLATE_PATTERN, match -> {
          @SuppressWarnings("null")
          @NonNull String metapath = match.group(2);
          MetapathExpression expr = MetapathExpression.compile(metapath, context.getStaticContext());
          return expr.evaluateAs(item, MetapathExpression.ResultType.STRING, context);
        }).toString();
  }
}
