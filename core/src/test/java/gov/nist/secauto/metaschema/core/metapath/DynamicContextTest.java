/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDocumentNodeItem;
import gov.nist.secauto.metaschema.core.model.IUriResolver;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

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

  @Test
  void testConcurrentDocumentLoadingLoadsOnlyOnce() throws Exception {
    // Given: A document loader that tracks load count
    AtomicInteger loadCount = new AtomicInteger(0);
    IDocumentNodeItem mockDocument = ObjectUtils.notNull(mock(IDocumentNodeItem.class));
    URI testUri = ObjectUtils.notNull(URI.create("https://example.com/test.xml"));

    IDocumentLoader countingLoader = new IDocumentLoader() {
      @Override
      public void setUriResolver(@NonNull IUriResolver resolver) {
        // no-op
      }

      @Override
      public IUriResolver getUriResolver() {
        return null;
      }

      @Override
      @NonNull
      public IDocumentNodeItem loadAsNodeItem(@NonNull URI uri) throws IOException {
        loadCount.incrementAndGet();
        // Simulate slow network loading
        try {
          Thread.sleep(50);
        } catch (@SuppressWarnings("unused") InterruptedException ex) {
          Thread.currentThread().interrupt();
        }
        return mockDocument;
      }
    };

    DynamicContext context = new DynamicContext();
    context.setDocumentLoader(countingLoader);

    // When: Multiple threads try to load the same document concurrently
    int threadCount = 5;
    CountDownLatch startLatch = new CountDownLatch(1);
    CountDownLatch doneLatch = new CountDownLatch(threadCount);
    IDocumentNodeItem[] results = new IDocumentNodeItem[threadCount];
    AtomicReference<Exception> threadException = new AtomicReference<>();

    for (int i = 0; i < threadCount; i++) {
      final int index = i;
      new Thread(() -> {
        try {
          startLatch.await(); // Wait for all threads to be ready
          results[index] = context.getDocumentLoader().loadAsNodeItem(testUri);
        } catch (Exception e) {
          threadException.compareAndSet(null, e);
        } finally {
          doneLatch.countDown();
        }
      }).start();
    }

    startLatch.countDown(); // Start all threads at once
    doneLatch.await(); // Wait for all to complete

    // Rethrow any exception from threads to fail the test with clear diagnostics
    if (threadException.get() != null) {
      throw new AssertionError("Thread threw exception during concurrent loading", threadException.get());
    }

    // Then: The document should only be loaded once
    assertEquals(1, loadCount.get(),
        "Document should only be loaded once when multiple threads request concurrently");

    // And: All threads should get the same document instance
    for (IDocumentNodeItem result : results) {
      assertSame(mockDocument, result, "All threads should receive the same document instance");
    }
  }

  @Test
  void testCachingLoaderNormalizesEquivalentUris() throws Exception {
    // Given: A document loader that tracks load count
    AtomicInteger loadCount = new AtomicInteger(0);
    IDocumentNodeItem mockDocument = ObjectUtils.notNull(mock(IDocumentNodeItem.class));

    IDocumentLoader countingLoader = new IDocumentLoader() {
      @Override
      public void setUriResolver(@NonNull IUriResolver resolver) {
        // no-op
      }

      @Override
      public IUriResolver getUriResolver() {
        return null;
      }

      @Override
      @NonNull

      public IDocumentNodeItem loadAsNodeItem(@NonNull URI uri) throws IOException {
        loadCount.incrementAndGet();
        return mockDocument;
      }
    };

    DynamicContext context = new DynamicContext();
    context.setDocumentLoader(countingLoader);

    // When: Loading document using equivalent but non-identical URIs
    URI normalUri = ObjectUtils.notNull(URI.create("https://example.com/a/b/document.xml"));
    URI uriWithDotSegments = ObjectUtils.notNull(URI.create("https://example.com/a/b/./document.xml"));
    URI uriWithDotDotSegments = ObjectUtils.notNull(URI.create("https://example.com/a/b/c/../document.xml"));

    IDocumentNodeItem result1 = context.getDocumentLoader().loadAsNodeItem(normalUri);
    IDocumentNodeItem result2 = context.getDocumentLoader().loadAsNodeItem(uriWithDotSegments);
    IDocumentNodeItem result3 = context.getDocumentLoader().loadAsNodeItem(uriWithDotDotSegments);

    // Then: Document should only be loaded once despite different URI forms
    assertEquals(1, loadCount.get(),
        "Document should only be loaded once for equivalent URIs");

    // And: All requests should return the same cached instance
    assertSame(result1, result2, "Same document should be returned for URI with . segments");
    assertSame(result1, result3, "Same document should be returned for URI with .. segments");
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
      return CollectionUtil.emptyList();
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
