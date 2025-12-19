/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Configuration for parallel constraint validation.
 * <p>
 * This class supports two modes:
 * <ul>
 * <li>Internal thread pool: Created via {@link #withThreads(int)}, shut down by
 * {@link #close()}. Uses {@link ForkJoinPool} internally to handle nested
 * parallelism without deadlock.</li>
 * <li>External executor: Provided via {@link #withExecutor(ExecutorService)},
 * NOT shut down by {@link #close()}</li>
 * </ul>
 * <p>
 * Instances should be used with try-with-resources or explicitly closed after
 * validation.
 */
public final class ParallelValidationConfig implements AutoCloseable {

  /**
   * Single-threaded sequential execution (default, current behavior).
   * <p>
   * This instance does not need to be closed.
   */
  @NonNull
  public static final ParallelValidationConfig SEQUENTIAL = new ParallelValidationConfig(null, 1, false);

  /**
   * The executor service, lazily initialized if using internal pool.
   * <p>
   * Volatile is required for thread-safe lazy initialization.
   */
  @SuppressWarnings("PMD.AvoidUsingVolatile") // Required for thread-safe lazy initialization
  @Nullable
  private volatile ExecutorService executor;
  private final int threadCount;
  private final boolean ownsExecutor;

  private ParallelValidationConfig(@Nullable ExecutorService executor, int threadCount, boolean ownsExecutor) {
    this.executor = executor;
    this.threadCount = threadCount;
    this.ownsExecutor = ownsExecutor;
  }

  /**
   * Create configuration using an application-provided executor.
   * <p>
   * The executor is NOT shut down by {@link #close()}; the caller retains
   * ownership.
   *
   * @param executor
   *          the executor service to use for parallel tasks
   * @return configuration using the provided executor
   * @throws NullPointerException
   *           if executor is null
   */
  @NonNull
  public static ParallelValidationConfig withExecutor(@NonNull ExecutorService executor) {
    Objects.requireNonNull(executor, "executor must not be null");
    return new ParallelValidationConfig(executor, 0, false);
  }

  /**
   * Create configuration that creates an internal thread pool.
   * <p>
   * The internal pool is shut down when {@link #close()} is called.
   *
   * @param threadCount
   *          number of threads (must be &gt;= 1)
   * @return configuration with internal thread pool
   * @throws IllegalArgumentException
   *           if threadCount &lt; 1
   */
  @NonNull
  public static ParallelValidationConfig withThreads(int threadCount) {
    if (threadCount < 1) {
      throw new IllegalArgumentException("threadCount must be at least 1, got: " + threadCount);
    }
    if (threadCount == 1) {
      return SEQUENTIAL;
    }
    return new ParallelValidationConfig(null, threadCount, true);
  }

  /**
   * Check if parallel execution is enabled.
   *
   * @return true if using more than one thread
   */
  public boolean isParallel() {
    return executor != null || threadCount > 1;
  }

  /**
   * Get the executor service, creating an internal pool if needed.
   * <p>
   * For internal pools, the executor is created lazily on first call.
   *
   * @return the executor service
   * @throws IllegalStateException
   *           if called on SEQUENTIAL config
   */
  @SuppressWarnings("PMD.DoubleCheckedLocking") // Correct with volatile field
  @NonNull
  public ExecutorService getExecutor() {
    if (!isParallel()) {
      throw new IllegalStateException("Cannot get executor for sequential configuration");
    }
    ExecutorService result = executor;
    if (result == null) {
      synchronized (this) {
        result = executor;
        if (result == null) {
          // Use ForkJoinPool to avoid deadlock with nested parallelism.
          // Fixed thread pools deadlock when all threads wait for children.
          result = new ForkJoinPool(threadCount);
          executor = result;
        }
      }
    }
    return Objects.requireNonNull(result, "Executor should not be null after initialization");
  }

  /**
   * Shut down internal executor if one was created.
   * <p>
   * Does nothing if using an external executor or if no executor was created.
   */
  @Override
  public void close() {
    ExecutorService exec = executor;
    if (ownsExecutor && exec != null) {
      exec.shutdown();
      try {
        if (!exec.awaitTermination(60, TimeUnit.SECONDS)) {
          exec.shutdownNow();
        }
      } catch (InterruptedException e) {
        exec.shutdownNow();
        Thread.currentThread().interrupt();
      }
    }
  }
}
