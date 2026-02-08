/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model.constraint;

import org.eclipse.jdt.annotation.Owning;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Configuration for constraint validation.
 * <p>
 * This class supports parallel execution and optional event-based
 * instrumentation. Two execution modes are available:
 * <ul>
 * <li>Internal thread pool: Created via {@link #withThreads(int)}, shut down by
 * {@link #close()}. Uses {@link ForkJoinPool} internally to handle nested
 * parallelism without deadlock.</li>
 * <li>External executor: Provided via {@link #withExecutor(ExecutorService)},
 * NOT shut down by {@link #close()}</li>
 * </ul>
 * <p>
 * An optional {@link ValidationEventListener} can be configured via
 * {@link #withListener(ValidationEventListener)} to receive callbacks during
 * validation. By default, a {@link NoOpValidationEventListener} is used,
 * ensuring zero overhead when instrumentation is not needed.
 * <p>
 * Instances should be used with try-with-resources or explicitly closed after
 * validation.
 */
public final class ValidationConfig implements AutoCloseable {

  /**
   * Single-threaded sequential execution (default, current behavior).
   * <p>
   * This instance does not need to be closed.
   */
  @SuppressWarnings("resource")
  @NonNull
  public static final ValidationConfig SEQUENTIAL = new ValidationConfig(null, 1, false,
      NoOpValidationEventListener.INSTANCE);

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
  @NonNull
  private final ValidationEventListener listener;

  private ValidationConfig(
      @Nullable ExecutorService executor,
      int threadCount,
      boolean ownsExecutor,
      @NonNull ValidationEventListener listener) {
    this.executor = executor;
    this.threadCount = threadCount;
    this.ownsExecutor = ownsExecutor;
    this.listener = listener;
  }

  /**
   * Create configuration using an application-provided executor.
   * <p>
   * The executor is NOT shut down by {@link #close()}; the caller retains
   * ownership.
   * <p>
   * The caller owns the returned configuration and is responsible for closing it.
   *
   * @param executor
   *          the executor service to use for parallel tasks
   * @return configuration using the provided executor
   * @throws NullPointerException
   *           if executor is null
   */
  @NonNull
  @Owning
  public static ValidationConfig withExecutor(@NonNull ExecutorService executor) {
    Objects.requireNonNull(executor, "executor must not be null");
    return new ValidationConfig(executor, 0, false, NoOpValidationEventListener.INSTANCE);
  }

  /**
   * Create configuration that creates an internal thread pool.
   * <p>
   * The internal pool is shut down when {@link #close()} is called.
   * <p>
   * The caller owns the returned configuration and is responsible for closing it.
   *
   * @param threadCount
   *          number of threads (must be &gt;= 1)
   * @return configuration with internal thread pool
   * @throws IllegalArgumentException
   *           if threadCount &lt; 1
   */
  @NonNull
  @Owning
  public static ValidationConfig withThreads(int threadCount) {
    if (threadCount < 1) {
      throw new IllegalArgumentException("threadCount must be at least 1, got: " + threadCount);
    }
    if (threadCount == 1) {
      return SEQUENTIAL;
    }
    return new ValidationConfig(null, threadCount, true, NoOpValidationEventListener.INSTANCE);
  }

  /**
   * Create a new configuration with the specified event listener, preserving the
   * current parallel execution settings.
   * <p>
   * If this config owns its executor, the derived config will lazily create its
   * own independent pool to avoid unsafe sharing of owned executors.
   *
   * @param listener
   *          the event listener to use for validation instrumentation
   * @return a new configuration with the given listener
   * @throws NullPointerException
   *           if listener is null
   */
  @NonNull
  @Owning
  public ValidationConfig withListener(@NonNull ValidationEventListener listener) {
    Objects.requireNonNull(listener, "listener must not be null");
    if (this.ownsExecutor) {
      // Don't share owned executor; derived config will lazily create its own
      return new ValidationConfig(null, this.threadCount, true, listener);
    }
    return new ValidationConfig(this.executor, this.threadCount, false, listener);
  }

  /**
   * Create a new configuration that adds an additional event listener, preserving
   * the current parallel execution settings and any existing listener.
   * <p>
   * If the current listener is a {@link NoOpValidationEventListener}, the new
   * listener replaces it directly. Otherwise, a
   * {@link CompositeValidationEventListener} is created to deliver events to both
   * the existing and new listeners.
   *
   * @param additionalListener
   *          the additional event listener to add
   * @return a new configuration with the additional listener
   * @throws NullPointerException
   *           if additionalListener is null
   */
  @NonNull
  @Owning
  public ValidationConfig addListener(@NonNull ValidationEventListener additionalListener) {
    Objects.requireNonNull(additionalListener, "additionalListener must not be null");
    ValidationEventListener current = this.listener;
    if (current instanceof NoOpValidationEventListener) {
      return withListener(additionalListener);
    }
    return withListener(
        new CompositeValidationEventListener(List.of(current, additionalListener)));
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
   * Get the configured validation event listener.
   *
   * @return the event listener, never null
   */
  @NonNull
  public ValidationEventListener getListener() {
    return listener;
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
      } catch (@SuppressWarnings("unused") InterruptedException ex) {
        exec.shutdownNow();
        Thread.currentThread().interrupt();
      }
    }
  }
}
