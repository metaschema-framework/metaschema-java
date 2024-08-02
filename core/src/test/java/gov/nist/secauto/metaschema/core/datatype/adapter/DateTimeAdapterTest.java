/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DateTimeAdapterTest {

  @ParameterizedTest
  @ValueSource(strings = {
      "2018-01-01T00:00:00",
      "2019-09-28T23:20:50.52Z",
      "2019-09-28T23:20:50.0Z",
      "2019-09-28T23:20:50.5200",
      "2019-12-02T16:39:57-08:00",
      "2019-12-02T16:39:57.100-08:00",
      "2019-12-02T16:39:57",
      "2019-12-31T23:59:59Z",
      "2019-12-31T23:59:59"
  })
  void testSimpleDate(String date) {
    new DateTimeAdapter().parse(ObjectUtils.requireNonNull(date));
  }

}
