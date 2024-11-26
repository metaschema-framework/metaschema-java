/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;

import gov.nist.secauto.metaschema.core.datatype.AbstractDataTypeAdapter;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.ITimeWithTimeZoneItem;
import gov.nist.secauto.metaschema.core.qname.EQNameFactory;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.time.DateTimeException;
import java.time.OffsetTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Support for the Metaschema <a href=
 * "https://pages.nist.gov/metaschema/specification/datatypes/#date-time-with-timezone">date-time-with-timezone</a>
 * data type.
 */
public class TimeWithTZAdapter
    extends AbstractDataTypeAdapter<OffsetTime, ITimeWithTimeZoneItem> {
  @NonNull
  private static final List<IEnhancedQName> NAMES = ObjectUtils.notNull(
      List.of(
          EQNameFactory.instance().newQName(MetapathConstants.NS_METAPATH, "time-with-timezone")));

  TimeWithTZAdapter() {
    super(OffsetTime.class, ITimeWithTimeZoneItem.class, ITimeWithTimeZoneItem::cast);
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
  public OffsetTime parse(String value) {
    try {
      return OffsetTime.from(DateFormats.TIME_WITH_TZ.parse(value));
    } catch (DateTimeParseException ex) {
      throw new IllegalArgumentException(ex.getLocalizedMessage(), ex);
    }
  }

  @SuppressWarnings("null")
  @Override
  public String asString(Object value) {
    try {
      return DateFormats.TIME_WITH_TZ.format(toValue(value));
    } catch (DateTimeException ex) {
      throw new IllegalArgumentException(
          String.format("The provided value '%s' cannot be formatted as a time value. %s",
              value.toString(),
              ex.getMessage()),
          ex);
    }
  }

  @SuppressWarnings("null")
  @Override
  public OffsetTime copy(Object obj) {
    return OffsetTime.from((OffsetTime) obj);
  }

  @Override
  public ITimeWithTimeZoneItem newItem(Object value) {
    OffsetTime item = toValue(value);
    return ITimeWithTimeZoneItem.valueOf(item);
  }
}
