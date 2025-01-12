/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IYearMonthDurationItem;
import gov.nist.secauto.metaschema.core.qname.EQNameFactory;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.time.Period;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Support for the Metaschema <a href=
 * "https://pages.nist.gov/metaschema/specification/datatypes/#year-month-duration">year-month-duration</a>
 * data type.
 */
public class YearMonthAdapter
    extends AbstractDurationAdapter<Period, IYearMonthDurationItem> {
  @NonNull
  private static final List<IEnhancedQName> NAMES = ObjectUtils.notNull(
      List.of(
          EQNameFactory.instance().newQName(MetapathConstants.NS_METAPATH, "year-month-duration")));

  private static final Pattern YEAR_MONTH_DURATION_PATTERN = Pattern.compile(
      "^(?<sign>-)?"
          + "P"
          + "(?:(?<year>[0-9]+)Y)?"
          + "(?:(?<month>[0-9]+)M)?"
          + "$");

  YearMonthAdapter() {
    super(Period.class, IYearMonthDurationItem.class, IYearMonthDurationItem::cast);
  }

  @Override
  public List<IEnhancedQName> getNames() {
    return NAMES;
  }

  @Override
  public Period copy(Object obj) {
    // value in immutable
    return (Period) obj;
  }

  @Override
  public Period parse(String value) {
    Matcher matcher = YEAR_MONTH_DURATION_PATTERN.matcher(value);

    if (!matcher.matches()) {
      throw new IllegalArgumentException(
          String.format("String duration '%s' is not a year/month duration.", value));
    }

    String year = matcher.group(2);
    String month = matcher.group(3);
    return parsePeriod(matcher.group(1) != null, year, month);
  }

  @Override
  public IYearMonthDurationItem newItem(Object value) {
    Period item = toValue(value);
    return IYearMonthDurationItem.valueOf(item);
  }
}
