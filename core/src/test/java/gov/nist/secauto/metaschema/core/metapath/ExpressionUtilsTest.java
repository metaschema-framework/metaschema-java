/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.cst.ExpressionUtils;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDefinitionNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFieldNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFlagNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IModelNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.auto.Mock;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

class ExpressionUtilsTest {

  @RegisterExtension
  Mockery context = new JUnit5Mockery();

  @Mock
  private IFlagNodeItem flagNodeItem1; // NOPMD - it's injected
  @Mock
  private IFlagNodeItem flagNodeItem2; // NOPMD - it's injected

  @Mock
  private IExpression basicFlagExpr1; // NOPMD - it's injected
  @Mock
  private IExpression basicFlagExpr2; // NOPMD - it's injected
  @Mock
  private IExpression basicAssemblyExpr; // NOPMD - it's injected
  @Mock
  private IExpression basicFieldExpr; // NOPMD - it's injected

  @Test
  void testTwoFlags() {
    Class<INodeItem> baseType = INodeItem.class;

    context.checking(new Expectations() {
      { // NOPMD - intentional
        allowing(basicFlagExpr1).getStaticResultType();
        will(returnValue(IFlagNodeItem.class));
        allowing(basicFlagExpr2).getStaticResultType();
        will(returnValue(IFlagNodeItem.class));
      }
    });
    @SuppressWarnings("null") Class<? extends INodeItem> result
        = ExpressionUtils.analyzeStaticResultType(baseType, List.of(basicFlagExpr1, basicFlagExpr2));
    assertEquals(IFlagNodeItem.class, result);
  }

  @Test
  void testFlagAndAssembly() {
    Class<INodeItem> baseType = INodeItem.class;

    context.checking(new Expectations() {
      { // NOPMD - intentional
        allowing(basicFlagExpr1).getStaticResultType();
        will(returnValue(IFlagNodeItem.class));
        allowing(basicAssemblyExpr).getStaticResultType();
        will(returnValue(IAssemblyNodeItem.class));
      }
    });
    @SuppressWarnings("null") Class<? extends INodeItem> result
        = ExpressionUtils.analyzeStaticResultType(baseType, List.of(basicFlagExpr1, basicAssemblyExpr));
    assertEquals(IDefinitionNodeItem.class, result);
  }

  @Test
  void testFieldAndAssembly() {
    Class<INodeItem> baseType = INodeItem.class;

    context.checking(new Expectations() {
      { // NOPMD - intentional
        allowing(basicFieldExpr).getStaticResultType();
        will(returnValue(IFieldNodeItem.class));
        allowing(basicAssemblyExpr).getStaticResultType();
        will(returnValue(IAssemblyNodeItem.class));
      }
    });
    @SuppressWarnings("null") Class<? extends INodeItem> result
        = ExpressionUtils.analyzeStaticResultType(baseType, List.of(basicFieldExpr, basicAssemblyExpr));
    assertEquals(IModelNodeItem.class, result);
  }
}
