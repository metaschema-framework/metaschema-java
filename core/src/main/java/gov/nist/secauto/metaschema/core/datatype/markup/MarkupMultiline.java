/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.markup;

import com.vladsch.flexmark.util.ast.Document;

import gov.nist.secauto.metaschema.core.datatype.markup.flexmark.FlexmarkFactory;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Supports a data value which may be multiple lines of markup.
 * <p>
 * This markup can be presented as XHTML or Markdown.
 */
public class MarkupMultiline
    extends AbstractMarkupString<MarkupMultiline> {

  @NonNull
  private static final FlexmarkFactory FLEXMARK_FACTORY = FlexmarkFactory.instance();

  /**
   * Convert the provided HTML string into markup.
   *
   * @param html
   *          the HTML
   * @return the markup instance
   */
  @NonNull
  public static MarkupMultiline fromHtml(@NonNull String html) {
    return new MarkupMultiline(
        parseHtml(
            html,
            FLEXMARK_FACTORY.getFlexmarkHtmlConverter(),
            FLEXMARK_FACTORY.getMarkdownParser()));
  }

  /**
   * Convert the provided markdown string into markup.
   *
   * @param markdown
   *          the markup
   * @return the markup instance
   */
  @NonNull
  public static MarkupMultiline fromMarkdown(@NonNull String markdown) {
    return new MarkupMultiline(
        parseMarkdown(markdown, FLEXMARK_FACTORY.getMarkdownParser()));
  }

  /**
   * Construct a new multiline markup instance.
   *
   * @param astNode
   *          the parsed markup AST
   */
  public MarkupMultiline(@NonNull Document astNode) {
    super(astNode);
  }

  @Override
  public FlexmarkFactory getFlexmarkFactory() {
    return FLEXMARK_FACTORY;
  }

  @Override
  public MarkupMultiline copy() {
    // TODO: find a way to do a deep copy
    // this is a shallow copy that uses the same underlying Document object
    return new MarkupMultiline(getDocument());
  }

  @Override
  public boolean isBlock() {
    return true;
  }
}
