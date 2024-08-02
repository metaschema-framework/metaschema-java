/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/**
 * Provides common date formats used by the data type implementations.
 */
@SuppressWarnings("PMD.DataClass")
final class DateFormats {

  // public static final DateTimeFormatter dateWithOptionalTZ;
  public static final DateTimeFormatter DATE_WITH_TZ;
  public static final DateTimeFormatter DATE_WITHOUT_TZ;
  // public static final DateTimeFormatter dateTimeWithOptionalTZ;
  public static final DateTimeFormatter DATE_TIME_WITH_TZ;
  public static final DateTimeFormatter DATE_TIME_WITHOUT_TZ;

  static {
    DATE_WITH_TZ = new DateTimeFormatterBuilder()
        .appendPattern("yyyy-MM-dd")
        .appendPattern("XXX")
        .toFormatter();
    DATE_WITHOUT_TZ = new DateTimeFormatterBuilder()
        .appendPattern("yyyy-MM-dd")
        .toFormatter();
    DATE_TIME_WITH_TZ = new DateTimeFormatterBuilder()
        .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
        .optionalStart()
        .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
        .optionalEnd()
        .appendPattern("XXX")
        .toFormatter();
    DATE_TIME_WITHOUT_TZ = new DateTimeFormatterBuilder()
        .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
        .optionalStart()
        .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
        .optionalEnd()
        .toFormatter();
  }

  private DateFormats() {
    // disable construction
  }
}
