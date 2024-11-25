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
import gov.nist.secauto.metaschema.core.qname.EQNameFactory;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Support for the Metaschema <a href=
 * "https://pages.nist.gov/metaschema/specification/datatypes/#boolean">boolean</a>
 * data type.
 */
public class BooleanAdapter
    extends AbstractDataTypeAdapter<Boolean, IBooleanItem> {
  @NonNull
  private static final List<IEnhancedQName> NAMES = ObjectUtils.notNull(
      List.of(
          EQNameFactory.instance().newQName(MetapathConstants.NS_METAPATH, "boolean")));

  BooleanAdapter() {
    super(Boolean.class, IBooleanItem.class, IBooleanItem::cast);
  }

  @Override
  public List<IEnhancedQName> getNames() {
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
  public IBooleanItem newItem(Object value) {
    Boolean item = toValue(value);
    return IBooleanItem.valueOf(item);
  }

}
