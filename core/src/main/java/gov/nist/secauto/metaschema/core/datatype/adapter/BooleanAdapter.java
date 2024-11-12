/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;

import gov.nist.secauto.metaschema.core.datatype.AbstractDataTypeAdapter;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBooleanItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Support for the Metaschema <a href=
 * "https://pages.nist.gov/metaschema/specification/datatypes/#boolean">boolean</a>
 * data type.
 */
public class BooleanAdapter
    extends AbstractDataTypeAdapter<Boolean, IBooleanItem> {
  @NonNull
  private static final List<QName> NAMES = ObjectUtils.notNull(
      List.of(new QName(MetapathConstants.NS_METAPATH.toASCIIString(), "boolean")));

  BooleanAdapter() {
    super(Boolean.class);
  }

  @Override
  public List<QName> getNames() {
    return NAMES;
  }

  @Override
  public JsonFormatTypes getJsonRawType() {
    return JsonFormatTypes.BOOLEAN;
  }

  @SuppressWarnings("null")
  @Override
  public Boolean parse(String value) {
    return Boolean.valueOf(value);
  }

  @Override
  public Boolean parse(JsonParser parser, URI resource) throws IOException {
    Boolean value = parser.getBooleanValue();
    // skip over value
    parser.nextToken();
    return value;
  }

  @Override
  public void writeJsonValue(Object value, JsonGenerator generator)
      throws IOException {
    try {
      generator.writeBoolean((Boolean) value);
    } catch (ClassCastException ex) {
      throw new IOException(ex);
    }
  }

  @Override
  public Boolean copy(Object obj) {
    // the value is immutable
    return (Boolean) obj;
  }

  @Override
  public Class<IBooleanItem> getItemClass() {
    return IBooleanItem.class;
  }

  @Override
  public IBooleanItem newItem(Object value) {
    Boolean item = toValue(value);
    return IBooleanItem.valueOf(item);
  }

}
