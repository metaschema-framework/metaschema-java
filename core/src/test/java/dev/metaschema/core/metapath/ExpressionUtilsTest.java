/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.metaschema.core.metapath.cst.ExpressionUtils;
import dev.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import dev.metaschema.core.metapath.item.node.IDefinitionNodeItem;
import dev.metaschema.core.metapath.item.node.IFieldNodeItem;
import dev.metaschema.core.metapath.item.node.IFlagNodeItem;
import dev.metaschema.core.metapath.item.node.IModelNodeItem;
import dev.metaschema.core.metapath.item.node.INodeItem;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.junit5.JUnit5Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

class ExpressionUtilsTest {

  @RegisterExtension
  Mockery context = new JUnit5Mockery() {
    {
      setThreadingPolicy(new Synchroniser());
    }
  };

  private IExpression basicFlagExpr1;
  private IExpression basicFlagExpr2;
  private IExpression basicAssemblyExpr;
  private IExpression basicFieldExpr;

  @Test
  void testTwoFlags() {
    final Class<INodeItem> baseType = INodeItem.class;
    basicFlagExpr1 = context.mock(IExpression.class, "basicFlagExpr1");
    basicFlagExpr2 = context.mock(IExpression.class, "basicFlagExpr2");

    context.checking(new Expectations() {
      { // NOPMD - intentional
        allowing(basicFlagExpr1).getStaticResultType();
        will(returnValue(IFlagNodeItem.class));
        allowing(basicFlagExpr2).getStaticResultType();
        will(returnValue(IFlagNodeItem.class));
      }
    });
    @SuppressWarnings("null")
    Class<? extends INodeItem> result
        = ExpressionUtils.analyzeStaticResultType(baseType, List.of(basicFlagExpr1, basicFlagExpr2));
    assertEquals(IFlagNodeItem.class, result);
  }

  @Test
  void testFlagAndAssembly() {
    final Class<INodeItem> baseType = INodeItem.class;
    basicFlagExpr1 = context.mock(IExpression.class, "basicFlagExpr1");
    basicAssemblyExpr = context.mock(IExpression.class, "basicAssemblyExpr");

    context.checking(new Expectations() {
      { // NOPMD - intentional
        allowing(basicFlagExpr1).getStaticResultType();
        will(returnValue(IFlagNodeItem.class));
        allowing(basicAssemblyExpr).getStaticResultType();
        will(returnValue(IAssemblyNodeItem.class));
      }
    });
    @SuppressWarnings("null")
    Class<? extends INodeItem> result
        = ExpressionUtils.analyzeStaticResultType(baseType, List.of(basicFlagExpr1, basicAssemblyExpr));
    assertEquals(IDefinitionNodeItem.class, result);
  }

  @Test
  void testFieldAndAssembly() {
    final Class<INodeItem> baseType = INodeItem.class;
    basicFieldExpr = context.mock(IExpression.class, "basicFieldExpr");
    basicAssemblyExpr = context.mock(IExpression.class, "basicAssemblyExpr");

    context.checking(new Expectations() {
      { // NOPMD - intentional
        allowing(basicFieldExpr).getStaticResultType();
        will(returnValue(IFieldNodeItem.class));
        allowing(basicAssemblyExpr).getStaticResultType();
        will(returnValue(IAssemblyNodeItem.class));
      }
    });
    @SuppressWarnings("null")
    Class<? extends INodeItem> result
        = ExpressionUtils.analyzeStaticResultType(baseType, List.of(basicFieldExpr, basicAssemblyExpr));
    assertEquals(IModelNodeItem.class, result);
  }
}
