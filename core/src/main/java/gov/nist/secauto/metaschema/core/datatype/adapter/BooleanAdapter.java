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
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBooleanItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.INumericItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
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

  @Override
  protected IBooleanItem castInternal(@NonNull IAnyAtomicItem item) {
    IBooleanItem retval;
    if (item instanceof INumericItem) {
      retval = castToBoolean((INumericItem) item);
    } else if (item instanceof IStringItem) {
      retval = castToBoolean((IStringItem) item);
    } else {
      try {
        retval = castToBoolean(item.asStringItem());
      } catch (IllegalStateException ex) {
        throw new InvalidValueForCastFunctionException(ex.getLocalizedMessage(), ex);
      }
    }
    return retval;
  }

  /**
   * Cast the provided numeric value to a boolean. Any non-zero value will be
   * {@code true}, or {@code false} otherwise.
   *
   * @param item
   *          the item to cast
   * @return {@code true} if the item value is non-zero, or {@code false}
   *         otherwise
   */
  @NonNull
  protected IBooleanItem castToBoolean(@NonNull INumericItem item) {
    return IBooleanItem.valueOf(item.toEffectiveBoolean());
  }

  /**
   * If the string is a numeric value, treat it as so. Otherwise parse the value
   * as a boolean string.
   *
   * @param item
   *          the item to cast
   * @return the effective boolean value of the string
   * @throws InvalidValueForCastFunctionException
   *           if the provided item cannot be cast to a boolean value by any means
   */
  @NonNull
  protected IBooleanItem castToBoolean(@NonNull IStringItem item) {
    IBooleanItem retval;
    try {
      INumericItem numeric = INumericItem.cast(item);
      retval = castToBoolean(numeric);
    } catch (InvalidValueForCastFunctionException ex) {
      retval = super.castInternal(item);
    }
    return retval;
  }

}
