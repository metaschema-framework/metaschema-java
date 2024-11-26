/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;

import gov.nist.secauto.metaschema.core.datatype.AbstractCustomJavaDataTypeAdapter;
import gov.nist.secauto.metaschema.core.datatype.object.AmbiguousDateTime;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDateTimeItem;
import gov.nist.secauto.metaschema.core.qname.EQNameFactory;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Support for the Metaschema <a href=
 * "https://pages.nist.gov/metaschema/specification/datatypes/#date-time">date-time</a>
 * data type.
 */
public class DateTimeAdapter
    extends AbstractCustomJavaDataTypeAdapter<AmbiguousDateTime, IDateTimeItem> {
  @NonNull
  private static final List<IEnhancedQName> NAMES = ObjectUtils.notNull(
      List.of(
          EQNameFactory.instance().newQName(MetapathConstants.NS_METAPATH, "date-time"),
          // for backwards compatibility with original type name
          EQNameFactory.instance().newQName(MetapathConstants.NS_METAPATH, "dateTime")));

  DateTimeAdapter() {
    super(AmbiguousDateTime.class, IDateTimeItem.class, IDateTimeItem::cast);
  }

  @Override
  public List<IEnhancedQName> getNames() {
    return NAMES;
  }

  @Override
  public JsonFormatTypes getJsonRawType() {
    return JsonFormatTypes.STRING;
  }

  @Override
  public AmbiguousDateTime parse(String value) {
    AmbiguousDateTime retval;
    try {
      retval = parseWithTimeZone(value);
    } catch (DateTimeParseException ex) {
      try {
        retval = parseWithoutTimeZone(value);
      } catch (DateTimeParseException ex2) {
        IllegalArgumentException newEx = new IllegalArgumentException(ex2.getLocalizedMessage(), ex2);
        newEx.addSuppressed(ex);
        throw newEx;
      }
    }
    return retval;
  }

  @NonNull
  private static AmbiguousDateTime parseWithTimeZone(@NonNull String value) {
    return new AmbiguousDateTime(
        ObjectUtils.notNull(ZonedDateTime.from(DateFormats.DATE_TIME_WITH_TZ.parse(value))),
        true);
  }

  @NonNull
  private static AmbiguousDateTime parseWithoutTimeZone(@NonNull String value) {
    LocalDateTime dateTime = LocalDateTime.from(DateFormats.DATE_TIME_WITH_OPTIONAL_TZ.parse(value));
    return new AmbiguousDateTime(
        ObjectUtils.notNull(ZonedDateTime.of(dateTime, ZoneOffset.UTC)),
        false);
  }

  @Override
  public String asString(Object obj) {
    AmbiguousDateTime value = toValue(obj);
    return ObjectUtils.notNull(value.hasTimeZone()
        ? DateFormats.DATE_TIME_WITH_TZ.format(value.getValue())
        : DateFormats.DATE_TIME_WITH_OPTIONAL_TZ.format(value.getValue()));
  }

  @Override
  public IDateTimeItem newItem(Object value) {
    AmbiguousDateTime item = toValue(value);
    return IDateTimeItem.valueOf(item);
  }
}
