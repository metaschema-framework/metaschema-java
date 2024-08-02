/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.markup;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Block;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataSet;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.util.misc.Extension;

import gov.nist.secauto.metaschema.core.datatype.markup.flexmark.FlexmarkConfiguration;
import gov.nist.secauto.metaschema.core.datatype.markup.flexmark.FlexmarkFactory;
import gov.nist.secauto.metaschema.core.datatype.markup.flexmark.SuppressPTagExtension;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

public final class MarkupLine
    extends AbstractMarkupString<MarkupLine> {

  @NonNull
  private static final DataSet FLEXMARK_CONFIG = newParserOptions();

  @NonNull
  private static final FlexmarkFactory FLEXMARK_FACTORY = FlexmarkFactory.newInstance(FLEXMARK_CONFIG);

  @SuppressWarnings("null")
  @NonNull
  private static DataSet newParserOptions() {
    MutableDataSet options = new MutableDataSet();
    // disable inline HTML
    options.set(Parser.HTML_BLOCK_PARSER, false);
    // disable list processing
    options.set(Parser.LIST_BLOCK_PARSER, false);
    options.set(HtmlRenderer.SUPPRESS_HTML_BLOCKS, true);

    Collection<Extension> currentExtensions = Parser.EXTENSIONS.get(FlexmarkConfiguration.FLEXMARK_CONFIG);
    List<Extension> extensions = new LinkedList<>(currentExtensions);
    extensions.add(SuppressPTagExtension.newInstance());
    Parser.EXTENSIONS.set(options, extensions);

    return FlexmarkConfiguration.newFlexmarkConfig(options);
  }

  /**
   * Convert the provided HTML string into markup.
   *
   * @param html
   *          the HTML
   * @return the markup instance
   */
  @NonNull
  public static MarkupLine fromHtml(@NonNull String html) {
    return new MarkupLine(
        parseHtml(html, FLEXMARK_FACTORY.getFlexmarkHtmlConverter(), FLEXMARK_FACTORY.getMarkdownParser()));
  }

  /**
   * Convert the provided markdown string into markup.
   *
   * @param markdown
   *          the markup
   * @return the markup instance
   */
  @NonNull
  public static MarkupLine fromMarkdown(@NonNull String markdown) {
    return new MarkupLine(parseMarkdown(markdown, FLEXMARK_FACTORY.getMarkdownParser()));
  }

  @Override
  public FlexmarkFactory getFlexmarkFactory() {
    return FLEXMARK_FACTORY;
  }

  /**
   * Construct a new single line markup instance.
   *
   * @param astNode
   *          the parsed markup AST
   */
  protected MarkupLine(@NonNull Document astNode) {
    super(astNode);
    Node child = astNode.getFirstChild();
    if (child instanceof Block && child.getNext() != null) {
      throw new IllegalStateException("multiple blocks not allowed");
    } // else empty markdown
  }

  @Override
  public MarkupLine copy() {
    // TODO: find a way to do a deep copy
    // this is a shallow copy that uses the same underlying Document object
    return new MarkupLine(getDocument());
  }

  @Override
  public boolean isBlock() {
    return false;
  }
}
