/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath;

import static dev.metaschema.core.metapath.TestUtils.decimal;
import static dev.metaschema.core.metapath.TestUtils.integer;
import static dev.metaschema.core.metapath.TestUtils.string;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import dev.metaschema.core.metapath.item.atomic.IDecimalItem;
import dev.metaschema.core.metapath.item.atomic.IIntegerItem;
import dev.metaschema.core.metapath.item.atomic.INumericItem;
import dev.metaschema.core.metapath.item.atomic.IStringItem;
import dev.metaschema.core.metapath.type.InvalidTypeMetapathException;

class ISequenceTest {

  @Test
  void testGetFirstNonSingleton() {
    assertAll(
        () -> assertEquals(integer(1), ISequence.of(integer(1), integer(2)).getFirstItem(false)),
        () -> assertEquals(integer(3), ISequence.of(integer(3)).getFirstItem(false)),
        () -> assertNull(ISequence.of().getFirstItem(false)));
  }

  @Test
  void testGetFirstSingleton() {
    assertAll(
        () -> assertThrows(InvalidTypeMetapathException.class,
            () -> ISequence.of(integer(1), integer(2)).getFirstItem(true)),
        () -> assertEquals(integer(3), ISequence.of(integer(3)).getFirstItem(true)),
        () -> assertNull(ISequence.of().getFirstItem(true)));
  }

  @Test
  void testCountTypesEmpty() {
    ISequence<IAnyAtomicItem> sequence = ISequence.of();
    Set<Class<? extends IAnyAtomicItem>> classes = Set.of(IStringItem.class, INumericItem.class);

    Map<Class<? extends IAnyAtomicItem>, Integer> counts = sequence.countTypes(classes);

    assertTrue(counts.isEmpty());
  }

  @Test
  void testCountTypesSingleType() {
    ISequence<IAnyAtomicItem> sequence = ISequence.of(integer(1), integer(2), integer(3));
    Set<Class<? extends IAnyAtomicItem>> classes = Set.of(INumericItem.class);

    Map<Class<? extends IAnyAtomicItem>, Integer> counts = sequence.countTypes(classes);

    assertEquals(1, counts.size());
    assertEquals(3, counts.get(INumericItem.class));
  }

  @Test
  void testCountTypesMultipleTypes() {
    ISequence<IAnyAtomicItem> sequence = ISequence.of(integer(1), string("test"), integer(2), decimal(3));
    Set<Class<? extends IAnyAtomicItem>> classes = Set.of(IStringItem.class, INumericItem.class, IDecimalItem.class);

    Map<Class<? extends IAnyAtomicItem>, Integer> counts = sequence.countTypes(classes);

    // integer(1), integer(2), decimal(3) are all INumericItem (3 items)
    assertEquals(3, counts.get(INumericItem.class));
    // string("test") is IStringItem (1 item)
    assertEquals(1, counts.get(IStringItem.class));
    // IIntegerItem extends IDecimalItem, so all 3 numeric items (2 integers + 1
    // decimal) match IDecimalItem
    assertEquals(3, counts.get(IDecimalItem.class));
  }

  @Test
  void testCountTypesInheritance() {
    // Test that IIntegerItem items are counted as INumericItem
    ISequence<IAnyAtomicItem> sequence = ISequence.of(integer(1), integer(2));
    Set<Class<? extends IAnyAtomicItem>> classes = Set.of(INumericItem.class, IIntegerItem.class);

    Map<Class<? extends IAnyAtomicItem>, Integer> counts = sequence.countTypes(classes);

    // Both integers should count for INumericItem (parent type)
    assertEquals(2, counts.get(INumericItem.class));
    // Both integers should also count for IIntegerItem (exact type)
    assertEquals(2, counts.get(IIntegerItem.class));
  }

  @Test
  void testCountTypesNoMatch() {
    ISequence<IAnyAtomicItem> sequence = ISequence.of(integer(1), integer(2));
    Set<Class<? extends IAnyAtomicItem>> classes = Set.of(IStringItem.class);

    Map<Class<? extends IAnyAtomicItem>, Integer> counts = sequence.countTypes(classes);

    // No strings in the sequence
    assertTrue(counts.isEmpty());
  }

  @Test
  void testGetItemTypesEmpty() {
    ISequence<IAnyAtomicItem> sequence = ISequence.of();

    List<Class<? extends IAnyAtomicItem>> types = sequence.getItemTypes();

    assertTrue(types.isEmpty());
  }

  @Test
  void testGetItemTypesSingleItem() {
    ISequence<IAnyAtomicItem> sequence = ISequence.of(integer(42));

    List<Class<? extends IAnyAtomicItem>> types = sequence.getItemTypes();

    assertEquals(1, types.size());
    assertTrue(IIntegerItem.class.isAssignableFrom(types.get(0)));
  }

  @Test
  void testGetItemTypesMultipleItems() {
    ISequence<IAnyAtomicItem> sequence = ISequence.of(integer(1), string("test"), decimal(3));

    List<Class<? extends IAnyAtomicItem>> types = sequence.getItemTypes();

    assertEquals(3, types.size());
    // First item is an integer
    assertTrue(IIntegerItem.class.isAssignableFrom(types.get(0)));
    // Second item is a string
    assertTrue(IStringItem.class.isAssignableFrom(types.get(1)));
    // Third item is a decimal
    assertTrue(IDecimalItem.class.isAssignableFrom(types.get(2)));
  }

  @Test
  void testGetItemTypesPreservesOrder() {
    ISequence<IAnyAtomicItem> sequence = ISequence.of(string("a"), integer(1), string("b"));

    List<Class<? extends IAnyAtomicItem>> types = sequence.getItemTypes();

    assertEquals(3, types.size());
    assertTrue(IStringItem.class.isAssignableFrom(types.get(0)));
    assertTrue(IIntegerItem.class.isAssignableFrom(types.get(1)));
    assertTrue(IStringItem.class.isAssignableFrom(types.get(2)));
  }

  @Test
  void testOfCollectionReturnsSequenceUnchanged() {
    // When ofCollection receives an ISequence, it should return the same instance
    ISequence<IAnyAtomicItem> original = ISequence.of(integer(1), integer(2));

    ISequence<IAnyAtomicItem> result = ISequence.ofCollection(original);

    // Should be the exact same instance, not a copy
    assertSame(original, result);
  }
}
