/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static org.junit.jupiter.api.Assertions.assertFalse;

import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

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
