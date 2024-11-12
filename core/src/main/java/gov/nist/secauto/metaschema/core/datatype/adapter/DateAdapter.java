/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;

import gov.nist.secauto.metaschema.core.datatype.AbstractCustomJavaDataTypeAdapter;
import gov.nist.secauto.metaschema.core.datatype.object.Date;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDateItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Support for the Metaschema <a href=
 * "https://pages.nist.gov/metaschema/specification/datatypes/#date">date</a>
 * data type.
 */
public class DateAdapter
    extends AbstractCustomJavaDataTypeAdapter<Date, IDateItem> {
  @NonNull
  private static final List<QName> NAMES = ObjectUtils.notNull(
      List.of(new QName(MetapathConstants.NS_METAPATH.toASCIIString(), "date")));
  @NonNull
  private static final Pattern DATE_TIMEZONE = ObjectUtils.notNull(
      Pattern.compile("^("
          + "^(?:(?:2000|2400|2800|(?:19|2[0-9](?:0[48]|[2468][048]|[13579][26])))-02-29)"
          + "|(?:(?:(?:19|2[0-9])[0-9]{2})-02-(?:0[1-9]|1[0-9]|2[0-8]))"
          + "|(?:(?:(?:19|2[0-9])[0-9]{2})-(?:0[13578]|10|12)-(?:0[1-9]|[12][0-9]|3[01]))"
          + "|(?:(?:(?:19|2[0-9])[0-9]{2})-(?:0[469]|11)-(?:0[1-9]|[12][0-9]|30))"
          + ")"
          + "(Z|[+-][0-9]{2}:[0-9]{2})?$"));

  DateAdapter() {
    super(Date.class);
  }

  @Override
  public List<QName> getNames() {
    return NAMES;
  }

  @Override
  public JsonFormatTypes getJsonRawType() {
    return JsonFormatTypes.STRING;
  }

  @Override
  public Date parse(String value) {
    Matcher matcher = DATE_TIMEZONE.matcher(value);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid date: " + value);
    }

    String parseValue
        = String.format("%sT00:00:00%s", matcher.group(1), matcher.group(2) == null ? "" : matcher.group(2));
    try {
      TemporalAccessor accessor = DateFormats.DATE_TIME_WITH_TZ.parse(parseValue);
      return new Date(ObjectUtils.notNull(ZonedDateTime.from(accessor)), true); // NOPMD - readability
    } catch (DateTimeParseException ex) {
      try {
        TemporalAccessor accessor = DateFormats.DATE_TIME_WITHOUT_TZ.parse(parseValue);
        LocalDate date = LocalDate.from(accessor);
        return new Date(ObjectUtils.notNull(ZonedDateTime.of(date, LocalTime.MIN, ZoneOffset.UTC)), false);
      } catch (DateTimeParseException ex2) {
        IllegalArgumentException newEx = new IllegalArgumentException(ex2.getLocalizedMessage(), ex2);
        newEx.addSuppressed(ex);
        throw newEx; // NOPMD - false positive
      }
    }
  }

  @Override
  public String asString(Object obj) {
    Date value = (Date) obj;
    return ObjectUtils.notNull(value.hasTimeZone()
        ? DateFormats.DATE_WITH_TZ.format(value.getValue())
        : DateFormats.DATE_WITHOUT_TZ.format(value.getValue()));
  }

  @Override
  public Class<IDateItem> getItemClass() {
    return IDateItem.class;
  }

  @Override
  public IDateItem newItem(Object value) {
    Date item = toValue(value);
    return IDateItem.valueOf(item);
  }
}
