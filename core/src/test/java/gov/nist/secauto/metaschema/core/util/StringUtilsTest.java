/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

class StringUtilsTest {

  @Test
  void test() {
    String string = "Some { first} text \\{ to {second\\}} scan";

    Pattern pattern = Pattern.compile("(?<!\\\\)(\\{\\s*((?:(?:\\\\})|[^}])*)\\s*\\})");
    assert pattern != null;
    String result = StringUtils.replaceTokens(string, pattern, match -> match.group(2)).toString();
    assertEquals("Some first text \\{ to second\\} scan", result, "replacement doesn't match expectation");
  }

}
