/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import static dev.metaschema.core.metapath.TestUtils.qname;
import static dev.metaschema.core.metapath.TestUtils.sequence;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import dev.metaschema.core.metapath.ExpressionTestBase;
import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.item.ISequence;
import edu.umd.cs.findbugs.annotations.NonNull;

class FnQNameTest
    extends ExpressionTestBase {

  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            sequence(qname("http://www.example.com/example", "person")),
            "fn:QName('http://www.example.com/example', 'person')"),
        Arguments.of(
            sequence(qname("http://www.example.com/example", "ht:person")),
            "fn:QName('http://www.example.com/example', 'ht:person')"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void test(@NonNull ISequence<?> expected, @NonNull String metapath) {
    assertEquals(expected,
        IMetapathExpression.compile(metapath).evaluate(null, newDynamicContext()));
  }
}
