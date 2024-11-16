/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.markup.flexmark;

import com.vladsch.flexmark.ext.typographic.TypographicQuotes;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import com.vladsch.flexmark.html2md.converter.HtmlMarkdownWriter;
import com.vladsch.flexmark.html2md.converter.HtmlNodeConverterContext;
import com.vladsch.flexmark.html2md.converter.HtmlNodeRenderer;
import com.vladsch.flexmark.html2md.converter.HtmlNodeRendererFactory;
import com.vladsch.flexmark.html2md.converter.HtmlNodeRendererHandler;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.block.NodePostProcessor;
import com.vladsch.flexmark.parser.block.NodePostProcessorFactory;
import com.vladsch.flexmark.util.ast.DoNotDecorate;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeTracker;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataHolder;

import org.jsoup.nodes.Element;

import java.util.Collections;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Adds support for the use of "q" tags in HTML to replace quotation marks.
 * These are translated to double quotes in Markdown.
 */
public class HtmlQuoteTagExtension
    implements Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension,
    FlexmarkHtmlConverter.HtmlConverterExtension {

  /**
   * Construct a new extension instance.
   *
   * @return the instance
   */
  public static HtmlQuoteTagExtension newInstance() {
    return new HtmlQuoteTagExtension();
  }

  @Override
  public void rendererOptions(MutableDataHolder options) {
    // do nothing
  }

  @Override
  public void parserOptions(MutableDataHolder options) {
    // do nothing
  }

  @Override
  public void extend(HtmlRenderer.Builder rendererBuilder, String rendererType) {
    rendererBuilder.nodeRendererFactory(new QTagNodeRenderer.Factory());
  }

  @Override
  public void extend(Parser.Builder parserBuilder) {
    parserBuilder.postProcessorFactory(new QuoteReplacingPostProcessor.Factory());
  }

  @Override
  public void extend(FlexmarkHtmlConverter.Builder builder) {
    builder.htmlNodeRendererFactory(new QTagHtmlNodeRenderer.Factory());
  }

  static class QTagNodeRenderer implements NodeRenderer {

    @Override
    @Nullable
    public Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
      return Collections.singleton(
          new NodeRenderingHandler<>(DoubleQuoteNode.class, this::render));
    }

    protected void render(@NonNull DoubleQuoteNode node, @NonNull NodeRendererContext context,
        @NonNull HtmlWriter html) {
      html.withAttr().tag("q");
      context.renderChildren(node);
      html.tag("/q");
    }

    public static class Factory implements NodeRendererFactory {

      @Override
      public NodeRenderer apply(DataHolder options) {
        return new QTagNodeRenderer();
      }

    }
  }

  static class QuoteReplacingPostProcessor
      extends NodePostProcessor {

    @Override
    public void process(NodeTracker state, Node node) {
      if (node instanceof TypographicQuotes) {
        TypographicQuotes typographicQuotes = (TypographicQuotes) node;
        if (typographicQuotes.getOpeningMarker().matchChars("\"")) {
          DoubleQuoteNode quoteNode = new DoubleQuoteNode(typographicQuotes);
          node.insertAfter(quoteNode);
          state.nodeAdded(quoteNode);
          node.unlink();
          state.nodeRemoved(node);
        }
      }
    }

    public static class Factory
        extends NodePostProcessorFactory {
      public Factory() {
        super(false);
        addNodeWithExclusions(TypographicQuotes.class, DoNotDecorate.class);
      }

      @NonNull
      @Override
      public NodePostProcessor apply(Document document) {
        return new QuoteReplacingPostProcessor();
      }
    }
  }

  static class QTagHtmlNodeRenderer implements HtmlNodeRenderer {

    @Override
    public Set<HtmlNodeRendererHandler<?>> getHtmlNodeRendererHandlers() {
      return Collections.singleton(new HtmlNodeRendererHandler<>("q", Element.class, this::renderMarkdown));
    }

    protected void renderMarkdown(
        Element element,
        HtmlNodeConverterContext context,
        @SuppressWarnings("unused") HtmlMarkdownWriter out) {
      context.wrapTextNodes(element, "\"", element.nextElementSibling() != null);
    }

    public static class Factory implements HtmlNodeRendererFactory {

      @Override
      public HtmlNodeRenderer apply(DataHolder options) {
        return new QTagHtmlNodeRenderer();
      }
    }

  }

  /**
   * A Flexmark node implementation representing a quotation mark.
   */
  public static final class DoubleQuoteNode
      extends TypographicQuotes {

    /**
     * Construct a new double quote node.
     *
     * @param node
     *          the typographic information pertaining to a double quote
     */
    public DoubleQuoteNode(TypographicQuotes node) {
      super(node.getOpeningMarker(), node.getText(), node.getClosingMarker());
      setTypographicOpening(node.getTypographicOpening());
      setTypographicClosing(node.getTypographicClosing());
      for (Node child : node.getChildren()) {
        appendChild(child);
      }
    }
  }
}
