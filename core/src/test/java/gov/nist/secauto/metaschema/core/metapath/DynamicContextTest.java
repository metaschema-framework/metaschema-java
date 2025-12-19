/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

class DynamicContextTest {

  @Test
  void testSubContextCopiesExecutionStack() {
    DynamicContext parent = new DynamicContext();
    IExpression mockExpr = new MockExpression();

    parent.pushExecutionStack(mockExpr);
    assertEquals(1, parent.getExecutionStack().size());

    DynamicContext child = parent.subContext();

    // Child should have copy of parent's stack
    assertEquals(1, child.getExecutionStack().size());

    // Modifying child stack should not affect parent
    child.popExecutionStack(mockExpr);
    assertEquals(0, child.getExecutionStack().size());
    assertEquals(1, parent.getExecutionStack().size());
  }

  @Test
  void testSubContextExecutionStackIsolation() {
    DynamicContext parent = new DynamicContext();
    DynamicContext child = parent.subContext();

    IExpression mockExpr = new MockExpression();
    child.pushExecutionStack(mockExpr);

    // Parent should not see child's push
    assertEquals(0, parent.getExecutionStack().size());
    assertEquals(1, child.getExecutionStack().size());
  }

  /**
   * Simple mock expression for testing execution stack isolation.
   */
  private static class MockExpression implements IExpression {
    @Override
    public String toCSTString() {
      return "mock";
    }

    @Override
    @NonNull
    public String getPath() {
      return "mock";
    }

    @Override
    @NonNull
    public List<? extends IExpression> getChildren() {
      return Collections.emptyList();
    }

    @Override
    @NonNull
    public ISequence<? extends IItem> accept(
        @NonNull DynamicContext dynamicContext,
        @NonNull ISequence<?> focus) {
      return ISequence.empty();
    }

    @Override
    public <RESULT, CONTEXT> RESULT accept(
        @NonNull IExpressionVisitor<RESULT, CONTEXT> visitor,
        @NonNull CONTEXT context) {
      return null;
    }
  }
}
