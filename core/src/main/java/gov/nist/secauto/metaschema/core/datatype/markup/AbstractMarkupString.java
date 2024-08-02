/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.markup;

import com.ctc.wstx.api.WstxOutputProperties;
import com.ctc.wstx.stax.WstxOutputFactory;
import com.vladsch.flexmark.formatter.Formatter;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;

import gov.nist.secauto.metaschema.core.datatype.markup.flexmark.AstCollectingVisitor;
import gov.nist.secauto.metaschema.core.datatype.markup.flexmark.FlexmarkFactory;
import gov.nist.secauto.metaschema.core.datatype.markup.flexmark.IMarkupVisitor;
import gov.nist.secauto.metaschema.core.datatype.markup.flexmark.IMarkupWriter;
import gov.nist.secauto.metaschema.core.datatype.markup.flexmark.InsertAnchorExtension;
import gov.nist.secauto.metaschema.core.datatype.markup.flexmark.InsertAnchorExtension.InsertAnchorNode;
import gov.nist.secauto.metaschema.core.datatype.markup.flexmark.MarkupVisitor;
import gov.nist.secauto.metaschema.core.datatype.markup.flexmark.MarkupXmlEventWriter;
import gov.nist.secauto.metaschema.core.datatype.markup.flexmark.MarkupXmlStreamWriter;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.stax2.XMLOutputFactory2;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.evt.XMLEventFactory2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

@SuppressWarnings("PMD.CouplingBetweenObjects")
public abstract class AbstractMarkupString<TYPE extends AbstractMarkupString<TYPE>>
    implements IMarkupString<TYPE> {
  private static final Logger LOGGER = LogManager.getLogger(FlexmarkFactory.class);

  private static final Pattern QUOTE_TAG_REPLACEMENT_PATTERN
      = Pattern.compile("</?q>");

  //
  // @NonNull
  // private static final String DEFAULT_HTML_NS = "http://www.w3.org/1999/xhtml";
  // @NonNull
  // private static final String DEFAULT_HTML_PREFIX = "";

  @NonNull
  private final Document document;

  /**
   * Construct a new markup string based on the provided flexmark AST graph.
   *
   * @param document
   *          the AST graph representing Markdown text
   */
  protected AbstractMarkupString(@NonNull Document document) {
    this.document = document;
  }

  @Override
  public Document getDocument() {
    return document;
  }

  @Override
  public boolean isEmpty() {
    return getDocument().getFirstChild() == null;
  }

  /**
   * Parse HTML-based text into markdown as a flexmark AST graph.
   * <p>
   * This method uses a two-step approach that first translates the HTML into
   * markdown, and then parses the markdown into an AST graph.
   *
   * @param html
   *          the HTML text to parse
   * @param htmlParser
   *          the HTML parser used to produce markdown
   * @param markdownParser
   *          the markdown parser
   * @return the markdown AST graph
   */
  @NonNull
  protected static Document parseHtml(@NonNull String html, @NonNull FlexmarkHtmlConverter htmlParser,
      @NonNull Parser markdownParser) {
    org.jsoup.nodes.Document document = Jsoup.parse(html);

    // Fix for usnistgov/liboscal-java#5
    // Caused by not stripping out extra newlines inside HTML tags
    NodeTraversor.traverse(new NodeVisitor() {

      @Override
      public void head(org.jsoup.nodes.Node node, int depth) {
        if (node instanceof TextNode) {
          TextNode textNode = (TextNode) node;

          org.jsoup.nodes.Node parent = textNode.parent();

          if (!isTag(parent, "code") || !isTag(parent.parent(), "pre")) {
            node.replaceWith(new TextNode(textNode.text()));
          }
        }
      }

      private boolean isTag(@Nullable org.jsoup.nodes.Node node, @NonNull String tagName) {
        return node != null && tagName.equals(node.normalName());
      }

    }, document);

    String markdown = htmlParser.convert(document);
    assert markdown != null;
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("html->markdown: {}", markdown);
    }
    return parseMarkdown(markdown, markdownParser);
  }

  /**
   * Parse markdown-based text into a flexmark AST graph.
   *
   * @param markdown
   *          the markdown text to parse
   * @param parser
   *          the markdown parser
   * @return the markdown AST graph
   */
  @SuppressWarnings("null")
  @NonNull
  protected static Document parseMarkdown(@NonNull String markdown, @NonNull Parser parser) {
    return parser.parse(markdown);
  }

  @Override
  public String toXHtml(@NonNull String namespace) throws XMLStreamException, IOException {

    String retval;

    Document document = getDocument();
    if (document.hasChildren()) {

      XMLOutputFactory2 factory = (XMLOutputFactory2) XMLOutputFactory.newInstance();
      assert factory instanceof WstxOutputFactory;
      factory.setProperty(WstxOutputProperties.P_OUTPUT_VALIDATE_STRUCTURE, false);
      try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
        XMLStreamWriter2 xmlStreamWriter = (XMLStreamWriter2) factory.createXMLStreamWriter(os);

        writeXHtml(namespace, ObjectUtils.notNull(xmlStreamWriter));

        xmlStreamWriter.flush();
        xmlStreamWriter.close();
        os.flush();
        retval = ObjectUtils.notNull(os.toString(StandardCharsets.UTF_8));
      }
    } else {
      retval = "";
    }
    return retval;
  }

  @Override
  public String toHtml() {
    // String html;
    // try {
    // html = toXHtml("");
    // } catch(RuntimeException ex) {
    // throw ex;
    // } catch (Throwable ex) {
    // throw new RuntimeException(ex);
    // }
    // return QUOTE_TAG_REPLACEMENT_PATTERN.matcher(html)
    // .replaceAll("&quot;");
    String html = getFlexmarkFactory().getHtmlRenderer().render(getDocument());
    return ObjectUtils.notNull(QUOTE_TAG_REPLACEMENT_PATTERN.matcher(html)
        .replaceAll("&quot;"));
  }

  @Override
  public String toMarkdown() {
    return toMarkdown(getFlexmarkFactory().getFormatter());
  }

  @Override
  public String toMarkdown(Formatter formatter) {
    return ObjectUtils.notNull(formatter.render(getDocument()));
  }

  @Override
  public void writeXHtml(String namespace, XMLStreamWriter2 streamWriter) throws XMLStreamException {
    Document document = getDocument();
    if (document.hasChildren()) {
      IMarkupWriter<XMLStreamWriter, XMLStreamException> writer = new MarkupXmlStreamWriter(
          namespace,
          getFlexmarkFactory().getListOptions(),
          streamWriter);

      IMarkupVisitor<XMLStreamWriter, XMLStreamException> visitor = new MarkupVisitor<>(isBlock());
      visitor.visitDocument(document, writer);
    } else {
      streamWriter.writeCharacters("");
    }
  }

  @Override
  public void writeXHtml(String namespace, XMLEventFactory2 eventFactory, XMLEventWriter eventWriter)
      throws XMLStreamException {
    Document document = getDocument();
    if (document.hasChildren()) {

      IMarkupWriter<XMLEventWriter, XMLStreamException> writer = new MarkupXmlEventWriter(
          namespace,
          getFlexmarkFactory().getListOptions(),
          eventWriter,
          eventFactory);

      IMarkupVisitor<XMLEventWriter, XMLStreamException> visitor = new MarkupVisitor<>(isBlock());
      visitor.visitDocument(getDocument(), writer);
    } else {
      eventWriter.add(eventFactory.createSpace(""));
    }

  }

  @SuppressWarnings("null")
  @Override
  public Stream<Node> getNodesAsStream() {
    return Stream.concat(Stream.of(getDocument()),
        StreamSupport.stream(getDocument().getDescendants().spliterator(), false));
  }

  @Override
  @NonNull
  public List<InsertAnchorNode> getInserts() {
    return getInserts(insert -> true);
  }

  /**
   * Retrieve all insert statements that are contained within this markup text
   * that match the provided filter.
   *
   * @param filter
   *          a filter used to identify matching insert statements
   * @return the matching insert statements
   */
  @Override
  @NonNull
  public List<InsertAnchorNode> getInserts(@NonNull Predicate<InsertAnchorNode> filter) {
    InsertAnchorExtension.InsertVisitor visitor = new InsertAnchorExtension.InsertVisitor(filter);
    visitor.visitChildren(getDocument());
    return visitor.getInserts();
  }

  @Override
  public String toString() {
    return AstCollectingVisitor.asString(getDocument());
  }
}
