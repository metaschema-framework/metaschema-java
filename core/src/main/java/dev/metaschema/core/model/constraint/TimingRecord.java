/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model.constraint;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Thread-safe accumulator for timing measurements of a single timed entity
 * (constraint, let-statement, phase, or overall validation).
 * <p>
 * Records total elapsed time, invocation count, and min/max per-invocation
 * durations. Also tracks the wall-clock start and end timestamps in UTC.
 * <p>
 * All methods are safe to call from multiple threads concurrently.
 */
public class TimingRecord {
  @NonNull
  private final LongAdder totalTimeNs = new LongAdder();
  @NonNull
  private final LongAdder count = new LongAdder();
  @NonNull
  private final AtomicLong minTimeNs = new AtomicLong(Long.MAX_VALUE);
  @NonNull
  private final AtomicLong maxTimeNs = new AtomicLong(Long.MIN_VALUE);
  @NonNull
  private final AtomicReference<Instant> startTimestampUtc = new AtomicReference<>();
  @NonNull
  private final AtomicReference<Instant> endTimestampUtc = new AtomicReference<>();

  /**
   * Record the start of a timed event. Atomically keeps the earliest start
   * timestamp across all invocations.
   *
   * @param startTimestamp
   *          the wall-clock time when the event began
   */
  void recordStart(@NonNull Instant startTimestamp) {
    startTimestampUtc.accumulateAndGet(
        startTimestamp,
        (prev, next) -> prev == null || next.isBefore(prev) ? next : prev);
  }

  /**
   * Record the completion of a timed event. Atomically keeps the latest end
   * timestamp across all invocations.
   *
   * @param durationNs
   *          the elapsed time of this invocation in nanoseconds
   * @param endTimestamp
   *          the wall-clock time when the event completed
   */
  void recordEnd(long durationNs, @NonNull Instant endTimestamp) {
    totalTimeNs.add(durationNs);
    count.increment();
    endTimestampUtc.accumulateAndGet(
        endTimestamp,
        (prev, next) -> prev == null || next.isAfter(prev) ? next : prev);

    // update min atomically
    long currentMin;
    do {
      currentMin = minTimeNs.get();
      if (durationNs >= currentMin) {
        break;
      }
    } while (!minTimeNs.compareAndSet(currentMin, durationNs));

    // update max atomically
    long currentMax;
    do {
      currentMax = maxTimeNs.get();
      if (durationNs <= currentMax) {
        break;
      }
    } while (!maxTimeNs.compareAndSet(currentMax, durationNs));
  }

  /**
   * Get the total accumulated time across all invocations.
   *
   * @return total time in nanoseconds
   */
  public long getTotalTimeNs() {
    return totalTimeNs.sum();
  }

  /**
   * Get the number of times this entity has been timed.
   *
   * @return the invocation count
   */
  public long getCount() {
    return count.sum();
  }

  /**
   * Get the minimum single-invocation duration recorded.
   *
   * @return minimum time in nanoseconds, or {@link Long#MAX_VALUE} if no
   *         invocations have been recorded
   */
  public long getMinTimeNs() {
    return minTimeNs.get();
  }

  /**
   * Get the maximum single-invocation duration recorded.
   *
   * @return maximum time in nanoseconds, or {@link Long#MIN_VALUE} if no
   *         invocations have been recorded
   */
  public long getMaxTimeNs() {
    return maxTimeNs.get();
  }

  /**
   * Get the wall-clock timestamp of the earliest recorded start event.
   *
   * @return the start timestamp, or {@code null} if no events have been recorded
   */
  @Nullable
  public Instant getStartTimestampUtc() {
    return startTimestampUtc.get();
  }

  /**
   * Get the wall-clock timestamp of the latest recorded end event.
   *
   * @return the end timestamp, or {@code null} if no events have completed
   */
  @Nullable
  public Instant getEndTimestampUtc() {
    return endTimestampUtc.get();
  }
}
