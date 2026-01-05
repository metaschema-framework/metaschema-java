/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model.constraint.impl;

import dev.metaschema.core.datatype.markup.MarkupLine;
import dev.metaschema.core.datatype.markup.MarkupMultiline;
import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.InvalidMetapathGrammarException;
import dev.metaschema.core.metapath.MetapathException;
import dev.metaschema.core.metapath.item.node.INodeItem;
import dev.metaschema.core.model.IAttributable;
import dev.metaschema.core.model.ISource;
import dev.metaschema.core.model.constraint.ConstraintInitializationException;
import dev.metaschema.core.model.constraint.ConstraintValidationException;
import dev.metaschema.core.model.constraint.IConfigurableMessageConstraint;
import dev.metaschema.core.model.constraint.IConstraint;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.core.util.StringUtils;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * The base class for all constraint implementations that allow a configurable
 * message.
 *
 * @since 2.0.0
 */
public abstract class AbstractConfigurableMessageConstraint
    extends AbstractConstraint
    implements IConfigurableMessageConstraint {
  @NonNull
  private static final Pattern METAPATH_VALUE_TEMPLATE_PATTERN
      = ObjectUtils.notNull(Pattern.compile("(?<!\\\\)(\\{\\s*((?:(?:\\\\})|[^}])*)\\s*\\})"));

  @Nullable
  private final String message;

  /**
   * Construct a new Metaschema constraint.
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
   * @param message
   *          an optional message to emit when the constraint is violated
   * @param remarks
   *          optional remarks describing the intent of the constraint
   */
  protected AbstractConfigurableMessageConstraint(
      @Nullable String id,
      @Nullable String formalName,
      @Nullable MarkupLine description,
      @NonNull ISource source,
      @NonNull Level level,
      @NonNull IMetapathExpression target,
      @NonNull Map<IAttributable.Key, Set<String>> properties,
      @Nullable String message,
      @Nullable MarkupMultiline remarks) {
    super(id, formalName, description, source, level, target, properties, remarks);
    this.message = message;
  }

  @Override
  public String getMessage() {
    return message;
  }

  @Override
  public String generateMessage(@NonNull INodeItem item, @NonNull DynamicContext context)
      throws ConstraintValidationException {
    String message = getMessage();
    if (message == null) {
      throw new ConstraintInitializationException(
          String.format("A custom message is not defined in the constraint %s in %s.",
              IConstraint.getConstraintIdentity(this),
              getSource().getLocationHint()));
    }
    try {
      return ObjectUtils.notNull(StringUtils.replaceTokens(message, METAPATH_VALUE_TEMPLATE_PATTERN, match -> {
        String metapath = ObjectUtils.notNull(match.group(2));
        IMetapathExpression expr = IMetapathExpression.compile(
            metapath,
            // need to use the static context of the source to resolve prefixes, etc., since
            // this is where the message is defined
            getSource().getStaticContext());
        return expr.evaluateAs(
            item,
            IMetapathExpression.ResultType.STRING,
            // here we are using the static context of the instance, since this is how
            // variables and nodes are resolved.
            context);
      }).toString());
    } catch (InvalidMetapathGrammarException ex) {
      throw new ConstraintValidationException(
          String.format("Unable to compile a message replacement expression in constraint '%s'. %s",
              IConstraint.getConstraintIdentity(this),
              ex.getLocalizedMessage()),
          ex);
    } catch (MetapathException ex) {
      throw new ConstraintValidationException(
          String.format("Unable to evaluate a message replacement expression in constraint '%s'. %s",
              IConstraint.getConstraintIdentity(this),
              ex.getLocalizedMessage()),
          ex);
    }
  }
}
