/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.function.ComparisonFunctions;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBooleanItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class ValueComparisonTest
    extends ExpressionTestBase {

  private static Stream<Arguments> testValueComparison() { // NOPMD - false positive
    return Stream.of(
        // string
        Arguments.of(IStringItem.valueOf("AbC"), ComparisonFunctions.Operator.EQ, IStringItem.valueOf("AbC"),
            IBooleanItem.TRUE),
        Arguments.of(IStringItem.valueOf("AbC"), ComparisonFunctions.Operator.EQ, IStringItem.valueOf("xYz"),
            IBooleanItem.FALSE),
        Arguments.of(IStringItem.valueOf("A.1"), ComparisonFunctions.Operator.NE, IStringItem.valueOf("A.2"),
            IBooleanItem.TRUE),
        Arguments.of(IStringItem.valueOf("A.1"), ComparisonFunctions.Operator.NE, IStringItem.valueOf("A.1"),
            IBooleanItem.FALSE),
        Arguments.of(IStringItem.valueOf("A.3"), ComparisonFunctions.Operator.GE, IStringItem.valueOf("A.2"),
            IBooleanItem.TRUE),
        Arguments.of(IStringItem.valueOf("B\\1"), ComparisonFunctions.Operator.GE, IStringItem.valueOf("B\\1"),
            IBooleanItem.TRUE),
        Arguments.of(IStringItem.valueOf("A.1"), ComparisonFunctions.Operator.GE, IStringItem.valueOf("A.2"),
            IBooleanItem.FALSE),
        Arguments.of(IStringItem.valueOf("A.1@"), ComparisonFunctions.Operator.GT, IStringItem.valueOf("A.1"),
            IBooleanItem.TRUE),
        Arguments.of(IStringItem.valueOf("X.1"), ComparisonFunctions.Operator.GT, IStringItem.valueOf("X.1"),
            IBooleanItem.FALSE),
        Arguments.of(IStringItem.valueOf("A"), ComparisonFunctions.Operator.LE, IStringItem.valueOf("A.2"),
            IBooleanItem.TRUE),
        Arguments.of(IStringItem.valueOf("B\\1"), ComparisonFunctions.Operator.LE, IStringItem.valueOf("C\\1"),
            IBooleanItem.TRUE),
        Arguments.of(IStringItem.valueOf("X#"), ComparisonFunctions.Operator.LE, IStringItem.valueOf("X"),
            IBooleanItem.FALSE),
        Arguments.of(IStringItem.valueOf("A"), ComparisonFunctions.Operator.LT, IStringItem.valueOf("A.2"),
            IBooleanItem.TRUE),
        Arguments.of(IStringItem.valueOf("X#"), ComparisonFunctions.Operator.LT, IStringItem.valueOf("X"),
            IBooleanItem.FALSE),
        // boolean
        Arguments.of(IBooleanItem.TRUE, ComparisonFunctions.Operator.EQ, IBooleanItem.TRUE, IBooleanItem.TRUE),
        Arguments.of(IBooleanItem.FALSE, ComparisonFunctions.Operator.EQ, IBooleanItem.FALSE, IBooleanItem.TRUE),
        Arguments.of(IBooleanItem.TRUE, ComparisonFunctions.Operator.EQ, IBooleanItem.FALSE, IBooleanItem.FALSE),
        Arguments.of(IBooleanItem.FALSE, ComparisonFunctions.Operator.EQ, IBooleanItem.TRUE, IBooleanItem.FALSE),
        Arguments.of(IBooleanItem.TRUE, ComparisonFunctions.Operator.NE, IBooleanItem.FALSE, IBooleanItem.TRUE),
        Arguments.of(IBooleanItem.TRUE, ComparisonFunctions.Operator.NE, IBooleanItem.FALSE, IBooleanItem.TRUE),
        Arguments.of(IBooleanItem.TRUE, ComparisonFunctions.Operator.NE, IBooleanItem.TRUE, IBooleanItem.FALSE),
        Arguments.of(IBooleanItem.FALSE, ComparisonFunctions.Operator.NE, IBooleanItem.FALSE, IBooleanItem.FALSE),
        Arguments.of(IBooleanItem.TRUE, ComparisonFunctions.Operator.GE, IBooleanItem.TRUE, IBooleanItem.TRUE),
        Arguments.of(IBooleanItem.TRUE, ComparisonFunctions.Operator.GT, IBooleanItem.TRUE, IBooleanItem.FALSE),
        Arguments.of(IBooleanItem.TRUE, ComparisonFunctions.Operator.LE, IBooleanItem.TRUE, IBooleanItem.TRUE),
        Arguments.of(IBooleanItem.TRUE, ComparisonFunctions.Operator.LT, IBooleanItem.TRUE, IBooleanItem.FALSE)

    );
  }

  @SuppressWarnings("null")
  @ParameterizedTest
  @MethodSource
  void testValueComparison(
      IItem leftItem,
      ComparisonFunctions.Operator operator,
      IItem rightItem,
      IBooleanItem expectedResult) {
    DynamicContext dynamicContext = newDynamicContext();
    Mockery context = getContext();

    ISequence<?> focus = ISequence.empty();

    IExpression exp1 = context.mock(IExpression.class, "exp1");
    IExpression exp2 = context.mock(IExpression.class, "exp2");

    context.checking(new Expectations() {
      { // NOPMD - intentional
        atMost(1).of(exp1).accept(dynamicContext, focus);
        will(returnValue(ISequence.of(leftItem)));
        atMost(1).of(exp2).accept(dynamicContext, focus);
        will(returnValue(ISequence.of(rightItem)));
      }
    });

    ValueComparison expr = new ValueComparison(exp1, operator, exp2);

    ISequence<?> result = expr.accept(dynamicContext, focus);
    assertEquals(ISequence.of(expectedResult), result, "Sequence does not match");
  }
}
