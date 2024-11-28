/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.path;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFlagNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IModelNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.NodeItemKind;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.jupiter.api.Test;

import edu.umd.cs.findbugs.annotations.NonNull;

class FlagTest
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

    Flag expr = new Flag(new NameTest(flagName));

    ISequence<?> result = expr.accept(dynamicContext, ISequence.of(focusItem));
    assertEquals(ISequence.of(flagNode), result, "Sequence does not match");
  }
}
