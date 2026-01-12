/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import dev.metaschema.core.metapath.ContextAbsentDynamicMetapathException;
import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.FocusContext;
import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.item.atomic.IIntegerItem;
import dev.metaschema.core.util.ObjectUtils;

class FnPositionTest {

  @Test
  void testPositionReturnsCorrectValueWhenFocusContextIsSet() {
    // Create a focus context with position 3 out of 5
    IIntegerItem item = IIntegerItem.valueOf(42);
    FocusContext focusContext = FocusContext.of(item, 3, 5);

    // Create a dynamic context with the focus context
    DynamicContext parentContext = new DynamicContext();
    DynamicContext context = parentContext.subContext(focusContext);

    // Evaluate position() - pass the context item as the focus
    IIntegerItem result = ObjectUtils.notNull(IMetapathExpression.compile("fn:position()")
        .evaluateAs(item, IMetapathExpression.ResultType.ITEM, context));

    assertEquals(3, result.asInteger().intValue(), "position() should return 3");
  }

  @Test
  void testPositionReturnsOneForSingletonSequence() {
    // Create a focus context with position 1 out of 1
    IIntegerItem item = IIntegerItem.valueOf(100);
    FocusContext focusContext = FocusContext.of(item, 1, 1);

    DynamicContext parentContext = new DynamicContext();
    DynamicContext context = parentContext.subContext(focusContext);

    IIntegerItem result = ObjectUtils.notNull(IMetapathExpression.compile("fn:position()")
        .evaluateAs(item, IMetapathExpression.ResultType.ITEM, context));

    assertEquals(1, result.asInteger().intValue(), "position() should return 1 for singleton");
  }

  @Test
  void testPositionReturnsLastPosition() {
    // Create a focus context with position 10 out of 10 (last position)
    IIntegerItem item = IIntegerItem.valueOf(999);
    FocusContext focusContext = FocusContext.of(item, 10, 10);

    DynamicContext parentContext = new DynamicContext();
    DynamicContext context = parentContext.subContext(focusContext);

    IIntegerItem result = ObjectUtils.notNull(IMetapathExpression.compile("fn:position()")
        .evaluateAs(item, IMetapathExpression.ResultType.ITEM, context));

    assertEquals(10, result.asInteger().intValue(), "position() should return 10 (last position)");
  }

  @Test
  void testPositionThrowsXPDY0002WhenNoFocusContext() {
    // Create a dynamic context without setting a focus context
    DynamicContext context = new DynamicContext();

    // Evaluate position() - should throw XPDY0002 (context absent)
    // The function framework checks for context item before calling the function
    assertThrows(
        ContextAbsentDynamicMetapathException.class,
        () -> IMetapathExpression.compile("fn:position()")
            .evaluateAs(null, IMetapathExpression.ResultType.ITEM, context));
  }
}
