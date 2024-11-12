/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;

import gov.nist.secauto.metaschema.core.datatype.AbstractDataTypeAdapter;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IYearMonthDurationItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Support for the Metaschema <a href=
 * "https://pages.nist.gov/metaschema/specification/datatypes/#year-month-duration">year-month-duration</a>
 * data type.
 */
public class YearMonthAdapter
    extends AbstractDataTypeAdapter<Period, IYearMonthDurationItem> {
  @NonNull
  private static final List<QName> NAMES = ObjectUtils.notNull(
      List.of(new QName(MetapathConstants.NS_METAPATH.toASCIIString(), "year-month-duration")));

  YearMonthAdapter() {
    super(Period.class);
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
  public Period copy(Object obj) {
    // value in immutable
    return (Period) obj;
  }

  @SuppressWarnings("null")
  @Override
  public Period parse(String value) {
    try {
      return Period.parse(value);
    } catch (DateTimeParseException ex) {
      throw new IllegalArgumentException(ex.getLocalizedMessage(), ex);
    }
  }

  @Override
  public Class<IYearMonthDurationItem> getItemClass() {
    return IYearMonthDurationItem.class;
  }

  @Override
  public IYearMonthDurationItem newItem(Object value) {
    Period item = toValue(value);
    return IYearMonthDurationItem.valueOf(item);
  }

}
