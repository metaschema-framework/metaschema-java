/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model.constraint;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dev.metaschema.core.metapath.item.node.INodeItem;
import dev.metaschema.core.util.ObjectUtils;

class ValidationConfigTest {

  // === Existing behavior (migrated from ParallelValidationConfigTest) ===

  @Test
  void testSequentialIsNotParallel() {
    ValidationConfig config = ValidationConfig.SEQUENTIAL;
    assertFalse(config.isParallel());
  }

  @Test
  void testWithThreadsOneIsNotParallel() {
    try (ValidationConfig config = ValidationConfig.withThreads(1)) {
      assertFalse(config.isParallel());
    }
  }

  @Test
  void testWithThreadsFourIsParallel() {
    try (ValidationConfig config = ValidationConfig.withThreads(4)) {
      assertTrue(config.isParallel());
      config.close();
    }
  }

  @Test
  void testWithThreadsZeroThrows() {
    assertThrows(IllegalArgumentException.class, () -> ValidationConfig.withThreads(0));
  }

  @Test
  void testWithThreadsNegativeThrows() {
    assertThrows(IllegalArgumentException.class, () -> ValidationConfig.withThreads(-1));
  }

  @Test
  void testWithExecutorIsParallel() {
    ExecutorService executor = ObjectUtils.notNull(Executors.newFixedThreadPool(2));
    try (ValidationConfig config = ValidationConfig.withExecutor(executor)) {
      assertTrue(config.isParallel());
    } finally {
      executor.shutdown();
    }
  }

  @SuppressWarnings("null")
  @Test
  void testWithExecutorNullThrows() {
    assertThrows(NullPointerException.class, () -> ValidationConfig.withExecutor(null));
  }

  @Test
  void testCloseShutdownsInternalExecutor() {
    try (ValidationConfig config = ValidationConfig.withThreads(2)) {
      ExecutorService executor = config.getExecutor();
      assertFalse(executor.isShutdown());
      config.close();
      assertTrue(executor.isShutdown());
    }
  }

  @Test
  void testCloseDoesNotShutdownExternalExecutor() {
    ExecutorService executor = ObjectUtils.notNull(Executors.newFixedThreadPool(2));
    try (ValidationConfig config = ValidationConfig.withExecutor(executor)) {
      config.close();
      assertFalse(executor.isShutdown());
    } finally {
      executor.shutdown();
    }
  }

  // === New ValidationEventListener tests ===

  @Test
  void testDefaultListenerIsNoOp() {
    ValidationConfig config = ValidationConfig.SEQUENTIAL;
    ValidationEventListener listener = config.getListener();
    assertNotNull(listener);
    assertInstanceOf(NoOpValidationEventListener.class, listener);
  }

  @Test
  void testWithListenerReturnsConfigWithListener() {
    TestListener listener = new TestListener();
    try (ValidationConfig config = ValidationConfig.withThreads(2).withListener(listener)) {
      assertSame(listener, config.getListener());
    }
  }

  @Test
  void testSequentialWithListenerReturnsNewConfig() {
    TestListener listener = new TestListener();
    try (ValidationConfig config = ValidationConfig.SEQUENTIAL.withListener(listener)) {
      assertSame(listener, config.getListener());
      assertFalse(config.isParallel());
    }
  }

  @SuppressWarnings("null")
  @Test
  void testWithListenerNullThrows() {
    assertThrows(NullPointerException.class,
        () -> ValidationConfig.SEQUENTIAL.withListener(null));
  }

  @Test
  void testWithListenerPreservesParallelConfig() {
    TestListener listener = new TestListener();
    try (ValidationConfig config = ValidationConfig.withThreads(4).withListener(listener)) {
      assertTrue(config.isParallel());
      assertSame(listener, config.getListener());
    }
  }

  // === addListener tests ===

  @Test
  void testAddListenerCreatesComposite() {
    TestListener listener1 = new TestListener();
    TestListener listener2 = new TestListener();
    try (ValidationConfig config = ValidationConfig.SEQUENTIAL
        .withListener(listener1)
        .addListener(listener2)) {
      assertInstanceOf(CompositeValidationEventListener.class, config.getListener());
    }
  }

  @Test
  void testAddListenerToNoOpReplacesDirectly() {
    TestListener listener = new TestListener();
    try (ValidationConfig config = ValidationConfig.SEQUENTIAL.addListener(listener)) {
      assertSame(listener, config.getListener());
    }
  }

  @Test
  void testAddListenerPreservesParallelConfig() {
    TestListener listener1 = new TestListener();
    TestListener listener2 = new TestListener();
    try (ValidationConfig config = ValidationConfig.withThreads(4)
        .withListener(listener1)
        .addListener(listener2)) {
      assertTrue(config.isParallel());
      assertInstanceOf(CompositeValidationEventListener.class, config.getListener());
    }
  }

  /**
   * A simple test listener that tracks whether events were called.
   */
  private static class TestListener implements ValidationEventListener {
    @Override
    public void beforeValidation(URI document) {
      // test stub
    }

    @Override
    public void afterValidation(URI document) {
      // test stub
    }

    @Override
    public void beforePhase(ValidationPhase phase) {
      // test stub
    }

    @Override
    public void afterPhase(ValidationPhase phase) {
      // test stub
    }

    @Override
    public void beforeConstraintEvaluation(IConstraint constraint, INodeItem target) {
      // test stub
    }

    @Override
    public void afterConstraintEvaluation(IConstraint constraint, INodeItem target) {
      // test stub
    }

    @Override
    public void beforeLetEvaluation(ILet let) {
      // test stub
    }

    @Override
    public void afterLetEvaluation(ILet let) {
      // test stub
    }
  }
}
