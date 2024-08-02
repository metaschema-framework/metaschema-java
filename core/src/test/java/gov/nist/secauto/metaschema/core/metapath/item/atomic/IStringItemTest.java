/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class IStringItemTest {
  private static Stream<Arguments> testCompare() { // NOPMD - false positive
    return Stream.of(
        // string
        Arguments.of(IStringItem.valueOf("A"), IStringItem.valueOf("B"), IIntegerItem.NEGATIVE_ONE));
  }

  @ParameterizedTest
  @MethodSource
  void testCompare(@NonNull IStringItem left, @NonNull IStringItem right, @NonNull IIntegerItem expectedResult) {
    IIntegerItem result = IIntegerItem.valueOf(left.compareTo(right));
    assertEquals(expectedResult, result);
  }

}
