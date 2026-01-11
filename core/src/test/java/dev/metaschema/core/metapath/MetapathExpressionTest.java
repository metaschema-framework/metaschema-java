/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.metapath.item.atomic.IBooleanItem;
import io.hosuaby.inject.resources.junit.jupiter.GivenTextResource;
import io.hosuaby.inject.resources.junit.jupiter.TestWithResources;

@TestWithResources
class MetapathExpressionTest {

  @GivenTextResource(from = "/correct-examples.txt", charset = "UTF-8")
  String correctMetapathInstances;

  // @GivenTextResource(from = "/incorrect-examples.txt", charset = "UTF-8")
  // String incorrectMetapathInstances;

  @ParameterizedTest
  @CsvFileSource(resources = "/correct-examples.txt", delimiter = ';')
  void testCorrect(String line) {
    if (!line.startsWith("# ")
        && !line.contains("text()")
        && !line.contains("number(")
        && !line.contains("current(")
        && !line.contains("last(")) {
      String expression = line.endsWith(";")
          ? line.substring(0, line.length() - 1)
          : line;
      // System.out.println(line);
      IMetapathExpression.compile(expression);
    }
  }
  //
  // @Test
  // @Disabled
  // void testIncorrect() {
  // for (String line : incorrectMetapathInstances.split("\\r?\\n")) {
  // if (line.startsWith("# ")) {
  // continue;
  // }
  // // System.out.println(line);
  // try {
  // MetapathExpression.compile(line);
  // } catch (ParseCancellationException ex) {
  // // ex.printStackTrace();
  // }
  // }
  // }

  @Test
  void testSyntaxError() {
    assertThrows(MetapathException.class, () -> {
      IMetapathExpression.compile("**");
    });
  }

  @Test
  void test() {
    IMetapathExpression path = IMetapathExpression.compile("2 eq 1 + 1");
    ISequence<?> result = path.evaluate();
    assertNotNull(result, "null result");
    assertTrue(!result.isEmpty(), "result was empty");
    assertEquals(1, result.size(), "unexpected size");
    assertEquals(true, ((IBooleanItem) result.iterator().next()).toBoolean(), "unexpected result");
  }

  @Test
  void testMalformedIf() {
    InvalidMetapathGrammarException thrown = assertThrows(InvalidMetapathGrammarException.class, () -> {
      IMetapathExpression.compile("if 'a' = '1.1.2' then true() else false()");
    });
    assertThat(thrown)
        .isExactlyInstanceOf(InvalidMetapathGrammarException.class)
        .cause()
        .isExactlyInstanceOf(ParseCancellationException.class);
  }
}
