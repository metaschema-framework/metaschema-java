/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
class PredicateExpressionTest
    extends ExpressionTestBase {

  @Test
  void testPredicateWithValues() {
    DynamicContext dynamicContext = newDynamicContext();
    Mockery context = getContext();

    @SuppressWarnings("null")
    @NonNull
    IExpression stepExpr = context.mock(IExpression.class);
    ISequence<?> stepResult = context.mock(ISequence.class, "stepResult");
    @SuppressWarnings("null")
    @NonNull
    IAssemblyNodeItem item = context.mock(IAssemblyNodeItem.class);
    @SuppressWarnings({ "unchecked", "null" })
    @NonNull
    List<IExpression> predicates = context.mock(List.class, "predicates");

    context.checking(new Expectations() {
      { // NOPMD - intentional
        allowing(stepExpr).getStaticResultType();
        will(returnValue(IAssemblyNodeItem.class));
        oneOf(stepExpr).accept(dynamicContext, ISequence.of(item));
        will(returnValue(stepResult));

        atMost(1).of(stepResult).stream();
        will(returnValue(Stream.of(item)));
        atMost(1).of(stepResult).getValue();
        will(returnValue(CollectionUtil.singletonList(item)));

        allowing(item).getNodeItem();
        will(returnValue(item));

        atMost(1).of(predicates).stream();
        will(returnValue(Stream.empty()));
        atMost(1).of(predicates).iterator();
        will(returnValue(Stream.empty()));
      }
    });

    PredicateExpression expr = new PredicateExpression(stepExpr, predicates);

    ISequence<?> result = expr.accept(dynamicContext, ISequence.of(item));
    assertEquals(ISequence.of(item), result, "Sequence does not match");
  }

  @Test
  void testPredicateWithoutValues() {
    DynamicContext dynamicContext = newDynamicContext().disablePredicateEvaluation();
    Mockery context = getContext();

    @SuppressWarnings("null")
    @NonNull
    IExpression stepExpr = context.mock(IExpression.class);
    ISequence<?> stepResult = context.mock(ISequence.class, "stepResult");
    @SuppressWarnings("null")
    @NonNull
    IAssemblyNodeItem item = context.mock(IAssemblyNodeItem.class);
    @SuppressWarnings({ "unchecked", "null" })
    @NonNull
    List<IExpression> predicates = context.mock(List.class, "predicates");

    context.checking(new Expectations() {
      { // NOPMD - intentional
        allowing(stepExpr).getStaticResultType();
        will(returnValue(IAssemblyNodeItem.class));
        oneOf(stepExpr).accept(dynamicContext, ISequence.of(item));
        will(returnValue(stepResult));

        atMost(1).of(stepResult).stream();
        will(returnValue(Stream.of(item)));
        atMost(1).of(stepResult).getValue();
        will(returnValue(CollectionUtil.singletonList(item)));

        allowing(item).getNodeItem();
        will(returnValue(item));

        never(predicates).stream();
        never(predicates).iterator();
      }
    });

    PredicateExpression expr = new PredicateExpression(stepExpr, predicates);

    ISequence<?> result = expr.accept(dynamicContext, ISequence.of(item));
    assertEquals(ISequence.of(item), result, "Sequence does not match");
  }
}
