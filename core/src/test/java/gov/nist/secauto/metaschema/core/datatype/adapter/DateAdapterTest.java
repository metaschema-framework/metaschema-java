/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import edu.umd.cs.findbugs.annotations.NonNull;

class DateAdapterTest {
  private static final DateAdapter ADAPTER = new DateAdapter();

  @ParameterizedTest
  @ValueSource(strings = { "2018-01-01", "2020-06-23Z", "2020-06-23-04:00", "2020-06-23", "2020-01-01" })
  void testSimpleDate(@NonNull String date) {
    ADAPTER.parse(date);
  }
}
