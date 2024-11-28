/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.bool;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.integer;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.string;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.MetapathException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBooleanItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IIntegerItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class CastFunctionTest
    extends ExpressionTestBase {

  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        // Boolean cases
        Arguments.of(string("ABCD0"), IBooleanItem.type(), bool(false)),
        Arguments.of(string("true"), IBooleanItem.type(), bool(true)),
        Arguments.of(string("0"), IBooleanItem.type(), bool(false)),
        Arguments.of(string("1"), IBooleanItem.type(), bool(true)),
        Arguments.of(string("yes"), IBooleanItem.type(), bool(false)),
        Arguments.of(string("no"), IBooleanItem.type(), bool(false)),
        Arguments.of(string("TRUE"), IBooleanItem.type(), bool(true)),
        Arguments.of(string(""), IBooleanItem.type(), bool(false)),
        Arguments.of(string("   "), IBooleanItem.type(), bool(false)),
        // Integer cases
        Arguments.of(string("1234567"), IIntegerItem.type(), integer(1234567)),
        Arguments.of(string("-1234567"), IIntegerItem.type(), integer(-1234567)),
        Arguments.of(string("0"), IIntegerItem.type(), integer(0)),
        Arguments.of(
            string(ObjectUtils.notNull(Integer.toString(Integer.MAX_VALUE))),
            IIntegerItem.type(),
            integer(Integer.MAX_VALUE)),
        Arguments.of(
            string(ObjectUtils.notNull(Integer.toString(Integer.MIN_VALUE))),
            IIntegerItem.type(),
            integer(Integer.MIN_VALUE)));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void testExpression(@NonNull IStringItem text, @NonNull IAtomicOrUnionType<?> type,
      @NonNull IAnyAtomicItem expected) {
    IAnyAtomicItem result = IMetapathExpression.compile(type.getQName().toEQName() + "('" + text.asString() + "')")
        .evaluateAs(null, IMetapathExpression.ResultType.ITEM, newDynamicContext());
    assertEquals(expected, result);
  }

  @Test
  void testInvalidCasts() {
    assertThrows(MetapathException.class, () -> {
      IMetapathExpression.compile(IIntegerItem.type().getQName().toEQName() + "('invalid')")
          .evaluateAs(null, IMetapathExpression.ResultType.ITEM, newDynamicContext());
    });
  }
}
