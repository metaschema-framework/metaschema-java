/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.DynamicMetapathException;
import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDocumentNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;

import org.junit.jupiter.api.Test;

class RootSlashOnlyPathTest
    extends ExpressionTestBase {

  @Test
  void testRootSlashOnlyPathUsingDocument() {
    IDocumentNodeItem nodeContext = newDocumentNodeMock();
    assert nodeContext != null;

    RootSlashOnlyPath expr = new RootSlashOnlyPath("test data");

    DynamicContext dynamicContext = newDynamicContext();
    ISequence<?> result = expr.accept(dynamicContext, ISequence.of(nodeContext));
    assertEquals(ISequence.of(nodeContext), result);
  }

  @Test
  void testRootSlashOnlyPathUsingNonDocument() {
    INodeItem item = newNonDocumentNodeMock("non-document");
    assert item != null;

    RootSlashOnlyPath expr = new RootSlashOnlyPath("test data");

    DynamicContext dynamicContext = newDynamicContext();
    DynamicMetapathException thrown = assertThrows(DynamicMetapathException.class, () -> {
      ISequence<?> result = expr.accept(dynamicContext, ISequence.of(item));
      assertEquals(ISequence.of(item), result);
    });

    assertEquals(DynamicMetapathException.TREAT_DOES_NOT_MATCH_TYPE, thrown.getCode());
  }
}
