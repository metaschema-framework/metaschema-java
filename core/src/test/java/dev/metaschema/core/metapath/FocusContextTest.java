/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.metaschema.core.metapath.item.IItem;
import dev.metaschema.core.metapath.item.atomic.IIntegerItem;

import org.junit.jupiter.api.Test;

class FocusContextTest {

  @Test
  void testFactoryMethodWithValidValues() {
    IItem item = IIntegerItem.valueOf(42);
    FocusContext context = FocusContext.of(item, 1, 3);

    assertAll(
        () -> assertSame(item, context.getContextItem(), "Context item should be the same instance"),
        () -> assertEquals(1, context.getPosition(), "Position should be 1"),
        () -> assertEquals(3, context.getSize(), "Size should be 3"));
  }

  @Test
  void testFactoryMethodWithMiddlePosition() {
    IItem item = IIntegerItem.valueOf(100);
    FocusContext context = FocusContext.of(item, 5, 10);

    assertAll(
        () -> assertSame(item, context.getContextItem()),
        () -> assertEquals(5, context.getPosition()),
        () -> assertEquals(10, context.getSize()));
  }

  @Test
  void testFactoryMethodWithLastPosition() {
    IItem item = IIntegerItem.valueOf(999);
    FocusContext context = FocusContext.of(item, 7, 7);

    assertAll(
        () -> assertSame(item, context.getContextItem()),
        () -> assertEquals(7, context.getPosition()),
        () -> assertEquals(7, context.getSize()));
  }

  @Test
  void testFactoryMethodWithSingletonSequence() {
    IItem item = IIntegerItem.valueOf(1);
    FocusContext context = FocusContext.of(item, 1, 1);

    assertAll(
        () -> assertSame(item, context.getContextItem()),
        () -> assertEquals(1, context.getPosition()),
        () -> assertEquals(1, context.getSize()));
  }

  @Test
  void testFactoryMethodRejectsZeroPosition() {
    IItem item = IIntegerItem.valueOf(1);

    IllegalArgumentException thrown = assertThrows(
        IllegalArgumentException.class,
        () -> FocusContext.of(item, 0, 3));

    assertEquals("Position must be >= 1, got: 0", thrown.getMessage());
  }

  @Test
  void testFactoryMethodRejectsNegativePosition() {
    IItem item = IIntegerItem.valueOf(1);

    IllegalArgumentException thrown = assertThrows(
        IllegalArgumentException.class,
        () -> FocusContext.of(item, -1, 3));

    assertEquals("Position must be >= 1, got: -1", thrown.getMessage());
  }

  @Test
  void testFactoryMethodRejectsZeroSize() {
    IItem item = IIntegerItem.valueOf(1);

    IllegalArgumentException thrown = assertThrows(
        IllegalArgumentException.class,
        () -> FocusContext.of(item, 1, 0));

    assertEquals("Size must be >= 1, got: 0", thrown.getMessage());
  }

  @Test
  void testFactoryMethodRejectsNegativeSize() {
    IItem item = IIntegerItem.valueOf(1);

    IllegalArgumentException thrown = assertThrows(
        IllegalArgumentException.class,
        () -> FocusContext.of(item, 1, -5));

    assertEquals("Size must be >= 1, got: -5", thrown.getMessage());
  }

  @Test
  void testFactoryMethodRejectsPositionGreaterThanSize() {
    IItem item = IIntegerItem.valueOf(1);

    IllegalArgumentException thrown = assertThrows(
        IllegalArgumentException.class,
        () -> FocusContext.of(item, 5, 3));

    assertEquals("Position (5) cannot be greater than size (3)", thrown.getMessage());
  }
}
