/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.bool;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.integer;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.string;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.uri;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.yearMonthDuration;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.MetapathException;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidArgumentFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBooleanItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IUntypedAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import org.jmock.Expectations;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

class FnNotTest
    extends FunctionTestBase {
  static Stream<Arguments> provideValues() {
    return Stream.of(
        Arguments.of(null, new IItem[] { yearMonthDuration("P20Y") }),
        Arguments.of(bool(false), new IItem[] { IBooleanItem.TRUE }),
        Arguments.of(bool(true), new IItem[] { IBooleanItem.FALSE }),
        Arguments.of(bool(false), new IItem[] { string("non-blank") }),
        Arguments.of(bool(true), new IItem[] { string("") }),
        Arguments.of(bool(true), new IItem[] { IBooleanItem.TRUE, IBooleanItem.FALSE }),
        Arguments.of(bool(false), new IItem[] { integer(1) }),
        Arguments.of(bool(true), new IItem[] { integer(0) }),
        Arguments.of(bool(false), new IItem[] { integer(-1) }),
        Arguments.of(bool(false), new IItem[] { uri("path") }),
        Arguments.of(bool(true), new IItem[] { uri("") }),
        Arguments.of(bool(true), new IItem[] {}));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void test(@Nullable IBooleanItem expected, @NonNull IItem... values) {
    try {
      assertFunctionResult(
          FnNot.SIGNATURE,
          ISequence.of(expected),
          CollectionUtil.singletonList(ISequence.of(values)));
    } catch (MetapathException ex) {
      assertAll(
          () -> assertNull(expected),
          () -> assertInstanceOf(InvalidArgumentFunctionException.class, ex.getCause()));
    }
  }

  @Test
  void testNodeItem() {
    INodeItem item = getContext().mock(INodeItem.class, "nodeItem");
    assertFunctionResult(
        FnNot.SIGNATURE,
        ISequence.of(IBooleanItem.FALSE),
        CollectionUtil.singletonList(ISequence.of(item)));
  }

  @Test
  void testUntypedAtomicItemBlank() {
    IUntypedAtomicItem item = getContext().mock(IUntypedAtomicItem.class, "untypedAtomicItem");
    assert item != null;

    getContext().checking(new Expectations() {
      { // NOPMD - intentional
        allowing(item).asString();
        will(returnValue(""));
      }
    });

    assertFunctionResult(
        FnNot.SIGNATURE,
        ISequence.of(IBooleanItem.TRUE),
        CollectionUtil.singletonList(ISequence.of(item)));
  }

  @Test
  void testUntypedAtomicItemNonBlank() {
    IUntypedAtomicItem item = getContext().mock(IUntypedAtomicItem.class, "untypedAtomicItem");
    assert item != null;

    getContext().checking(new Expectations() {
      { // NOPMD - intentional
        allowing(item).asString();
        will(returnValue("non-blank"));
      }
    });

    assertFunctionResult(
        FnNot.SIGNATURE,
        ISequence.of(IBooleanItem.FALSE),
        CollectionUtil.singletonList(ISequence.of(item)));
  }
}
