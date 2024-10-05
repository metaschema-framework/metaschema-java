/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of the
 * <a href="https://www.w3.org/TR/xpath-31/#id-literals">String Literal
 * Expression</a> supporting the creation of a Metapath constant literal
 * {@link IStringItem}.
 */
public class StringLiteral
    extends AbstractLiteralExpression<IStringItem, String> {

  private static final Pattern QUOTE_PATTERN = Pattern.compile("(?:^'(.*)'$)|(?:^\"(.*)\"$)");

  /**
   * Construct a new expression that always returns the same string value.
   *
   * @param value
   *          the literal value
   */
  public StringLiteral(@NonNull String value) {
    super(removeQuotes(value));
  }

  @Override
  public Class<IStringItem> getBaseResultType() {
    return IStringItem.class;
  }

  @SuppressWarnings("null")
  @NonNull
  private static String removeQuotes(@NonNull String value) {
    return value.substring(1, value.length() - 1);
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitStringLiteral(this, context);
  }

  @Override
  public ISequence<? extends IStringItem> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    return ISequence.of(IStringItem.valueOf(getValue()));
  }
}
