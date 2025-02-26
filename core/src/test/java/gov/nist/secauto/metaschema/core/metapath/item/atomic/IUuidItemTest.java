/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.UUID;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class IUuidItemTest {
  private static Stream<Arguments> provideDeepEqualValues() { // NOPMD - false positive
    UUID uuidRandom = ObjectUtils.notNull(UUID.randomUUID());
    UUID uuid1 = ObjectUtils.notNull(UUID.fromString("4cfa2c52-9345-4012-8055-0bc9ac9b03fa"));
    UUID uuid2 = ObjectUtils.notNull(UUID.fromString("25a6d916-f179-4550-ad2b-7e7cd9df35d2"));
    String uuid2String = ObjectUtils.notNull(uuid2.toString());

    return Stream.of(
        Arguments.of(IUuidItem.valueOf(uuidRandom), IUuidItem.valueOf(uuidRandom), true),
        Arguments.of(IUuidItem.valueOf(uuid1), IUuidItem.valueOf(uuid2), false),
        Arguments.of(IUuidItem.valueOf(uuid2), IStringItem.valueOf(uuid2String), true),
        Arguments.of(IStringItem.valueOf(uuid2String), IUuidItem.valueOf(uuid2), true));
  }

  @ParameterizedTest
  @MethodSource("provideDeepEqualValues")
  void testDeepEqual(@NonNull IAnyAtomicItem left, @NonNull IAnyAtomicItem right, boolean expected) {
    DynamicContext dynamicContext = new DynamicContext();
    boolean actual = left.deepEquals(right, dynamicContext);
    assertEquals(expected, actual);
  }
}
