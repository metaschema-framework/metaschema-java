/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;

import gov.nist.secauto.metaschema.core.datatype.AbstractDataTypeAdapter;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDateWithTimeZoneItem;
import gov.nist.secauto.metaschema.core.qname.EQNameFactory;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.time.DateTimeException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Support for the Metaschema <a href=
 * "https://pages.nist.gov/metaschema/specification/datatypes/#date-with-timezone">date-with-timezone</a>
 * data type.
 */
public class DateWithTZAdapter
    extends AbstractDataTypeAdapter<ZonedDateTime, IDateWithTimeZoneItem> {
  @NonNull
  private static final List<IEnhancedQName> NAMES = ObjectUtils.notNull(
      List.of(
          EQNameFactory.instance().newQName(MetapathConstants.NS_METAPATH, "date-with-timezone")));
  private static final Pattern DATE_TIMEZONE = Pattern.compile("^("
      + "^(?:(?:2000|2400|2800|(?:19|2[0-9](?:0[48]|[2468][048]|[13579][26])))-02-29)"
      + "|(?:(?:(?:19|2[0-9])[0-9]{2})-02-(?:0[1-9]|1[0-9]|2[0-8]))"
      + "|(?:(?:(?:19|2[0-9])[0-9]{2})-(?:0[13578]|10|12)-(?:0[1-9]|[12][0-9]|3[01]))"
      + "|(?:(?:(?:19|2[0-9])[0-9]{2})-(?:0[469]|11)-(?:0[1-9]|[12][0-9]|30))"
      + ")"
      + "(Z|[+-][0-9]{2}:[0-9]{2})$");

  DateWithTZAdapter() {
    super(ZonedDateTime.class, IDateWithTimeZoneItem.class, IDateWithTimeZoneItem::cast);
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
    Matcher matcher = DATE_TIMEZONE.matcher(value);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid date: " + value);
    }
    String parseValue = String.format("%sT00:00:00%s", matcher.group(1), matcher.group(2));
    try {
      return ZonedDateTime.from(DateFormats.DATE_TIME_WITH_TZ.parse(parseValue));
    } catch (DateTimeParseException ex) {
      throw new IllegalArgumentException(ex.getLocalizedMessage(), ex);
    }
  }

  @SuppressWarnings("null")
  @Override
  public String asString(Object value) {
    try {
      return DateFormats.DATE_WITH_TZ.format((ZonedDateTime) value);
    } catch (DateTimeException ex) {
      throw new IllegalArgumentException(
          String.format("The provided value '%s' cannot be formatted as a date value. %s",
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
  public IDateWithTimeZoneItem newItem(Object value) {
    ZonedDateTime item = toValue(value);
    return IDateWithTimeZoneItem.valueOf(item);
  }
}
