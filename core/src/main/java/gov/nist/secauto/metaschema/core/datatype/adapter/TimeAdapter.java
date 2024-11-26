/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;

import gov.nist.secauto.metaschema.core.datatype.AbstractCustomJavaDataTypeAdapter;
import gov.nist.secauto.metaschema.core.datatype.object.AmbiguousTime;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.ITimeItem;
import gov.nist.secauto.metaschema.core.qname.EQNameFactory;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Support for the Metaschema <a href=
 * "https://pages.nist.gov/metaschema/specification/datatypes/#date">date</a>
 * data type.
 */
public class TimeAdapter
    extends AbstractCustomJavaDataTypeAdapter<AmbiguousTime, ITimeItem> {
  @NonNull
  private static final List<IEnhancedQName> NAMES = ObjectUtils.notNull(
      List.of(
          EQNameFactory.instance().newQName(MetapathConstants.NS_METAPATH, "time")));

  TimeAdapter() {
    super(AmbiguousTime.class, ITimeItem.class, ITimeItem::cast);
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
  public AmbiguousTime parse(String value) {
    AmbiguousTime retval;
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
  private static AmbiguousTime parseWithTimeZone(@NonNull String value) {
    return new AmbiguousTime(
        ObjectUtils.notNull(OffsetTime.from(DateFormats.TIME_WITH_TZ.parse(value))),
        true);
  }

  @NonNull
  private static AmbiguousTime parseWithoutTimeZone(@NonNull String value) {
    LocalTime time = LocalTime.from(DateFormats.TIME_WITH_OPTIONAL_TZ.parse(value));
    return new AmbiguousTime(
        ObjectUtils.notNull(OffsetTime.of(time, ZoneOffset.UTC)),
        false);
  }

  @Override
  public String asString(Object obj) {
    AmbiguousTime value = toValue(obj);
    return ObjectUtils.notNull(value.hasTimeZone()
        ? DateFormats.TIME_WITH_TZ.format(value.getValue())
        : DateFormats.TIME_WITH_OPTIONAL_TZ.format(value.getValue()));
  }

  @Override
  public ITimeItem newItem(Object value) {
    AmbiguousTime item = toValue(value);
    return ITimeItem.valueOf(item);
  }
}
