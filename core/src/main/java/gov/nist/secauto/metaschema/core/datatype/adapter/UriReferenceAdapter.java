/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;

import gov.nist.secauto.metaschema.core.datatype.AbstractDataTypeAdapter;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IUriReferenceItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Support for the Metaschema <a href=
 * "https://pages.nist.gov/metaschema/specification/datatypes/#uri-reference">uri-reference</a>
 * data type.
 */
public class UriReferenceAdapter
    extends AbstractDataTypeAdapter<URI, IUriReferenceItem> {
  @NonNull
  private static final List<QName> NAMES = ObjectUtils.notNull(
      List.of(new QName(MetapathConstants.NS_METAPATH.toASCIIString(), "uri-reference")));

  UriReferenceAdapter() {
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
  public Class<IUriReferenceItem> getItemClass() {
    return IUriReferenceItem.class;
  }

  @Override
  public IUriReferenceItem newItem(Object value) {
    URI item = toValue(value);
    return IUriReferenceItem.valueOf(item);
  }
}
