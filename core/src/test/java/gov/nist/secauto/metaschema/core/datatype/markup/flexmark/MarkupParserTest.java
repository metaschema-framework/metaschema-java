/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.markup.flexmark;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.ctc.wstx.stax.WstxInputFactory;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.model.util.XmlEventUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.stax2.XMLEventReader2;
import org.codehaus.stax2.XMLInputFactory2;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

class MarkupParserTest {
  private static final Logger LOGGER = LogManager.getLogger(MarkupParserTest.class);

  @Test
  void test() throws XMLStreamException {
    XMLInputFactory2 factory = (XMLInputFactory2) XMLInputFactory.newInstance();
    assert factory instanceof WstxInputFactory;
    factory.configureForXmlConformance();
    factory.setProperty(XMLInputFactory.IS_COALESCING, true);

    String html = new StringBuilder()
        .append("<node>\n")
        .append("  <p> some text </p>\n")
        .append("  <p><q>text</q></p>\n")
        .append("  <p>some <b>text</b> <insert param-id=\"param-id\"/>.</p>\n")
        .append("  <h1>Example</h1>\n")
        .append("  <p><a href=\"link\">text</a></p>\n")
        .append("  <ul>\n")
        .append("    <li>a <strong>list item</strong></li>\n")
        .append("    <li>another <i>list item</i></li>\n")
        .append("  </ul>\n")
        .append(" <table>\n")
        .append(" <tr><th>Heading 1</th></tr>\n")
        .append(" <tr><td><q>data1</q> <insert param-id=\"insert\" /></td></tr>\n")
        .append(" </table>\n")
        .append("  <p>Some <em>more</em> <strong>text</strong><img alt=\"alt\" src=\"src\"/></p>\n")
        .append("</node>\n")
        .toString();

    XMLEventReader2 reader = (XMLEventReader2) factory.createXMLEventReader(new StringReader(html));

    CharSequence startDocument = XmlEventUtil.toString(reader.nextEvent());
    LOGGER.atDebug().log("StartDocument: {}", startDocument);

    CharSequence startElement = XmlEventUtil.toString(reader.nextEvent());
    LOGGER.atDebug().log("StartElement: {}", startElement);

    assertDoesNotThrow(() -> {
      MarkupMultiline markupString = XmlMarkupParser.instance().parseMarkupMultiline(reader);
      AstCollectingVisitor.asString(markupString.getDocument());
      // System.out.println(html);
      // System.out.println(visitor.getAst());
      // System.out.println(markupString.toMarkdown());

    });
  }

  @Test
  void emptyParagraphTest() throws XMLStreamException {
    final String html = new StringBuilder()
        .append("<node>\n")
        .append("  <p/>\n")
        .append("</node>\n")
        .toString();

    XMLInputFactory2 factory = (XMLInputFactory2) XMLInputFactory.newInstance();
    assert factory instanceof WstxInputFactory;
    factory.configureForXmlConformance();
    factory.setProperty(XMLInputFactory.IS_COALESCING, true);
    XMLEventReader2 reader = (XMLEventReader2) factory.createXMLEventReader(new StringReader(html));

    CharSequence startDocument = XmlEventUtil.toString(reader.nextEvent());
    LOGGER.atDebug().log("StartDocument: {}", startDocument);

    CharSequence startElement = XmlEventUtil.toString(reader.nextEvent());
    LOGGER.atDebug().log("StartElement: {}", startElement);

    assertDoesNotThrow(() -> {
      MarkupMultiline ms = XmlMarkupParser.instance().parseMarkupMultiline(reader);
      LOGGER.atDebug().log("AST: {}", AstCollectingVisitor.asString(ms.getDocument()));
      LOGGER.atDebug().log("HTML: {}", ms.toXHtml(""));
      LOGGER.atDebug().log("Markdown: {}", ms.toMarkdown());
    });
  }
}
