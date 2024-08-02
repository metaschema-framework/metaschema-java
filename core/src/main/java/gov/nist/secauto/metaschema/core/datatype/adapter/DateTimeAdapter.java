/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;

import gov.nist.secauto.metaschema.core.datatype.AbstractCustomJavaDataTypeAdapter;
import gov.nist.secauto.metaschema.core.datatype.object.DateTime;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDateItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDateTimeItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IUntypedAtomicItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

public class DateTimeAdapter
    extends AbstractCustomJavaDataTypeAdapter<DateTime, IDateTimeItem> {
  @NonNull
  private static final List<QName> NAMES = ObjectUtils.notNull(
      List.of(
          new QName(MetapathConstants.NS_METAPATH.toASCIIString(), "date-time"),
          // for backwards compatibility with original type name
          new QName(MetapathConstants.NS_METAPATH.toASCIIString(), "dateTime")));

  DateTimeAdapter() {
    super(DateTime.class);
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
  public DateTime parse(String value) {
    try {
      return new DateTime(ZonedDateTime.from(DateFormats.DATE_TIME_WITH_TZ.parse(value)), true); // NOPMD - readability
    } catch (DateTimeParseException ex) {
      try {
        LocalDateTime dateTime = LocalDateTime.from(DateFormats.DATE_TIME_WITHOUT_TZ.parse(value));
        return new DateTime(ZonedDateTime.of(dateTime, ZoneOffset.UTC), false);
      } catch (DateTimeParseException ex2) {
        IllegalArgumentException newEx = new IllegalArgumentException(ex2.getLocalizedMessage(), ex2);
        newEx.addSuppressed(ex);
        throw newEx; // NOPMD - it's ok
      }
    }
  }

  @Override
  public String asString(Object obj) {
    DateTime value = (DateTime) obj;
    String retval;
    if (value.hasTimeZone()) {
      @SuppressWarnings("null")
      @NonNull String formatted = DateFormats.DATE_TIME_WITH_TZ.format(value.getValue());
      retval = formatted;
    } else {
      @SuppressWarnings("null")
      @NonNull String formatted = DateFormats.DATE_TIME_WITHOUT_TZ.format(value.getValue());
      retval = formatted;
    }
    return retval;
  }

  @Override
  public Class<IDateTimeItem> getItemClass() {
    return IDateTimeItem.class;
  }

  @Override
  public IDateTimeItem newItem(Object value) {
    DateTime item = toValue(value);
    return IDateTimeItem.valueOf(item);
  }

  @Override
  protected IDateTimeItem castInternal(@NonNull IAnyAtomicItem item) {
    // TODO: bring up to spec
    IDateTimeItem retval;
    if (item instanceof IDateItem) {
      retval = IDateTimeItem.valueOf(((IDateItem) item).asZonedDateTime());
    } else if (item instanceof IStringItem || item instanceof IUntypedAtomicItem) {
      retval = super.castInternal(item);
    } else {
      throw new InvalidValueForCastFunctionException(
          String.format("unsupported item type '%s'", item.getClass().getName()));
    }
    return retval;
  }

}
