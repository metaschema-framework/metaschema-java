/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.model.xml.impl.XmlbeansMarkupVisitor;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.METASCHEMADocument;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.METASCHEMADocument.METASCHEMA;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.MarkupLineDatatype;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlOptions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;

import edu.umd.cs.findbugs.annotations.NonNull;

class XmlbeansMarkupVisitorTest {

  @Test
  void testText() throws IOException {
    String html = testMarkupLine("this is some basic text");
    assertNotNull(html, "not null");
    // System.out.println(html);
  }

  @Test
  void testQuote() throws IOException {
    String html = testMarkupLine("this is some \"basic text\"");
    assertNotNull(html, "not null");
    // System.out.println(html);
  }

  @Test
  void testLink() throws IOException {
    String html = testMarkupLine("this is some basic text with a [link](url/).");
    assertNotNull(html, "not null");
    // System.out.println(html);
  }

  @Test
  void testComplex() throws IOException {
    String html = testMarkupLine(
        "this is some \"quoted *basic text*\" with a [**bold** link](url/).");
    assertNotNull(html, "not null");
    // System.out.println(html);
  }

  @NonNull
  private static String testMarkupLine(@NonNull String markdown) throws IOException {
    MarkupLine markup = MarkupLine.fromMarkdown(markdown);

    XmlOptions options = new XmlOptions();
    options.setSaveAggressiveNamespaces(true);
    options.setUseDefaultNamespace(true);

    METASCHEMADocument metaschemaDocument = METASCHEMADocument.Factory.newInstance();
    METASCHEMA metaschema = metaschemaDocument.addNewMETASCHEMA();
    MarkupLineDatatype xmlData = metaschema.addNewSchemaName();

    try (XmlCursor cursor = xmlData.newCursor()) {
      cursor.toEndToken();

      XmlbeansMarkupVisitor.visit(markup, "http://csrc.nist.gov/ns/oscal/metaschema/1.0", cursor);

      try (StringWriter writer = new StringWriter()) {
        metaschemaDocument.save(writer, options);
        return ObjectUtils.notNull(writer.toString());
      }
    }
  }
}
