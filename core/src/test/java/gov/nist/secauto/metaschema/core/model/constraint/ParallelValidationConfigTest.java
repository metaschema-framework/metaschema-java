/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ParallelValidationConfigTest {

  @Test
  void testSequentialIsNotParallel() {
    ParallelValidationConfig config = ParallelValidationConfig.SEQUENTIAL;
    assertFalse(config.isParallel());
  }

  @Test
  void testWithThreadsOneIsNotParallel() {
    try (ParallelValidationConfig config = ParallelValidationConfig.withThreads(1)) {
      assertFalse(config.isParallel());
    }
  }

  @Test
  void testWithThreadsFourIsParallel() {
    try (ParallelValidationConfig config = ParallelValidationConfig.withThreads(4)) {
      assertTrue(config.isParallel());
      config.close();
    }
  }

  @Test
  void testWithThreadsZeroThrows() {
    assertThrows(IllegalArgumentException.class, () -> ParallelValidationConfig.withThreads(0));
  }

  @Test
  void testWithThreadsNegativeThrows() {
    assertThrows(IllegalArgumentException.class, () -> ParallelValidationConfig.withThreads(-1));
  }

  @Test
  void testWithExecutorIsParallel() {
    ExecutorService executor = ObjectUtils.notNull(Executors.newFixedThreadPool(2));
    try (ParallelValidationConfig config = ParallelValidationConfig.withExecutor(executor)) {
      assertTrue(config.isParallel());
    } finally {
      executor.shutdown();
    }
  }

  @SuppressWarnings("null")
  @Test
  void testWithExecutorNullThrows() {
    assertThrows(NullPointerException.class, () -> ParallelValidationConfig.withExecutor(null));
  }

  @Test
  void testCloseShutdownsInternalExecutor() {
    try (ParallelValidationConfig config = ParallelValidationConfig.withThreads(2)) {
      ExecutorService executor = config.getExecutor();
      assertFalse(executor.isShutdown());
      config.close();
      assertTrue(executor.isShutdown());
    }
  }

  @Test
  void testCloseDoesNotShutdownExternalExecutor() {
    ExecutorService executor = ObjectUtils.notNull(Executors.newFixedThreadPool(2));
    try (ParallelValidationConfig config = ParallelValidationConfig.withExecutor(executor)) {
      config.close();
      assertFalse(executor.isShutdown());
    } finally {
      executor.shutdown();
    }
  }
}
