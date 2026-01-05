/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.metaschema.core.metapath.ContextAbsentDynamicMetapathException;
import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.FocusContext;
import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.item.atomic.IIntegerItem;
import dev.metaschema.core.util.ObjectUtils;

import org.junit.jupiter.api.Test;

class FnLastTest {

  @Test
  void testLastReturnsCorrectValueWhenFocusContextIsSet() {
    // Create a focus context with position 3 out of 5
    IIntegerItem item = IIntegerItem.valueOf(42);
    FocusContext focusContext = FocusContext.of(item, 3, 5);

    // Create a dynamic context with the focus context
    DynamicContext parentContext = new DynamicContext();
    DynamicContext context = parentContext.subContext(focusContext);

    // Evaluate last() - pass the context item as the focus
    IIntegerItem result = ObjectUtils.notNull(IMetapathExpression.compile("fn:last()")
        .evaluateAs(item, IMetapathExpression.ResultType.ITEM, context));

    assertEquals(5, result.asInteger().intValue(), "last() should return 5");
  }

  @Test
  void testLastReturnsOneForSingletonSequence() {
    // Create a focus context with position 1 out of 1
    IIntegerItem item = IIntegerItem.valueOf(100);
    FocusContext focusContext = FocusContext.of(item, 1, 1);

    DynamicContext parentContext = new DynamicContext();
    DynamicContext context = parentContext.subContext(focusContext);

    IIntegerItem result = ObjectUtils.notNull(IMetapathExpression.compile("fn:last()")
        .evaluateAs(item, IMetapathExpression.ResultType.ITEM, context));

    assertEquals(1, result.asInteger().intValue(), "last() should return 1 for singleton");
  }

  @Test
  void testLastReturnsCorrectSizeFromDifferentPosition() {
    // Create a focus context with position 1 out of 10 - last() should still return
    // 10
    IIntegerItem item = IIntegerItem.valueOf(999);
    FocusContext focusContext = FocusContext.of(item, 1, 10);

    DynamicContext parentContext = new DynamicContext();
    DynamicContext context = parentContext.subContext(focusContext);

    IIntegerItem result = ObjectUtils.notNull(IMetapathExpression.compile("fn:last()")
        .evaluateAs(item, IMetapathExpression.ResultType.ITEM, context));

    assertEquals(10, result.asInteger().intValue(), "last() should return 10 (sequence size)");
  }

  @Test
  void testLastThrowsXPDY0002WhenNoFocusContext() {
    // Create a dynamic context without setting a focus context
    DynamicContext context = new DynamicContext();

    // Evaluate last() - should throw XPDY0002 (context absent)
    // The function framework checks for context item before calling the function
    assertThrows(
        ContextAbsentDynamicMetapathException.class,
        () -> IMetapathExpression.compile("fn:last()")
            .evaluateAs(null, IMetapathExpression.ResultType.ITEM, context));
  }
}
