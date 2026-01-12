/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.datatype.adapter;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;

import java.net.URI;
import java.util.List;

import dev.metaschema.core.datatype.AbstractDataTypeAdapter;
import dev.metaschema.core.metapath.MetapathConstants;
import dev.metaschema.core.metapath.item.atomic.IUriReferenceItem;
import dev.metaschema.core.qname.EQNameFactory;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Support for the Metaschema <a href=
 * "https://pages.nist.gov/metaschema/specification/datatypes/#uri-reference">uri-reference</a>
 * data type.
 */
public class UriReferenceAdapter
    extends AbstractDataTypeAdapter<URI, IUriReferenceItem> {
  @NonNull
  private static final List<IEnhancedQName> NAMES = ObjectUtils.notNull(
      List.of(
          EQNameFactory.instance().newQName(MetapathConstants.NS_METAPATH, "uri-reference")));

  UriReferenceAdapter() {
    super(URI.class, IUriReferenceItem.class, IUriReferenceItem::cast);
  }

  @Override
  public List<IEnhancedQName> getNames() {
    return NAMES;
  }

  @Override
  public JsonFormatTypes getJsonRawType() {
    return JsonFormatTypes.STRING;
  }

  @SuppressWarnings("null")
  @Override
  public URI parse(String value) {
    return URI.create(value);
  }

  @Override
  public URI copy(Object obj) {
    // a URI is immutable
    return (URI) obj;
  }

  @Override
  public IUriReferenceItem newItem(Object value) {
    URI item = toValue(value);
    return IUriReferenceItem.valueOf(item);
  }
}
