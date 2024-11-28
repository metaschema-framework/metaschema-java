/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDayTimeDurationItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.junit.jupiter.api.Test;

import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

class FnImplicitTimezoneTest {

  @Test
  void test() {
    DynamicContext context = new DynamicContext();
    IDayTimeDurationItem currentTime = ObjectUtils.notNull(IMetapathExpression.compile("fn:implicit-timezone()")
        .evaluateAs(null, IMetapathExpression.ResultType.ITEM, context));

    assertEquals(
        context.getCurrentDateTime().getOffset().get(ChronoField.OFFSET_SECONDS),
        currentTime.asDuration().get(ChronoUnit.SECONDS),
        "The offset in seconds must be equal.");
  }
}
