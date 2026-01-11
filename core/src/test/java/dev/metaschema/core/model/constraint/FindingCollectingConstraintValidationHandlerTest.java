/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model.constraint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.StaticContext;
import dev.metaschema.core.metapath.format.IPathFormatter;
import dev.metaschema.core.metapath.item.node.INodeItem;
import dev.metaschema.core.model.ISource;
import dev.metaschema.core.model.constraint.IConstraint.Level;
import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;

@SuppressWarnings("PMD.TooManyStaticImports")
class FindingCollectingConstraintValidationHandlerTest {

  @Test
  void testConcurrentAddFindings() throws Exception {
    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();

    int threadCount = 10;
    int findingsPerThread = 100;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch latch = new CountDownLatch(threadCount);

    for (int t = 0; t < threadCount; t++) {
      final int threadId = t;
      executor.submit(() -> {
        try {
          for (int i = 0; i < findingsPerThread; i++) {
            // Create mock finding
            addFinding(handler, Level.ERROR, "/root/item" + threadId + "-" + i);
          }
        } finally {
          latch.countDown();
        }
      });
    }

    assertTrue(latch.await(30, TimeUnit.SECONDS), "Threads should complete within timeout");
    executor.shutdown();

    List<ConstraintValidationFinding> findings = handler.getFindings();
    assertEquals(threadCount * findingsPerThread, findings.size(),
        "Should have all findings from all threads");
  }

  @Test
  void testHighestSeverityConcurrentUpdates() throws Exception {
    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();

    ExecutorService executor = Executors.newFixedThreadPool(4);
    CountDownLatch latch = new CountDownLatch(4);

    // Thread 1: Add INFORMATIONAL findings
    executor.submit(() -> {
      try {
        for (int i = 0; i < 100; i++) {
          addFinding(handler, Level.INFORMATIONAL, "/root/info-" + i);
        }
      } finally {
        latch.countDown();
      }
    });

    // Thread 2: Add WARNING findings
    executor.submit(() -> {
      try {
        for (int i = 0; i < 100; i++) {
          addFinding(handler, Level.WARNING, "/root/warn-" + i);
        }
      } finally {
        latch.countDown();
      }
    });

    // Thread 3: Add ERROR findings
    executor.submit(() -> {
      try {
        for (int i = 0; i < 100; i++) {
          addFinding(handler, Level.ERROR, "/root/error-" + i);
        }
      } finally {
        latch.countDown();
      }
    });

    // Thread 4: Add CRITICAL findings
    executor.submit(() -> {
      try {
        for (int i = 0; i < 100; i++) {
          addFinding(handler, Level.CRITICAL, "/root/critical-" + i);
        }
      } finally {
        latch.countDown();
      }
    });

    assertTrue(latch.await(30, TimeUnit.SECONDS), "Threads should complete within timeout");
    executor.shutdown();

    assertEquals(Level.CRITICAL, handler.getHighestSeverity(),
        "Highest severity should be CRITICAL");
    assertEquals(400, handler.getFindings().size(),
        "Should have all findings from all threads");
  }

  @Test
  void testFindingsSortedByMetapath() {
    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();

    // Add findings in random order
    addFinding(handler, Level.ERROR, "/root/zebra");
    addFinding(handler, Level.ERROR, "/root/alpha");
    addFinding(handler, Level.ERROR, "/root/middle");

    List<ConstraintValidationFinding> findings = handler.getFindings();

    assertEquals(3, findings.size(), "Should have 3 findings");
    assertEquals("/root/alpha", findings.get(0).getTarget().getMetapath(),
        "First finding should be /root/alpha");
    assertEquals("/root/middle", findings.get(1).getTarget().getMetapath(),
        "Second finding should be /root/middle");
    assertEquals("/root/zebra", findings.get(2).getTarget().getMetapath(),
        "Third finding should be /root/zebra");
  }

  /**
   * Helper method to add a finding with a specific level and metapath.
   *
   * @param handler
   *          the handler to add the finding to
   * @param level
   *          the severity level
   * @param metapath
   *          the metapath for the target node
   */
  @SuppressWarnings("null")
  private static void addFinding(
      @NonNull FindingCollectingConstraintValidationHandler handler,
      @NonNull Level level,
      @NonNull String metapath) {
    // Create mock constraint
    IExpectConstraint constraint = ObjectUtils.notNull(mock(IExpectConstraint.class));
    ISource source = mock(ISource.class);
    doReturn(source).when(constraint).getSource();
    doReturn(level).when(constraint).getLevel();
    doReturn("test-constraint").when(constraint).getId();
    doReturn("Test violation message").when(constraint).getMessage();
    try {
      doReturn("Test violation message").when(constraint).generateMessage(any(), any());
    } catch (@SuppressWarnings("unused") ConstraintValidationException ex) {
      // Mockito stub doesn't actually call the method
    }
    doReturn(StaticContext.instance()).when(source).getStaticContext();

    // Create mock node item with the specified metapath
    INodeItem node = ObjectUtils.notNull(mock(INodeItem.class));
    doReturn(metapath).when(node).getMetapath();
    doReturn(metapath).when(node).toPath(any(IPathFormatter.class));

    // Create dynamic context
    DynamicContext context = new DynamicContext(StaticContext.instance());

    // Add the finding
    try {
      handler.handleExpectViolation(constraint, node, node, context);
    } catch (ConstraintValidationException e) {
      throw new RuntimeException("Unexpected exception during test", e);
    }
  }
}
