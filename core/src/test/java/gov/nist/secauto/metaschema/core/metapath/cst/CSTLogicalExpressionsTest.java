/*
 * Portions of this software was developed by employees of the National Institute
 * of Standards and Technology (NIST), an agency of the Federal Government and is
 * being made available as a public service. Pursuant to title 17 United States
 * Code Section 105, works of NIST employees are not subject to copyright
 * protection in the United States. This software may be subject to foreign
 * copyright. Permission in the United States and in foreign countries, to the
 * extent that NIST may hold copyright, to use, copy, modify, create derivative
 * works, and distribute this software and its documentation without fee is hereby
 * granted on a non-exclusive basis, provided that this notice and disclaimer
 * of warranty appears in all copies.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS' WITHOUT ANY WARRANTY OF ANY KIND, EITHER
 * EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT LIMITED TO, ANY WARRANTY
 * THAT THE SOFTWARE WILL CONFORM TO SPECIFICATIONS, ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND FREEDOM FROM
 * INFRINGEMENT, AND ANY WARRANTY THAT THE DOCUMENTATION WILL CONFORM TO THE
 * SOFTWARE, OR ANY WARRANTY THAT THE SOFTWARE WILL BE ERROR FREE.  IN NO EVENT
 * SHALL NIST BE LIABLE FOR ANY DAMAGES, INCLUDING, BUT NOT LIMITED TO, DIRECT,
 * INDIRECT, SPECIAL OR CONSEQUENTIAL DAMAGES, ARISING OUT OF, RESULTING FROM,
 * OR IN ANY WAY CONNECTED WITH THIS SOFTWARE, WHETHER OR NOT BASED UPON WARRANTY,
 * CONTRACT, TORT, OR OTHERWISE, WHETHER OR NOT INJURY WAS SUSTAINED BY PERSONS OR
 * PROPERTY OR OTHERWISE, AND WHETHER OR NOT LOSS WAS SUSTAINED FROM, OR AROSE OUT
 * OF THE RESULTS OF, OR USE OF, THE SOFTWARE OR SERVICES PROVIDED HEREUNDER.
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.MetapathExpression;
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
    And expr = new And(list);

    ISequence<?> result = expr.accept(dynamicContext, focus);
    assertEquals(ISequence.of(expectedResult), result);

    result = MetapathExpression.compile(ObjectUtils.notNull(
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

    Or expr = new Or(exp1, exp2);

    ISequence<?> result = expr.accept(dynamicContext, focus);
    assertEquals(ISequence.of(expectedResult), result, "Sequence does not match");

    result = MetapathExpression.compile(ObjectUtils.notNull(
        new StringBuilder()
            .append(bool1.toBoolean() ? "true()" : "false()")
            .append(" or ")
            .append(bool2.toBoolean() ? "true()" : "false()")
            .toString()))
        .evaluate();
    assertEquals(ISequence.of(expectedResult), result, "Sequence does not match");
  }
}
