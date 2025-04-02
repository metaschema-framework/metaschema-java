/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.DynamicMetapathException;
import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDocumentNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.testing.model.mocking.MockNodeItemFactory;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import org.junit.jupiter.api.Test;

import java.net.URI;

class RootSlashOnlyPathTest
    extends ExpressionTestBase {

  @Test
  void testRootSlashOnlyPathUsingDocument() {
    IDocumentNodeItem item = new MockNodeItemFactory().document(
        URI.create("https://example.com/resource"),
        IEnhancedQName.of("https://example.com/ns", "root"),
        CollectionUtil.emptyList(),
        CollectionUtil.emptyList());
    assert item != null;

    RootSlashOnlyPath expr = new RootSlashOnlyPath("test data");

    DynamicContext dynamicContext = newDynamicContext();
    ISequence<?> result = expr.accept(dynamicContext, ISequence.of(item));
    assertEquals(ISequence.of(item), result);
  }

  @Test
  void testRootSlashOnlyPathUsingNonDocument() {
    INodeItem item = new MockNodeItemFactory().assembly(
        IEnhancedQName.of("https://example.com/ns", "non-root"),
        CollectionUtil.emptyList(),
        CollectionUtil.emptyList());
    // ensure the correct position is provided
    doReturn(1).when(item).getPosition();

    RootSlashOnlyPath expr = new RootSlashOnlyPath("test data");

    DynamicContext dynamicContext = newDynamicContext();
    DynamicMetapathException thrown = assertThrows(DynamicMetapathException.class, () -> {
      ISequence<?> result = expr.accept(dynamicContext, ISequence.of(item));
      assertEquals(ISequence.of(item), result);
    });

    assertEquals(DynamicMetapathException.TREAT_DOES_NOT_MATCH_TYPE, thrown.getCode());
  }
}
