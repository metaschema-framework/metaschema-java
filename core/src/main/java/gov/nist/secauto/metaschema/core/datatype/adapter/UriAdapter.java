/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;

import gov.nist.secauto.metaschema.core.datatype.AbstractDataTypeAdapter;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyUriItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

public class UriAdapter
    extends AbstractDataTypeAdapter<URI, IAnyUriItem> {
  @NonNull
  private static final List<QName> NAMES = ObjectUtils.notNull(
      List.of(new QName(MetapathConstants.NS_METAPATH.toASCIIString(), "uri")));

  UriAdapter() {
    super(URI.class);
  }

  @Override
  public List<QName> getNames() {
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
  public Class<IAnyUriItem> getItemClass() {
    return IAnyUriItem.class;
  }

  @Override
  public IAnyUriItem newItem(Object value) {
    URI item = toValue(value);
    return IAnyUriItem.valueOf(item);
  }

}
