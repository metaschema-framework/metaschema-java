/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.markup;

import com.fasterxml.jackson.core.JsonParser;

import gov.nist.secauto.metaschema.core.datatype.markup.flexmark.XmlMarkupParser;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IMarkupItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.codehaus.stax2.XMLEventReader2;

import java.io.IOException;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import edu.umd.cs.findbugs.annotations.NonNull;

public class MarkupMultilineAdapter
    extends AbstractMarkupAdapter<MarkupMultiline> {
  @NonNull
  private static final List<QName> NAMES = ObjectUtils.notNull(
      List.of(new QName(MetapathConstants.NS_METAPATH.toASCIIString(), "markup-multiline")));

  MarkupMultilineAdapter() {
    super(MarkupMultiline.class);
  }

  @Override
  public List<QName> getNames() {
    return NAMES;
  }

  @Override
  public boolean isUnrappedValueAllowedInXml() {
    return true;
  }

  /**
   * Parse a line of Markdown.
   */
  @Override
  public MarkupMultiline parse(String value) {
    return MarkupMultiline.fromMarkdown(value);
  }

  @SuppressWarnings("null")
  @Override
  public MarkupMultiline parse(XMLEventReader2 eventReader) throws IOException {
    try {
      return XmlMarkupParser.instance().parseMarkupMultiline(eventReader);
    } catch (XMLStreamException ex) {
      throw new IOException(ex);
    }
  }

  @Override
  public MarkupMultiline parse(JsonParser parser) throws IOException {
    @SuppressWarnings("null") MarkupMultiline retval = parse(parser.getValueAsString());
    // skip past value
    parser.nextToken();
    return retval;
  }

  @Override
  public boolean canHandleQName(QName nextQName) {
    return true;
  }

  @Override
  public String getDefaultJsonValueKey() {
    return "PROSE";
  }

  @Override
  public Class<IMarkupItem> getItemClass() {
    return IMarkupItem.class;
  }

  @Override
  public IMarkupItem newItem(Object value) {
    MarkupMultiline item = toValue(value);
    return IMarkupItem.valueOf(item);
  }
}
