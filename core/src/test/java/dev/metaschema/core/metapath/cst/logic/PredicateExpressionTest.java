/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.cst.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.ExpressionTestBase;
import dev.metaschema.core.metapath.IExpression;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.testsupport.mocking.MockNodeItemFactory;
import dev.metaschema.core.util.CollectionUtil;

import org.junit.jupiter.api.Test;

import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
class PredicateExpressionTest
    extends ExpressionTestBase {

  @SuppressWarnings("null")
  @Test
  void testPredicateWithValues() {
    DynamicContext dynamicContext = newDynamicContext();

    MockNodeItemFactory mockFactory = new MockNodeItemFactory();

    IAssemblyNodeItem item = mockFactory.assembly(
        IEnhancedQName.of("assembly", NS),
        CollectionUtil.emptyList(),
        CollectionUtil.emptyList());
    List<IExpression> predicates = List.of();

    ISequence<?> focus = ISequence.of(item).reusable();

    // setup step expression
    IExpression stepExpr = mockFactory.mock(IExpression.class);
    doReturn(focus).when(stepExpr).accept(dynamicContext, focus);

    PredicateExpression expr = new PredicateExpression("test data", stepExpr, predicates);

    ISequence<?> result = expr.accept(dynamicContext, focus);

    verify(stepExpr, times(1)).accept(dynamicContext, focus);

    assertEquals(ISequence.of(item).reusable(), result, "Sequence must match");
  }

  @Test
  void testPredicateWithoutValues() {
    DynamicContext dynamicContext = newDynamicContext().disablePredicateEvaluation();
    MockNodeItemFactory mockFactory = new MockNodeItemFactory();

    IAssemblyNodeItem item = mockFactory.assembly(
        IEnhancedQName.of("assembly", NS),
        CollectionUtil.emptyList(),
        CollectionUtil.emptyList());
    @SuppressWarnings("unchecked")
    List<IExpression> predicates = mockFactory.mock(List.class);

    ISequence<?> focus = ISequence.of(item).reusable();

    // setup step expression
    IExpression stepExpr = mockFactory.mock(IExpression.class);
    doReturn(focus).when(stepExpr).accept(dynamicContext, focus);

    PredicateExpression expr = new PredicateExpression("test data", stepExpr, predicates);

    ISequence<?> result = expr.accept(dynamicContext, ISequence.of(item));

    assertEquals(ISequence.of(item), result, "Sequence must match");

    verify(stepExpr, times(1)).accept(dynamicContext, focus);
    verify(predicates, never()).stream();
    verify(predicates, never()).iterator();
  }
}
