/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import static dev.metaschema.core.metapath.TestUtils.sequence;
import static dev.metaschema.core.metapath.TestUtils.string;
import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.metaschema.core.metapath.ExpressionTestBase;
import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.item.ISequence;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class FnFunctionLookupTest
    extends ExpressionTestBase {

  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    // TODO: Review metaschema-framework/metaschema-java#396 and change accordingly.
    return Stream.of(
        Arguments.of(
            sequence(string("bcd")),
            "fn:function-lookup(meta:qname('Q{http://csrc.nist.gov/ns/metaschema/metapath-functions}substring'), 2)('abcd', 2)"),
        Arguments.of(
            sequence(),
            "let $f := fn:function-lookup(meta:qname('Q{http://expath.org/ns/zip}binary-entry'), 2) return if (exists($f)) then $f($href, $entry) else ()"));

  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void test(@NonNull ISequence<?> expected, @NonNull String metapath) {
    assertEquals(expected,
        IMetapathExpression.compile(metapath).evaluate(null, newDynamicContext()));
  }
}
