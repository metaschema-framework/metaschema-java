/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.array;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.entry;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.map;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.sequence;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.string;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class MapFindTest
    extends ExpressionTestBase {
  private static final String RESPONSES = "[map{0:'no', 1:'yes'}, map{0:'non', 1:'oui'},"
      + " map{0:'nein', 1:('ja', 'doch')}]";
  private static final String INVENTORY = "map{\"name\":\"car\", \"id\":\"QZ123\","
      + " \"parts\": [map{\"name\":\"engine\", \"id\":\"YW678\", \"parts\":[]}]}";

  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            array(string("no"), string("non"), string("nein")),
            "let $responses :=  " + RESPONSES + " return map:find($responses, 0)"),
        Arguments.of(
            array(string("yes"), string("oui"), sequence(string("ja"), string("doch"))),
            "let $responses :=  " + RESPONSES + " return map:find($responses, 1)"),
        Arguments.of(
            array(),
            "let $responses :=  " + RESPONSES + " return map:find($responses, 2)"),
        Arguments.of(
            array(array(map(entry(string("name"), string("engine")), entry(string("id"), string("YW678")),
                entry(string("parts"), array()))), array()),
            "let $inventory :=  " + INVENTORY + " return map:find($inventory, \"parts\")"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void testExpression(@NonNull IItem expected, @NonNull String metapath) {

    IItem result = IMetapathExpression.compile(metapath)
        .evaluateAs(null, IMetapathExpression.ResultType.ITEM, newDynamicContext());
    assertEquals(expected, result);
  }
}
