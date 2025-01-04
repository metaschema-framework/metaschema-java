/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.IExpression;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBooleanItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

class CSTLogicalExpressionsTest
    extends ExpressionTestBase {

  private static Stream<Arguments> testAnd() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(IBooleanItem.TRUE, IBooleanItem.TRUE, IBooleanItem.TRUE),
        Arguments.of(IBooleanItem.TRUE, IBooleanItem.FALSE, IBooleanItem.FALSE),
        Arguments.of(IBooleanItem.FALSE, IBooleanItem.TRUE, IBooleanItem.FALSE),
        Arguments.of(IBooleanItem.FALSE, IBooleanItem.FALSE, IBooleanItem.FALSE));
  }

  @DisplayName("And")
  @ParameterizedTest
  @MethodSource
  void testAnd(IBooleanItem bool1, IBooleanItem bool2, IBooleanItem expectedResult) {
    DynamicContext dynamicContext = newDynamicContext();

    Mockery context = getContext();

    ISequence<?> focus = ISequence.empty();

    IExpression exp1 = context.mock(IExpression.class, "exp1");
    IExpression exp2 = context.mock(IExpression.class, "exp2");

    context.checking(new Expectations() {
      { // NOPMD - intentional
        atMost(1).of(exp1).accept(dynamicContext, focus);
        will(returnValue(ISequence.of(bool1)));
        atMost(1).of(exp2).accept(dynamicContext, focus);
        will(returnValue(ISequence.of(bool2)));
      }
    });

    List<IExpression> list = List.of(exp1, exp2);
    assert list != null;
    And expr = new And("test data", list);

    ISequence<?> result = expr.accept(dynamicContext, focus);
    assertEquals(ISequence.of(expectedResult), result);

    result = IMetapathExpression.compile(ObjectUtils.notNull(
        new StringBuilder()
            .append(bool1.toBoolean() ? "true()" : "false()")
            .append(" and ")
            .append(bool2.toBoolean() ? "true()" : "false()")
            .toString()))
        .evaluate();
    assertEquals(ISequence.of(expectedResult), result, "Sequence does not match");
  }

  private static Stream<Arguments> testOr() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(IBooleanItem.TRUE, IBooleanItem.TRUE, IBooleanItem.TRUE),
        Arguments.of(IBooleanItem.TRUE, IBooleanItem.FALSE, IBooleanItem.TRUE),
        Arguments.of(IBooleanItem.FALSE, IBooleanItem.TRUE, IBooleanItem.TRUE),
        Arguments.of(IBooleanItem.FALSE, IBooleanItem.FALSE, IBooleanItem.FALSE));
  }

  @DisplayName("Or")
  @ParameterizedTest
  @MethodSource
  void testOr(IBooleanItem bool1, IBooleanItem bool2, IBooleanItem expectedResult) {
    DynamicContext dynamicContext = newDynamicContext();
    Mockery context = getContext();

    ISequence<?> focus = ISequence.empty();

    IExpression exp1 = context.mock(IExpression.class, "exp1");
    IExpression exp2 = context.mock(IExpression.class, "exp2");

    context.checking(new Expectations() {
      { // NOPMD - intentional
        atMost(1).of(exp1).accept(dynamicContext, focus);
        will(returnValue(ISequence.of(bool1)));
        atMost(1).of(exp2).accept(dynamicContext, focus);
        will(returnValue(ISequence.of(bool2)));
      }
    });

    Or expr = new Or("test data", exp1, exp2);

    ISequence<?> result = expr.accept(dynamicContext, focus);
    assertEquals(ISequence.of(expectedResult), result, "Sequence does not match");

    result = IMetapathExpression.compile(ObjectUtils.notNull(
        new StringBuilder()
            .append(bool1.toBoolean() ? "true()" : "false()")
            .append(" or ")
            .append(bool2.toBoolean() ? "true()" : "false()")
            .toString()))
        .evaluate();
    assertEquals(ISequence.of(expectedResult), result, "Sequence does not match");
  }
}
