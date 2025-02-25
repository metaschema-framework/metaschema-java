/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDayTimeDurationItem;
import gov.nist.secauto.metaschema.core.qname.EQNameFactory;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Support for the Metaschema <a href=
 * "https://pages.nist.gov/metaschema/specification/datatypes/#day-time-duration">day-time-duration</a>
 * data type.
 */
public class DayTimeAdapter
    extends AbstractDurationAdapter<Duration, IDayTimeDurationItem> {
  @NonNull
  private static final List<IEnhancedQName> NAMES = ObjectUtils.notNull(
      List.of(
          EQNameFactory.instance().newQName(MetapathConstants.NS_METAPATH, "day-time-duration")));

  private static final Pattern DAY_TIME_DURATION_PATTERN = Pattern.compile(
      "^(?<sign>-)?"
          + "P"
          + "(?:(?<day>[0-9]+)D)?"
          + "(?:T"
          + "(?:(?<hour>[0-9]+)H)?"
          + "(?:(?<minute>[0-9]+)M)?"
          + "(?:(?<second2>[0-9]+(?:\\.[0-9]+)?)S)?"
          + ")?$");

  DayTimeAdapter() {
    super(Duration.class, IDayTimeDurationItem.class, IDayTimeDurationItem::cast);
  }

  @Override
  public List<IEnhancedQName> getNames() {
    return NAMES;
  }

  @Override
  public Duration copy(Object obj) {
    // value in immutable
    return (Duration) obj;
  }

  @Override
  public Duration parse(String value) {
    Matcher matcher = DAY_TIME_DURATION_PATTERN.matcher(value);

    if (!matcher.matches()) {
      throw new IllegalArgumentException(
          String.format("String duration '%s' is not a day/time duration.", value));
    }

    try {
      return parseDuration(
          matcher.group(1) != null,
          matcher.group(2),
          matcher.group(3),
          matcher.group(4),
          matcher.group(5));
    } catch (ArithmeticException ex) {
      throw new IllegalArgumentException(
          String.format("Invalid duration value '%s'.", value),
          ex);
    }
  }

  @Override
  public IDayTimeDurationItem newItem(Object value) {
    Duration item = toValue(value);
    return IDayTimeDurationItem.valueOf(item);
  }
}
