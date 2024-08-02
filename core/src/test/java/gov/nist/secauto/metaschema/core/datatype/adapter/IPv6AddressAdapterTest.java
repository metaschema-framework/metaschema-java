/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import edu.umd.cs.findbugs.annotations.NonNull;

@SuppressWarnings("PMD.AvoidUsingHardCodedIP")
class IPv6AddressAdapterTest {
  @ParameterizedTest
  @ValueSource(strings = {
      // Disallow empty
      "",
      // Disallow IPv4
      "127.0.0.1",
      // Disallow binary notation
      "10000000000001:110110111000:1000010110100011:0:0:1000101000101110:1101110000:1110",
      // Disallow wildcard separator
      "2001:0000:4136:\\*:\\*:\\*:\\*:\\*",
      // Disallow prefixes beyond address size
      "baba:baba:baba:baba:baba:baba:baba:/64"
  })
  void testIPv6AddressThrowsWithInvalid(@NonNull String addr) {
    assertThrows(IllegalArgumentException.class, () -> {
      new IPv6AddressAdapter().parse(addr);
    });
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "::",
      "::1",
      "::/128",
      "::1/128",
      "fe80::/64",
      "::ffff:192.0.2.47",
      "fdf8:f53b:82e4::53",
      "fe80::200:5aee:feaa:20a2",
      "2001:10:240:ab::a",
      "2001:0000:4136:e378:8000:63bf:3fff:fdd2",
      // Regression test for usnistgov/metaschema-java#156
      "2001:0000:0000:0000:0000:ffff:0a02:0202",
  })
  void testIPv6AddressAllowsCommonIPv6Addresses(@NonNull String addr) {
    assertDoesNotThrow(() -> {
      new IPv6AddressAdapter().parse(addr);
    });
  }
}
