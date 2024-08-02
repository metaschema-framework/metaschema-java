/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.ZonedDateTime;

import edu.umd.cs.findbugs.annotations.NonNull;

class DateTimeWithTZAdapterTest {
  private static final DateTimeWithTZAdapter ADAPTER = MetaschemaDataTypeProvider.DATE_TIME_WITH_TZ;

  @ParameterizedTest
  @ValueSource(strings = {
      "2020-12-20T14:47:48.623-05:00",
      "2019-09-28T23:20:50.52Z",
      "2019-09-28T23:20:50.0Z",
      "2019-12-02T16:39:57-08:00",
      "2019-12-02T16:39:57.100-08:00",
      "2019-12-31T23:59:59Z"
  })
  void testParse(@NonNull String value) {
    ZonedDateTime obj = ADAPTER.parse(value);
    assertNotNull(obj, "not null");
  }

}
