/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml.impl;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.MarkupLineDatatype;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.MarkupMultilineDatatype;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlTokenSource;

import java.io.IOException;
import java.io.StringWriter;

import edu.umd.cs.findbugs.annotations.NonNull;

// TODO: is this needed, or can we use methods on the markup implementations?
public final class MarkupStringConverter {
  private MarkupStringConverter() {
    // disable construction
  }

  /**
   * Converts HTML-like markup into a MarkupLine.
   *
   * @param content
   *          the content to convert
   * @return the equivalent formatted text as a MarkupLine
   * @throws IllegalArgumentException
   *           if the {@code content} argument contains malformed markup
   */
  @NonNull
  public static MarkupLine toMarkupString(@NonNull MarkupLineDatatype content) {
    String html = processHTML(content);
    return MarkupLine.fromHtml(html);
  }

  /**
   * Converts multiple lines of HTML-like markup into a MarkupMultiline.
   *
   * @param content
   *          the content to convert
   * @return the equivalent formatted text as a MarkupLine
   * @throws IllegalArgumentException
   *           if the {@code content} argument contains malformed markup
   */
  @NonNull
  public static MarkupMultiline toMarkupString(@NonNull MarkupMultilineDatatype content) {
    String html = processHTML(content);
    return MarkupMultiline.fromHtml(html);
  }

  /**
   * Convert a line of HTML-like markup into a an XmlBeans representation.
   *
   * @param markup
   *          the markup to convert
   * @return the XmlBeans object
   */
  @NonNull
  public static MarkupLineDatatype toMarkupLineDatatype(@NonNull MarkupLine markup) {
    MarkupLineDatatype retval = ObjectUtils.notNull(MarkupLineDatatype.Factory.newInstance());
    XmlbeansMarkupVisitor.visit(markup, IModule.XML_NAMESPACE, retval);
    return retval;
  }

  /**
   * Converts a set of XML tokens, which represent HTML content, into an HTML
   * string.
   *
   * @param content
   *          the content to convert
   * @return an HTML string
   * @throws IllegalArgumentException
   *           if the {@code content} argument contains malformed markup
   */
  @NonNull
  private static String processHTML(XmlTokenSource content) {
    XmlOptions options = new XmlOptions();
    options.setSaveInner();
    options.setSaveUseOpenFrag();
    StringWriter writer = new StringWriter();
    try {
      content.save(writer, options);
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    }
    return ObjectUtils.notNull(
        writer.toString().replaceFirst("^<frag\\:fragment[^>]+>", "").replaceFirst("</frag\\:fragment>$", ""));
  }
}
