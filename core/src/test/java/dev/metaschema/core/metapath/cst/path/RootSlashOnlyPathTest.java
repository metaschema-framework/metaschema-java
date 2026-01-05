/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.cst.path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.ExpressionTestBase;
import dev.metaschema.core.metapath.InvalidTreatTypeDynamicMetapathException;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.metapath.item.node.IDocumentNodeItem;
import dev.metaschema.core.metapath.item.node.INodeItem;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.testsupport.mocking.MockNodeItemFactory;
import dev.metaschema.core.util.CollectionUtil;

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
    assertThrows(InvalidTreatTypeDynamicMetapathException.class, () -> {
      expr.accept(dynamicContext, ISequence.of(item))
          // ensure the stream is processed
          .safeStream();
    });
  }
}
