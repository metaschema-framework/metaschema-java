/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;

import gov.nist.secauto.metaschema.core.datatype.AbstractDataTypeAdapter;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDateTimeWithTimeZoneItem;
import gov.nist.secauto.metaschema.core.qname.EQNameFactory;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.time.DateTimeException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Support for the Metaschema <a href=
 * "https://pages.nist.gov/metaschema/specification/datatypes/#date-time-with-timezone">date-time-with-timezone</a>
 * data type.
 */
public class DateTimeWithTZAdapter
    extends AbstractDataTypeAdapter<ZonedDateTime, IDateTimeWithTimeZoneItem> {
  @NonNull
  private static final List<IEnhancedQName> NAMES = ObjectUtils.notNull(
      List.of(
          EQNameFactory.instance().newQName(MetapathConstants.NS_METAPATH, "date-time-with-timezone"),
          // for backwards compatibility with original type name
          EQNameFactory.instance().newQName(MetapathConstants.NS_METAPATH, "dateTime-with-timezone")));

  DateTimeWithTZAdapter() {
    super(ZonedDateTime.class, IDateTimeWithTimeZoneItem.class, IDateTimeWithTimeZoneItem::cast);
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
  public ZonedDateTime parse(String value) {
    try {
      return ZonedDateTime.from(DateFormats.DATE_TIME_WITH_TZ.parse(value));
    } catch (DateTimeParseException ex) {
      throw new IllegalArgumentException(ex.getLocalizedMessage(), ex);
    }
  }

  @SuppressWarnings("null")
  @Override
  public String asString(Object value) {
    try {
      return DateFormats.DATE_TIME_WITH_TZ.format((ZonedDateTime) value);
    } catch (DateTimeException ex) {
      throw new IllegalArgumentException(
          String.format("The provided value '%s' cannot be formatted as a date/time value. %s",
              value.toString(),
              ex.getMessage()),
          ex);
    }
  }

  @SuppressWarnings("null")
  @Override
  public ZonedDateTime copy(Object obj) {
    return ZonedDateTime.from((ZonedDateTime) obj);
  }

  @Override
  public IDateTimeWithTimeZoneItem newItem(Object value) {
    ZonedDateTime item = toValue(value);
    return IDateTimeWithTimeZoneItem.valueOf(item);
  }
}
