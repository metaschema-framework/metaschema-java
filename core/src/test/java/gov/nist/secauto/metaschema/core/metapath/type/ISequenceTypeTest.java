/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.type;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.integer;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.string;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.MetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.item.function.IMapItem;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class ISequenceTypeTest
    extends ExpressionTestBase {

  // FIXME: Use test vectors from
  // https://www.w3.org/TR/xpath-31/#id-sequencetype-syntax
  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        // data type tests
        Arguments.of("'a' instance of string", true),
        Arguments.of("'a' instance of meta:string", true),
        Arguments.of("'a' instance of meta:uuid", false),
        Arguments.of("'a' instance of node()", false),
        Arguments.of("'a' instance of item()", true),
        Arguments.of("'a' instance of meta:string?", true),
        // sequence tests
        Arguments.of("() instance of string", false),
        Arguments.of("('a', 1) instance of meta:string+", false),
        Arguments.of("('a', 1) instance of item()+", true),
        Arguments.of("() instance of item()+", false),
        // array tests
        Arguments.of("[ 1, 2 ] instance of array(integer+)", true),
        Arguments.of("[ 1, 'not an integer' ] instance of array(integer+)", false),
        // map tests
        Arguments.of("$M instance of map(*)", true),
        Arguments.of("$M instance of map(meta:integer, meta:string)", true),
        Arguments.of("$M instance of map(meta:decimal, meta:any-atomic-type)", true),
        Arguments.of("not($M instance of map(meta:uuid, meta:string))", true),
        Arguments.of("not($M instance of map(meta:integer, meta:token))", true));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void testMatch(@NonNull String test, boolean expected) {
    DynamicContext dynamicContext = new DynamicContext();
    dynamicContext.bindVariableValue(
        IEnhancedQName.of("M"),
        IMapItem.of(integer(0), string("no"), integer(1), string("yes")).toSequence());

    MetapathExpression metapath = MetapathExpression.compile(test);
    Boolean result = metapath.evaluateAs(null, MetapathExpression.ResultType.BOOLEAN, dynamicContext);

    assertEquals(
        expected,
        result,
        String.format("Expected `%s` to evaluate to '%s'",
            test,
            expected));
  }
}
