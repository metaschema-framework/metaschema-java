/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IIntegerItem;

import org.junit.jupiter.api.Test;

/**
 * Tests for focus context support in DynamicContext.
 */
class DynamicContextFocusTest {

  @Test
  void testNewContextHasNoFocusContext() {
    DynamicContext context = new DynamicContext();

    assertNull(context.getFocusContext(), "New context should have no focus context");
  }

  @Test
  void testSubContextWithFocusContext() {
    DynamicContext parent = new DynamicContext();
    IItem item = IIntegerItem.valueOf(42);
    FocusContext focus = FocusContext.of(item, 2, 5);

    DynamicContext child = parent.subContext(focus);

    assertAll(
        () -> assertNotSame(parent, child, "Child should be a new context"),
        () -> assertNotNull(child.getFocusContext(), "Child should have focus context"),
        () -> assertSame(focus, child.getFocusContext(), "Child should have the provided focus context"),
        () -> assertNull(parent.getFocusContext(), "Parent should still have no focus context"));
  }

  @Test
  void testSubContextPreservesFocusContext() {
    DynamicContext parent = new DynamicContext();
    IItem item = IIntegerItem.valueOf(42);
    FocusContext focus = FocusContext.of(item, 2, 5);

    DynamicContext contextWithFocus = parent.subContext(focus);
    // Create a sub-context without specifying new focus (e.g., for variable
    // binding)
    DynamicContext child = contextWithFocus.subContext();

    assertAll(
        () -> assertNotSame(contextWithFocus, child, "Child should be a new context"),
        () -> assertNotNull(child.getFocusContext(), "Child should inherit parent's focus context"),
        () -> assertSame(focus, child.getFocusContext(), "Child should have same focus as parent"));
  }

  @Test
  void testSubContextReplaceFocusContext() {
    DynamicContext parent = new DynamicContext();
    IItem item1 = IIntegerItem.valueOf(1);
    IItem item2 = IIntegerItem.valueOf(2);
    FocusContext focus1 = FocusContext.of(item1, 1, 3);
    FocusContext focus2 = FocusContext.of(item2, 2, 3);

    DynamicContext contextWithFocus1 = parent.subContext(focus1);
    // Replace focus context with a new one (e.g., for a nested predicate)
    DynamicContext contextWithFocus2 = contextWithFocus1.subContext(focus2);

    assertAll(
        () -> assertSame(focus1, contextWithFocus1.getFocusContext(), "First context should have focus1"),
        () -> assertSame(focus2, contextWithFocus2.getFocusContext(), "Second context should have focus2"),
        () -> assertNotSame(focus1, focus2, "Focus contexts should be different"));
  }

  @Test
  void testFocusContextAccessors() {
    DynamicContext parent = new DynamicContext();
    IItem item = IIntegerItem.valueOf(100);
    FocusContext focus = FocusContext.of(item, 3, 7);

    DynamicContext context = parent.subContext(focus);
    FocusContext retrievedFocus = context.getFocusContext();

    assertAll(
        () -> assertNotNull(retrievedFocus),
        () -> assertSame(item, retrievedFocus.getContextItem()),
        () -> assertEquals(3, retrievedFocus.getPosition()),
        () -> assertEquals(7, retrievedFocus.getSize()));
  }

  @Test
  void testNestedSubContextsPreserveFocus() {
    // Simulates: (1,2,3)[some $x in (4,5) satisfies $x > . and position() = 1]
    // The "some" creates a sub-context for $x, but position() should still work
    DynamicContext root = new DynamicContext();
    IItem predicateItem = IIntegerItem.valueOf(1);
    FocusContext predicateFocus = FocusContext.of(predicateItem, 1, 3);

    // Enter predicate - establishes focus
    DynamicContext predicateContext = root.subContext(predicateFocus);

    // Enter "some" expression - creates variable binding scope, preserves focus
    DynamicContext someContext = predicateContext.subContext();

    assertAll(
        () -> assertSame(predicateFocus, predicateContext.getFocusContext(),
            "Predicate context should have focus"),
        () -> assertSame(predicateFocus, someContext.getFocusContext(),
            "Some context should inherit predicate's focus"));
  }
}
