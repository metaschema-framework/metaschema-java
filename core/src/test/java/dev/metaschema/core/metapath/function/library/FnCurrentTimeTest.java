/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import static org.junit.jupiter.api.Assertions.assertFalse;

import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.util.ObjectUtils;

import org.junit.jupiter.api.Test;

class FnCurrentTimeTest {
  @Test
  void test() {
    String currentTime = ObjectUtils.notNull(IMetapathExpression.compile("fn:current-time()")
        .evaluateAs(IMetapathExpression.ResultType.STRING));
    System.out.println(currentTime);
    assertFalse(currentTime.isBlank());
  }
}
