/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.markup;

import com.fasterxml.jackson.core.JsonParser;

import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IMarkupItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.codehaus.stax2.XMLEventReader2;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Support for the Metaschema <a href=
 * "https://pages.nist.gov/metaschema/specification/datatypes/#markup-line">markup-line</a>
 * data type.
 */
public class MarkupLineAdapter
    extends AbstractMarkupAdapter<MarkupLine> {
  @NonNull
  private static final List<QName> NAMES = ObjectUtils.notNull(
      List.of(new QName(MetapathConstants.NS_METAPATH.toASCIIString(), "markup-line")));

  MarkupLineAdapter() {
    super(MarkupLine.class);
  }

  @Override
  public List<QName> getNames() {
    return NAMES;
  }

  /**
   * Parse a line of Markdown.
   */
  @Override
  public MarkupLine parse(String value) {
    return MarkupLine.fromMarkdown(value);
  }

  @SuppressWarnings("null")
  @Override
  public MarkupLine parse(XMLEventReader2 eventReader, URI resource) throws IOException {
    try {
      return XmlMarkupParser.instance().parseMarkupline(eventReader, resource);
    } catch (XMLStreamException ex) {
      throw new IOException(ex);
    }
  }

  @Override
  public MarkupLine parse(JsonParser parser, URI resource) throws IOException {
    @SuppressWarnings("null")
    MarkupLine retval = parse(parser.getValueAsString());
    // skip past value
    parser.nextToken();
    return retval;
  }

  @Override
  public String getDefaultJsonValueKey() {
    return "RICHTEXT";
  }

  @Override
  public Class<IMarkupItem> getItemClass() {
    return IMarkupItem.class;
  }

  @Override
  public IMarkupItem newItem(Object value) {
    MarkupLine item = toValue(value);
    return IMarkupItem.valueOf(item);
  }
}
