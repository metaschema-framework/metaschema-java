/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.markup.flexmark;

import com.ctc.wstx.api.WstxOutputProperties;
import com.ctc.wstx.stax.WstxOutputFactory;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;

import org.codehaus.stax2.XMLOutputFactory2;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.ri.evt.MergedNsContext;
import org.codehaus.stax2.ri.evt.NamespaceEventImpl;
import org.junit.jupiter.api.Test;

import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

class MarkupXmlStreamWriterTest {
  private static final String NAMESPACE = "http://www.w3.org/1999/xhtml";
  private static final String NS_PREFIX = "";

  @Test
  void testHTML() throws XMLStreamException {

    XMLOutputFactory2 factory = (XMLOutputFactory2) XMLOutputFactory.newInstance();
    assert factory instanceof WstxOutputFactory;
    factory.setProperty(WstxOutputProperties.P_OUTPUT_VALIDATE_STRUCTURE, false);
    XMLStreamWriter2 xmlStreamWriter = (XMLStreamWriter2) factory.createXMLStreamWriter(System.out);
    NamespaceContext nsContext = MergedNsContext.construct(xmlStreamWriter.getNamespaceContext(),
        List.of(NamespaceEventImpl.constructNamespace(null, NS_PREFIX, NAMESPACE)));
    xmlStreamWriter.setNamespaceContext(nsContext);

    String html = "<h1>Example</h1>\n"
        + "<p><a href=\"link\">text</a><q>quote1</q></p>\n"
        + "<table>\n"
        + "<thead>\n"
        + "<tr><th>Heading 1</th></tr>\n"
        + "</thead>\n"
        + "<tbody>\n"
        + "<tr><td><q>data1</q> <insert type=\"param\" id-ref=\"insert\" /></td></tr>\n"
        + "<tr><td><q>data2</q> <insert type=\"param\" id-ref=\"insert\" /></td></tr>\n"
        + "</tbody>\n"
        + "</table>\n"
        + "<p>Some <q><em>more</em></q> <strong>text</strong> <img src=\"src\" alt=\"alt\" /></p>\n";

    MarkupMultiline ms = MarkupMultiline.fromHtml(html);
    // System.out.println(AstCollectingVisitor.asString(ms.getDocument()));

    ms.writeXHtml(NAMESPACE, xmlStreamWriter);
    xmlStreamWriter.close();
  }

}
