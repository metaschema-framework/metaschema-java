/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model.constraint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.StaticContext;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.metapath.item.node.IDefinitionNodeItem;
import dev.metaschema.core.metapath.item.node.INodeItem;
import dev.metaschema.core.model.ISource;
import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Tests for thread-safety of DefaultConstraintValidator.
 */
@SuppressWarnings({ "PMD.TooManyStaticImports", "PMD.CouplingBetweenObjects" })
class DefaultConstraintValidatorThreadSafetyTest {

  /**
   * Test that parallel validation produces the same results as sequential
   * validation.
   */
  @Test
  void testParallelValidationBehavioralEquivalence() throws Exception {
    // Run validation with sequential config
    FindingCollectingConstraintValidationHandler sequentialHandler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator sequentialValidator
        = new DefaultConstraintValidator(sequentialHandler, ValidationConfig.SEQUENTIAL)) {
      runValidationOperations(sequentialValidator);
    }
    int sequentialFindingCount = sequentialHandler.getFindings().size();

    // Run validation with parallel config
    FindingCollectingConstraintValidationHandler parallelHandler = new FindingCollectingConstraintValidationHandler();
    try (ValidationConfig config = ValidationConfig.withThreads(4)) {
      assertTrue(config.isParallel(), "Config should be parallel with 4 threads");

      try (DefaultConstraintValidator parallelValidator = new DefaultConstraintValidator(parallelHandler, config)) {
        assertNotNull(parallelValidator);

        // The executor should be created lazily
        java.util.concurrent.ExecutorService executor = config.getExecutor();
        assertNotNull(executor, "Executor should be created");

        runValidationOperations(parallelValidator);
      }
    }
    int parallelFindingCount = parallelHandler.getFindings().size();

    // Both should produce the same number of findings
    assertEquals(sequentialFindingCount, parallelFindingCount,
        "Sequential and parallel validation should produce the same number of findings");
  }

  /**
   * Helper method to run a set of validation operations on a validator.
   *
   * @param validator
   *          the validator to run operations on
   * @throws Exception
   *           if an error occurs during validation
   */
  private void runValidationOperations(@NonNull DefaultConstraintValidator validator) throws Exception {
    // Perform a series of index-has-key validations
    String indexName = "test-index";
    for (int i = 0; i < 10; i++) {
      IIndexHasKeyConstraint constraint = createMockIndexHasKeyConstraint(indexName);
      IDefinitionNodeItem<?, ?> node = createMockDefinitionNodeItem("/item" + i);
      ISequence<? extends INodeItem> targets = createMockSequence();
      invokeValidateIndexHasKey(validator, constraint, node, targets);
    }

    // Perform a series of allowed-values tracking operations
    for (int i = 0; i < 10; i++) {
      INodeItem targetItem = createMockNodeItemWithValue("/value" + i);
      IAllowedValuesConstraint constraint = createMockAllowedValuesConstraint();
      IDefinitionNodeItem<?, ?> node = createMockDefinitionNodeItem("/node" + i);
      try {
        validator.updateValueStatus(targetItem, constraint, node);
      } catch (@SuppressWarnings("unused") ConstraintValidationException ex) {
        // Expected for some constraint combinations
      }
    }
  }

  /**
   * Test that sequential validation still works with new constructor.
   */
  @Test
  void testSequentialValidationBackwardCompatibility() {
    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();

    // Old constructor should still work
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      assertNotNull(validator, "Old constructor should work");
    }

    // New constructor with SEQUENTIAL config should also work
    try (DefaultConstraintValidator validator
        = new DefaultConstraintValidator(handler, ValidationConfig.SEQUENTIAL)) {
      assertNotNull(validator, "New constructor with SEQUENTIAL should work");
    }
  }

  /**
   * Test that concurrent calls to validateIndexHasKey properly accumulate key
   * references without race conditions.
   */
  @Test
  void testConcurrentIndexHasKeyAccumulation() throws Exception {
    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {

      int threadCount = 10;
      int keysPerThread = 50;
      ExecutorService executor = ObjectUtils.notNull(Executors.newFixedThreadPool(threadCount));
      try {
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        ConcurrentLinkedQueue<String> errorMessages = new ConcurrentLinkedQueue<>();

        // All threads will add key references to the same index name
        String indexName = "test-index";

        for (int t = 0; t < threadCount; t++) {
          final int threadId = t;
          executor.submit(() -> {
            try {
              startLatch.await(); // Wait for all threads to be ready
              for (int i = 0; i < keysPerThread; i++) {
                IIndexHasKeyConstraint constraint = createMockIndexHasKeyConstraint(indexName);
                IDefinitionNodeItem<?, ?> node = createMockDefinitionNodeItem("/thread" + threadId + "/item" + i);
                ISequence<? extends INodeItem> targets = createMockSequence();

                // This method adds to indexNameToKeyRefMap
                invokeValidateIndexHasKey(validator, constraint, node, targets);
              }
            } catch (Exception e) {
              StringBuilder sb = new StringBuilder();
              sb.append("Thread ").append(threadId).append(" error: ")
                  .append(e.getClass().getName()).append(": ").append(e.getMessage());
              if (e.getCause() != null) {
                sb.append(" Caused by: ").append(e.getCause().getClass().getName())
                    .append(": ").append(e.getCause().getMessage());
              }
              errorMessages.add(sb.toString());
            } finally {
              doneLatch.countDown();
            }
          });
        }

        startLatch.countDown(); // Start all threads simultaneously
        assertTrue(doneLatch.await(30, TimeUnit.SECONDS), "Threads should complete within timeout");

        assertEquals(0, errorMessages.size(),
            "No errors should occur during concurrent access. Errors: " + errorMessages);
      } finally {
        executor.shutdown();
      }
    }
  }

  /**
   * Test that concurrent updateValueStatus calls properly track allowed values
   * without race conditions.
   */
  @Test
  void testConcurrentAllowedValuesTracking() throws Exception {
    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {

      int threadCount = 8;
      int itemsPerThread = 25;
      ExecutorService executor = ObjectUtils.notNull(Executors.newFixedThreadPool(threadCount));
      try {
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        ConcurrentLinkedQueue<String> errorMessages = new ConcurrentLinkedQueue<>();

        for (int t = 0; t < threadCount; t++) {
          final int threadId = t;
          executor.submit(() -> {
            try {
              startLatch.await();
              for (int i = 0; i < itemsPerThread; i++) {
                INodeItem targetItem = createMockNodeItemWithValue("/thread" + threadId + "/value" + i);
                IAllowedValuesConstraint constraint = createMockAllowedValuesConstraint();
                IDefinitionNodeItem<?, ?> node = createMockDefinitionNodeItem("/thread" + threadId + "/node" + i);

                try {
                  validator.updateValueStatus(targetItem, constraint, node);
                } catch (@SuppressWarnings("unused") ConstraintValidationException ex) {
                  // Expected for some constraint combinations
                }
              }
            } catch (Exception e) {
              StringBuilder sb = new StringBuilder();
              sb.append("Thread ").append(threadId).append(" error: ")
                  .append(e.getClass().getName()).append(": ").append(e.getMessage());
              if (e.getCause() != null) {
                sb.append(" Caused by: ").append(e.getCause().getClass().getName())
                    .append(": ").append(e.getCause().getMessage());
              }
              errorMessages.add(sb.toString());
            } finally {
              doneLatch.countDown();
            }
          });
        }

        startLatch.countDown();
        assertTrue(doneLatch.await(30, TimeUnit.SECONDS), "Threads should complete within timeout");

        assertEquals(0, errorMessages.size(),
            "No errors should occur during concurrent access. Errors: " + errorMessages);
      } finally {
        executor.shutdown();
      }
    }
  }

  // Helper methods

  @NonNull
  private static IIndexHasKeyConstraint createMockIndexHasKeyConstraint(@NonNull String indexName) {
    IIndexHasKeyConstraint constraint = ObjectUtils.notNull(mock(IIndexHasKeyConstraint.class));
    ISource source = mock(ISource.class);
    doReturn(source).when(constraint).getSource();
    doReturn(StaticContext.instance()).when(source).getStaticContext();
    doReturn(indexName).when(constraint).getIndexName();
    doReturn(IConstraint.Level.ERROR).when(constraint).getLevel();
    doReturn(Collections.emptyList()).when(constraint).getKeyFields();
    return constraint;
  }

  @NonNull
  private static IDefinitionNodeItem<?, ?> createMockDefinitionNodeItem(@NonNull String metapath) {
    IDefinitionNodeItem<?, ?> item = ObjectUtils.notNull(mock(IDefinitionNodeItem.class));
    doReturn(metapath).when(item).getMetapath();
    return item;
  }

  @NonNull
  private static INodeItem createMockNodeItemWithValue(@NonNull String metapath) {
    INodeItem item = ObjectUtils.notNull(mock(INodeItem.class));
    doReturn(metapath).when(item).getMetapath();
    doReturn(true).when(item).hasValue();

    // Mock toAtomicItem() to return a proper atomic item
    dev.metaschema.core.metapath.item.atomic.IAnyAtomicItem atomicItem
        = mock(dev.metaschema.core.metapath.item.atomic.IAnyAtomicItem.class);
    doReturn("test-value").when(atomicItem).asString();
    doReturn(atomicItem).when(item).toAtomicItem();

    return item;
  }

  @NonNull
  private static ISequence<? extends INodeItem> createMockSequence() {
    // Return an actual empty sequence instead of a mock
    // This avoids issues with incomplete mock implementations
    return ISequence.empty();
  }

  @NonNull
  private static IAllowedValuesConstraint createMockAllowedValuesConstraint() {
    IAllowedValuesConstraint constraint = ObjectUtils.notNull(mock(IAllowedValuesConstraint.class));
    ISource source = mock(ISource.class);
    doReturn(source).when(constraint).getSource();
    doReturn(StaticContext.instance()).when(source).getStaticContext();
    doReturn(IConstraint.Level.ERROR).when(constraint).getLevel();
    doReturn(IAllowedValuesConstraint.Extensible.EXTERNAL).when(constraint).getExtensible();
    doReturn(true).when(constraint).isAllowedOther();
    IMetapathExpression targetExpr = mock(IMetapathExpression.class);
    doReturn(".").when(targetExpr).getPath();
    doReturn(targetExpr).when(constraint).getTarget();
    return constraint;
  }

  /**
   * Invoke the private validateIndexHasKey method via the public interface. Since
   * validateIndexHasKey is private, we use reflection to test the thread-safety
   * of the underlying data structure operations.
   */
  private static void invokeValidateIndexHasKey(
      @NonNull DefaultConstraintValidator validator,
      @NonNull IIndexHasKeyConstraint constraint,
      @NonNull IDefinitionNodeItem<?, ?> node,
      @NonNull ISequence<? extends INodeItem> targets) throws Exception {
    // Use reflection to access the private method
    java.lang.reflect.Method method = DefaultConstraintValidator.class.getDeclaredMethod(
        "validateIndexHasKey",
        IIndexHasKeyConstraint.class,
        IDefinitionNodeItem.class,
        ISequence.class);
    method.setAccessible(true);
    method.invoke(validator, constraint, node, targets);
  }
}
