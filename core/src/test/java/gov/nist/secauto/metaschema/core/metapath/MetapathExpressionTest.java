/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.adelean.inject.resources.junit.jupiter.GivenTextResource;
import com.adelean.inject.resources.junit.jupiter.TestWithResources;

import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBooleanItem;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@TestWithResources
class MetapathExpressionTest {

  @GivenTextResource(from = "/correct-examples.txt", charset = "UTF-8")
  String correctMetapathInstances;

  // @GivenTextResource(from = "/incorrect-examples.txt", charset = "UTF-8")
  // String incorrectMetapathInstances;

  @Test
  @Disabled
  void testCorrect() {
    for (String line : correctMetapathInstances.split("\\r?\\n")) {
      if (line.startsWith("# ")) {
        continue;
      }
      // System.out.println(line);
      MetapathExpression.compile(line);
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
      MetapathExpression.compile("**");
    });
  }

  @Test
  void test() {
    MetapathExpression path = MetapathExpression.compile("2 eq 1 + 1");
    ISequence<?> result = path.evaluate();
    assertNotNull(result, "null result");
    assertTrue(!result.isEmpty(), "result was empty");
    assertEquals(1, result.size(), "unexpected size");
    assertEquals(true, ((IBooleanItem) result.getValue().iterator().next()).toBoolean(), "unexpected result");
  }
}
