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
import dev.metaschema.core.metapath.item.atomic.IAnyUriItem;
import dev.metaschema.core.qname.EQNameFactory;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Support for the Metaschema <a href=
 * "https://pages.nist.gov/metaschema/specification/datatypes/#uri">uri</a> data
 * type.
 */
public class UriAdapter
    extends AbstractDataTypeAdapter<URI, IAnyUriItem> {
  @NonNull
  private static final List<IEnhancedQName> NAMES = ObjectUtils.notNull(
      List.of(
          EQNameFactory.instance().newQName(MetapathConstants.NS_METAPATH, "uri")));

  UriAdapter() {
    super(URI.class, IAnyUriItem.class, IAnyUriItem::cast);
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
  public IAnyUriItem newItem(Object value) {
    URI item = toValue(value);
    return IAnyUriItem.valueOf(item);
  }

}
