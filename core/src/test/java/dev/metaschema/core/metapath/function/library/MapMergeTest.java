/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import static dev.metaschema.core.metapath.TestUtils.entry;
import static dev.metaschema.core.metapath.TestUtils.integer;
import static dev.metaschema.core.metapath.TestUtils.map;
import static dev.metaschema.core.metapath.TestUtils.sequence;
import static dev.metaschema.core.metapath.TestUtils.string;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import dev.metaschema.core.metapath.ExpressionTestBase;
import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.item.IItem;
import dev.metaschema.core.metapath.item.function.LookupTest;
import edu.umd.cs.findbugs.annotations.NonNull;

class MapMergeTest
    extends ExpressionTestBase {
  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            map(),
            "map:merge(())"),
        Arguments.of(
            map(entry(integer(0), string("no")), entry(integer(1), string("yes"))),
            "map:merge((map:entry(0, \"no\"), map:entry(1, \"yes\")))"),
        Arguments.of(
            map(
                entry(integer(0), string("Sonntag")),
                entry(integer(1), string("Montag")),
                entry(integer(2), string("Dienstag")),
                entry(integer(3), string("Mittwoch")),
                entry(integer(4), string("Donnerstag")),
                entry(integer(5), string("Freitag")),
                entry(integer(6), string("Samstag")),
                entry(integer(7), string("Unbekannt"))),
            "let $week :=  " + LookupTest.WEEKDAYS_GERMAN + " return map:merge(($week, map{7:\"Unbekannt\"}))"),
        Arguments.of(
            map(
                entry(integer(0), string("Sonntag")),
                entry(integer(1), string("Montag")),
                entry(integer(2), string("Dienstag")),
                entry(integer(3), string("Mittwoch")),
                entry(integer(4), string("Donnerstag")),
                entry(integer(5), string("Freitag")),
                entry(integer(6), string("Sonnabend"))),
            "let $week :=  " + LookupTest.WEEKDAYS_GERMAN
                + " return map:merge(($week, map{6:\"Sonnabend\"}), map{\"duplicates\":\"use-last\"})"),
        Arguments.of(
            map(
                entry(integer(0), string("Sonntag")),
                entry(integer(1), string("Montag")),
                entry(integer(2), string("Dienstag")),
                entry(integer(3), string("Mittwoch")),
                entry(integer(4), string("Donnerstag")),
                entry(integer(5), string("Freitag")),
                entry(integer(6), string("Samstag"))),
            "let $week :=  " + LookupTest.WEEKDAYS_GERMAN
                + " return map:merge(($week, map{6:\"Sonnabend\"}), map{\"duplicates\":\"use-first\"})"),
        Arguments.of(
            map(
                entry(integer(0), string("Sonntag")),
                entry(integer(1), string("Montag")),
                entry(integer(2), string("Dienstag")),
                entry(integer(3), string("Mittwoch")),
                entry(integer(4), string("Donnerstag")),
                entry(integer(5), string("Freitag")),
                entry(integer(6), sequence(string("Samstag"), string("Sonnabend")))),
            "let $week :=  " + LookupTest.WEEKDAYS_GERMAN
                + " return map:merge(($week, map{6:\"Sonnabend\"}), map{\"duplicates\":\"combine\"})"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void testExpression(@NonNull IItem expected, @NonNull String metapath) {

    IItem result = IMetapathExpression.compile(metapath)
        .evaluateAs(null, IMetapathExpression.ResultType.ITEM, newDynamicContext());
    assertEquals(expected, result);
  }
}
