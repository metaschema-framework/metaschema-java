/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.string;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class FnStringJoinTest
    extends ExpressionTestBase {
  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            string("123456789"),
            "string-join(1 to 9)"),
        Arguments.of(
                string("123456789"),
                "string-join(1 to 9, '')"),
        Arguments.of(
                string("123456789"),
                "string-join(1 to 9, ())"),
        Arguments.of(
            string("Now is the time ..."),
            "string-join(('Now', 'is', 'the', 'time', '...'), ' ')"),
        Arguments.of(
            string(""),
            "string-join((), 'separator')"),
        Arguments.of(
            string("1, 2, 3, 4, 5"),
            "string-join(1 to 5, ', ')")
    // Arguments.of(
    // string("xml:id=\"xyz\""),
    // "let $doc := <doc><chap><section xml:id=\"xyz\"/></chap></doc>\n"
    // + "return $doc//@xml:id ! fn:string-join((node-name(), '=\"', ., '\"'))"
    // ),
    // Arguments.of(
    // string("doc/chap/section"),
    // "let $doc := <doc><chap><section xml:id=\"xyz\"/></chap></doc>\n"
    // + "return $doc//@xml:id ! fn:string-join((node-name(), '=\"', ., '\"'))"
    // )
    );
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void testExpression(@NonNull IStringItem expected, @NonNull String metapath) {
    assertEquals(
        expected,
        IMetapathExpression.compile(metapath).evaluateAs(null, IMetapathExpression.ResultType.ITEM,
            newDynamicContext()));
  }

}
