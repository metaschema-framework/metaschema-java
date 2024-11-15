/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml.impl;

import com.vladsch.flexmark.parser.ListOptions;

import gov.nist.secauto.metaschema.core.datatype.markup.IMarkupString;
import gov.nist.secauto.metaschema.core.datatype.markup.flexmark.impl.AbstractMarkupWriter;
import gov.nist.secauto.metaschema.core.datatype.markup.flexmark.impl.IMarkupVisitor;
import gov.nist.secauto.metaschema.core.datatype.markup.flexmark.impl.IMarkupWriter;
import gov.nist.secauto.metaschema.core.datatype.markup.flexmark.impl.MarkupVisitor;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;

import java.util.Map;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

@SuppressWarnings("PMD.AvoidUncheckedExceptionsInSignatures") // intended
public class XmlbeansMarkupWriter
    extends AbstractMarkupWriter<XmlCursor, IllegalArgumentException> {

  /**
   * Write the provided markup to the provided object.
   *
   * @param markup
   *          the markup to write
   * @param namespace
   *          the XML namespace to use for markup elements
   * @param obj
   *          the XML beans object to write to
   */
  @SuppressWarnings("resource")
  public static void visit(@NonNull IMarkupString<?> markup, @NonNull String namespace,
      @NonNull XmlObject obj) {
    try (XmlCursor cursor = ObjectUtils.notNull(obj.newCursor())) {
      visit(markup, namespace, cursor);
    }
  }

  /**
   * Write the provided markup to the provided object.
   *
   * @param markup
   *          the markup to write
   * @param namespace
   *          the XML namespace to use for markup elements
   * @param cursor
   *          the XML beans cursor to write to
   */
  public static void visit(@NonNull IMarkupString<?> markup, @NonNull String namespace,
      @NonNull XmlCursor cursor) {
    IMarkupWriter<XmlCursor, IllegalArgumentException> writer = new XmlbeansMarkupWriter(
        namespace,
        markup.getFlexmarkFactory().getListOptions(),
        cursor);
    IMarkupVisitor<XmlCursor, IllegalArgumentException> visitor = new MarkupVisitor<>(markup.isBlock());
    visitor.visitDocument(markup.getDocument(), writer);
  }

  /**
   * Construct a new XML beans markup visitor used for writing XML.
   *
   * @param namespace
   *          the XML namespace to use for markup elements
   * @param options
   *          Flexmark-based formatting options to control output formatting
   * @param writer
   *          the XML beans cursor to write to
   */
  protected XmlbeansMarkupWriter(
      @NonNull String namespace,
      @NonNull ListOptions options,
      @NonNull XmlCursor writer) {
    super(namespace, options, writer);
  }

  @Override
  public void writeEmptyElement(QName qname, Map<String, String> attributes)
      throws IllegalArgumentException {
    @SuppressWarnings({ "resource", "PMD.CloseResource" }) // not owned
    XmlCursor cursor = getStream();
    cursor.beginElement(qname);

    attributes.forEach(cursor::insertAttributeWithValue);

    // go to the end of the new element
    cursor.toEndToken();

    // state advance past the end element
    cursor.toNextToken();
  }

  @Override
  public void writeElementStart(QName qname, Map<String, String> attributes)
      throws IllegalArgumentException {
    @SuppressWarnings({ "resource", "PMD.CloseResource" }) // not owned
    XmlCursor cursor = getStream();
    cursor.beginElement(qname);

    attributes.forEach(cursor::insertAttributeWithValue);

    // save the current location state
    cursor.push();
  }

  @Override
  public void writeElementEnd(QName qname) throws IllegalArgumentException {
    @SuppressWarnings({ "resource", "PMD.CloseResource" }) // not owned
    XmlCursor cursor = getStream();

    // restore location to end of start element
    cursor.pop();

    // go to the end of the new element
    cursor.toEndToken();

    // state advance past the end element
    cursor.toNextToken();
  }

  @Override
  public void writeText(CharSequence text) throws IllegalArgumentException {
    @SuppressWarnings({ "resource", "PMD.CloseResource" }) // not owned
    XmlCursor cursor = getStream();
    cursor.insertChars(text.toString());
  }

  @Override
  protected void writeComment(CharSequence text) throws IllegalArgumentException {
    @SuppressWarnings({ "resource", "PMD.CloseResource" }) // not owned
    XmlCursor cursor = getStream();
    cursor.insertComment(text.toString());
  }
}
