/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.decimal;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.integer;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.string;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class IBooleanItemTest {
  @Test
  void testValueOf() {
    Assertions.assertAll(
        () -> assertEquals(IBooleanItem.valueOf(true), IBooleanItem.TRUE),
        () -> assertEquals(IBooleanItem.valueOf(false), IBooleanItem.FALSE),
        () -> assertEquals(IBooleanItem.valueOf(ObjectUtils.notNull(Boolean.TRUE)), IBooleanItem.TRUE),
        () -> assertEquals(IBooleanItem.valueOf(ObjectUtils.notNull(Boolean.FALSE)), IBooleanItem.FALSE),
        () -> assertEquals(IBooleanItem.valueOf("1"), IBooleanItem.TRUE, "1"),
        () -> assertEquals(IBooleanItem.valueOf("0"), IBooleanItem.FALSE, "0"),
        () -> assertEquals(IBooleanItem.valueOf(""), IBooleanItem.FALSE, ""),
        () -> assertEquals(IBooleanItem.valueOf("true"), IBooleanItem.TRUE),
        () -> assertEquals(IBooleanItem.valueOf("false"), IBooleanItem.FALSE));
  }

  private static Stream<Arguments> provideValuesForCast() {
    return Stream.of(
        Arguments.of(IBooleanItem.TRUE, IBooleanItem.TRUE),
        Arguments.of(IBooleanItem.FALSE, IBooleanItem.FALSE),
        Arguments.of(integer(1), IBooleanItem.TRUE),
        Arguments.of(integer(0), IBooleanItem.FALSE),
        Arguments.of(decimal("1"), IBooleanItem.TRUE),
        Arguments.of(decimal("0"), IBooleanItem.FALSE),
        Arguments.of(string("1"), IBooleanItem.TRUE),
        Arguments.of(string("654321"), IBooleanItem.TRUE),
        Arguments.of(string("0"), IBooleanItem.FALSE),
        Arguments.of(string(""), IBooleanItem.FALSE),
        Arguments.of(string("true"), IBooleanItem.TRUE),
        Arguments.of(string("false"), IBooleanItem.FALSE));
  }

  @ParameterizedTest
  @MethodSource("provideValuesForCast")
  void testCast(@NonNull IAnyAtomicItem item, @NonNull IBooleanItem expected) {
    IBooleanItem result = IBooleanItem.cast(item);
    assertEquals(expected, result);
  }

}
