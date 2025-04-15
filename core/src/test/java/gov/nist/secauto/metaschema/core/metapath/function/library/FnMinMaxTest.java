/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.decimal;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.integer;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.string;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.uri;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.nist.secauto.metaschema.core.metapath.function.InvalidArgumentFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

class FnMinMaxTest
    extends FunctionTestBase {

  private static Stream<Arguments> provideValuesMin() {
    return Stream.of(
        Arguments.of(null, new IAnyAtomicItem[] { integer(3), string("text") }),
        Arguments.of(string("same"), new IAnyAtomicItem[] { string("same"), uri("uri/") }),
        Arguments.of(integer(3), new IAnyAtomicItem[] { integer(3) }),
        Arguments.of(decimal("5"), new IAnyAtomicItem[] { decimal("5") }),
        Arguments.of(string("same"), new IAnyAtomicItem[] { string("same") }),
        Arguments.of(integer(3), new IAnyAtomicItem[] { integer(3), integer(4), integer(5) }),
        Arguments.of(integer(5), new IAnyAtomicItem[] { integer(5), decimal("5"), decimal("10") }),
        Arguments.of(decimal("5"), new IAnyAtomicItem[] { decimal("5"), integer(5), decimal("10") }));
  }

  @ParameterizedTest
  @MethodSource("provideValuesMin")
  void testMin(@Nullable IAnyAtomicItem expected, @NonNull IAnyAtomicItem... values) {
    List<ISequence<?>> arguments = CollectionUtil.singletonList(ISequence.of(values));
    if (expected == null) {
      assertThrows(InvalidArgumentFunctionException.class, () -> {
        FunctionTestBase.executeFunction(
            FnMinMax.SIGNATURE_MIN,
            newDynamicContext(),
            null,
            arguments);
      });
    } else {
      assertFunctionResult(
          FnMinMax.SIGNATURE_MIN,
          ISequence.of(expected),
          arguments);
    }
  }

  @Test
  void testMinNoOp() {
    assertFunctionResult(
        FnMinMax.SIGNATURE_MIN,
        ISequence.empty(),
        CollectionUtil.singletonList(ISequence.empty()));
  }

  private static Stream<Arguments> provideValuesMax() {
    return Stream.of(
        Arguments.of(null, new IAnyAtomicItem[] { integer(3), string("text") }),
        Arguments.of(string("uri/"), new IAnyAtomicItem[] { string("same"), uri("uri/") }),
        Arguments.of(integer(3), new IAnyAtomicItem[] { integer(3) }),
        Arguments.of(decimal("5"), new IAnyAtomicItem[] { decimal("5") }),
        Arguments.of(string("same"), new IAnyAtomicItem[] { string("same") }),
        Arguments.of(integer(5), new IAnyAtomicItem[] { integer(3), integer(4), integer(5) }),
        Arguments.of(integer(10), new IAnyAtomicItem[] { integer(5), decimal("5"), integer(10), decimal("10") }),
        Arguments.of(decimal("10"), new IAnyAtomicItem[] { decimal("5"), integer(5), decimal("10"), integer(10) }));
  }

  @ParameterizedTest
  @MethodSource("provideValuesMax")
  void testMax(@Nullable IAnyAtomicItem expected, @NonNull IAnyAtomicItem... values) {
    List<ISequence<?>> arguments = CollectionUtil.singletonList(ISequence.of(values));
    if (expected == null) {
      assertThrows(InvalidArgumentFunctionException.class, () -> {
        FunctionTestBase.executeFunction(
            FnMinMax.SIGNATURE_MAX,
            newDynamicContext(),
            null,
            arguments);
      });
    } else {
      assertFunctionResult(
          FnMinMax.SIGNATURE_MAX,
          ISequence.of(expected),
          CollectionUtil.singletonList(ISequence.of(values)));
    }
  }

  @Test
  void testMaxNoOp() {
    assertFunctionResult(
        FnMinMax.SIGNATURE_MAX,
        ISequence.empty(),
        CollectionUtil.singletonList(ISequence.empty()));
  }
}
