/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.InvalidTreatTypeDynamicMetapathException;
import gov.nist.secauto.metaschema.core.metapath.node.IDocumentNodeItem;
import gov.nist.secauto.metaschema.core.metapath.node.INodeItem;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.testing.node.MockNodeItemFactory;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import org.junit.jupiter.api.Test;

import java.net.URI;

class RootSlashOnlyTest
    extends ExpressionTestBase {

  @Test
  void testRootSlashOnlyPathUsingDocument() {
    IDocumentNodeItem item = new MockNodeItemFactory().document(
        URI.create("https://example.com/resource"),
        IEnhancedQName.of("https://example.com/ns", "root"),
        CollectionUtil.emptyList(),
        CollectionUtil.emptyList());
    assert item != null;

    RootSlashOnlyPath expr = new RootSlashOnlyPath();

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

    assert item != null;

    RootSlashOnlyPath expr = new RootSlashOnlyPath();

    DynamicContext dynamicContext = newDynamicContext();
    assertThrows(InvalidTreatTypeDynamicMetapathException.class, () -> {
      expr.accept(dynamicContext, ISequence.of(item))
          // ensure the stream is processed
          .safeStream();
    });
  }
}
