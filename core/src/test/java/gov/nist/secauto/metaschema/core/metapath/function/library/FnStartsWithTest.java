/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.bool;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.string;

import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBooleanItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.Nullable;

class FnStartsWithTest
    extends FunctionTestBase {
  static Stream<Arguments> provideValues() {
    return Stream.of(
        Arguments.of(bool(true), null, null),
        Arguments.of(bool(true), string(""), null),
        Arguments.of(bool(true), null, string("")),
        Arguments.of(bool(true), string("non-empty"), null),
        Arguments.of(bool(false), null, string("non-empty")),
        Arguments.of(bool(true), string(""), string("")),
        Arguments.of(bool(true), string("non-empty"), string("")),
        Arguments.of(bool(false), string(""), string("non-empty")),
        Arguments.of(bool(true), string("abcdefg"), string("abc")),
        Arguments.of(bool(false), string("abcdefg"), string("bc")));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void test(@Nullable IBooleanItem expected, @Nullable IStringItem text, @Nullable IStringItem pattern) {
    assertFunctionResult(
        FnStartsWith.SIGNATURE,
        ISequence.of(expected),
        ObjectUtils.notNull(List.of(ISequence.of(text), ISequence.of(pattern))));
  }
}
