/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.cst.path;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.ExpressionTestBase;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.metapath.item.node.IFlagNodeItem;
import dev.metaschema.core.metapath.item.node.IModelNodeItem;
import dev.metaschema.core.metapath.item.node.NodeItemKind;
import dev.metaschema.core.model.IFlagInstance;
import dev.metaschema.core.qname.IEnhancedQName;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.jupiter.api.Test;

import edu.umd.cs.findbugs.annotations.NonNull;

class FlagStepTest
    extends ExpressionTestBase {
  @Test
  void testFlagWithName() {
    DynamicContext dynamicContext = newDynamicContext();

    Mockery context = getContext();

    @SuppressWarnings("null")
    @NonNull
    IModelNodeItem<?, ?> focusItem = context.mock(IModelNodeItem.class);

    IFlagInstance instance = context.mock(IFlagInstance.class);
    IFlagNodeItem flagNode = context.mock(IFlagNodeItem.class);

    IEnhancedQName flagName = IEnhancedQName.of("test");

    context.checking(new Expectations() {
      { // NOPMD - intentional
        allowing(focusItem).getNodeItem();
        will(returnValue(focusItem));
        allowing(focusItem).getNodeItemKind();
        will(returnValue(NodeItemKind.ASSEMBLY));
        allowing(focusItem).getFlagByName(flagName);
        will(returnValue(flagNode));

        allowing(flagNode).getInstance();
        will(returnValue(instance));

        allowing(instance).getEffectiveName();
        will(returnValue(flagName));

      }
    });

    FlagStep expr = new FlagStep("test data", new NameNodeTest("test data", flagName));

    ISequence<?> result = expr.accept(dynamicContext, ISequence.of(focusItem));
    assertEquals(ISequence.of(flagNode), result, "Sequence does not match");
  }
}
