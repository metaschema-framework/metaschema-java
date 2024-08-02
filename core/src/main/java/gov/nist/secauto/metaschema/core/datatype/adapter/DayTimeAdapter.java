/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;

import gov.nist.secauto.metaschema.core.datatype.AbstractDataTypeAdapter;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDayTimeDurationItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

public class DayTimeAdapter
    extends AbstractDataTypeAdapter<Duration, IDayTimeDurationItem> {
  @NonNull
  private static final List<QName> NAMES = ObjectUtils.notNull(
      List.of(new QName(MetapathConstants.NS_METAPATH.toASCIIString(), "day-time-duration")));

  DayTimeAdapter() {
    super(Duration.class);
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
  public Duration copy(Object obj) {
    // value in immutable
    return (Duration) obj;
  }

  @SuppressWarnings("null")
  @Override
  public Duration parse(String value) {
    try {
      return Duration.parse(value);
    } catch (DateTimeParseException ex) {
      throw new IllegalArgumentException(ex.getLocalizedMessage(), ex);
    }
  }

  @Override
  public Class<IDayTimeDurationItem> getItemClass() {
    return IDayTimeDurationItem.class;
  }

  @Override
  public IDayTimeDurationItem newItem(Object value) {
    Duration item = toValue(value);
    return IDayTimeDurationItem.valueOf(item);
  }

}
