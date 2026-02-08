/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model.constraint;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import dev.metaschema.core.metapath.item.node.INodeItem;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * A {@link ValidationEventListener} implementation that collects timing
 * measurements for all validation events.
 * <p>
 * Timing data is organized hierarchically:
 * <ul>
 * <li>Overall validation timing</li>
 * <li>Per-phase timing (keyed by {@link ValidationPhase})</li>
 * <li>Per-constraint timing (keyed by
 * {@link IConstraint#getInternalIdentifier()})</li>
 * <li>Per-let-statement timing (keyed by
 * {@link ILet#getInternalIdentifier()})</li>
 * </ul>
 * <p>
 * This class is thread-safe. Each thread maintains its own stack of start times
 * to handle nested events correctly (e.g., a constraint evaluation that
 * triggers let-statement evaluations).
 *
 * @see TimingRecord
 * @see ValidationEventListener
 */
public class TimingCollector implements ValidationEventListener {
  @NonNull
  private final ConcurrentHashMap<ValidationPhase, TimingRecord> phaseTimings = new ConcurrentHashMap<>();
  @NonNull
  private final ConcurrentHashMap<String, TimingRecord> constraintTimings = new ConcurrentHashMap<>();
  @NonNull
  private final ConcurrentHashMap<ILet, TimingRecord> letTimings = new ConcurrentHashMap<>();
  @SuppressWarnings("PMD.AvoidUsingVolatile") // Required for thread-safe publication
  @Nullable
  private volatile TimingRecord validationTiming;

  /**
   * Thread-local stacks of nano-time start values, used to support nested
   * before/after event pairs.
   */
  @NonNull
  private final ThreadLocal<Deque<Long>> startTimeStack = ThreadLocal.withInitial(ArrayDeque::new);

  /**
   * Construct a new, empty timing collector.
   */
  public TimingCollector() {
    // nothing to initialize beyond field defaults
  }

  /**
   * Push a start time onto the current thread's stack.
   */
  private void pushStartTime() {
    startTimeStack.get().push(System.nanoTime());
  }

  /**
   * Pop and return the most recent start time from the current thread's stack.
   *
   * @return the elapsed duration in nanoseconds since the corresponding push
   */
  private long popElapsedNs() {
    Long startNs = startTimeStack.get().poll();
    if (startNs == null) {
      return 0L;
    }
    return System.nanoTime() - startNs;
  }

  @Override
  public void beforeValidation(@NonNull URI document) {
    TimingRecord record = validationTiming;
    if (record == null) {
      record = new TimingRecord();
      validationTiming = record;
    }
    record.recordStart(Instant.now());
    pushStartTime();
  }

  @Override
  public void afterValidation(@NonNull URI document) {
    long elapsed = popElapsedNs();
    TimingRecord record = validationTiming;
    if (record != null) {
      record.recordEnd(elapsed, Instant.now());
    }
  }

  @Override
  public void beforePhase(@NonNull ValidationPhase phase) {
    phaseTimings.computeIfAbsent(phase, k -> new TimingRecord())
        .recordStart(Instant.now());
    pushStartTime();
  }

  @Override
  public void afterPhase(@NonNull ValidationPhase phase) {
    long elapsed = popElapsedNs();
    TimingRecord record = phaseTimings.get(phase);
    if (record != null) {
      record.recordEnd(elapsed, Instant.now());
    }
  }

  @Override
  public void beforeConstraintEvaluation(@NonNull IConstraint constraint, @NonNull INodeItem target) {
    String id = constraint.getInternalIdentifier();
    constraintTimings.computeIfAbsent(id, k -> new TimingRecord())
        .recordStart(Instant.now());
    pushStartTime();
  }

  @Override
  public void afterConstraintEvaluation(@NonNull IConstraint constraint, @NonNull INodeItem target) {
    long elapsed = popElapsedNs();
    String id = constraint.getInternalIdentifier();
    TimingRecord record = constraintTimings.get(id);
    if (record != null) {
      record.recordEnd(elapsed, Instant.now());
    }
  }

  @Override
  public void beforeLetEvaluation(@NonNull ILet let) {
    letTimings.computeIfAbsent(let, k -> new TimingRecord())
        .recordStart(Instant.now());
    pushStartTime();
  }

  @Override
  public void afterLetEvaluation(@NonNull ILet let) {
    long elapsed = popElapsedNs();
    TimingRecord record = letTimings.get(let);
    if (record != null) {
      record.recordEnd(elapsed, Instant.now());
    }
  }

  /**
   * Get the timing record for a specific validation phase.
   *
   * @param phase
   *          the phase to look up
   * @return the timing record, or {@code null} if the phase was not recorded
   */
  @Nullable
  public TimingRecord getPhaseTiming(@NonNull ValidationPhase phase) {
    return phaseTimings.get(phase);
  }

  /**
   * Get all phase timing records.
   *
   * @return an unmodifiable map of phase to timing record
   */
  @NonNull
  public Map<ValidationPhase, TimingRecord> getPhaseTimings() {
    return Collections.unmodifiableMap(phaseTimings);
  }

  /**
   * Get the timing record for a specific constraint by its internal identifier.
   *
   * @param constraintId
   *          the constraint's internal identifier
   * @return the timing record, or {@code null} if the constraint was not recorded
   */
  @Nullable
  public TimingRecord getConstraintTiming(@NonNull String constraintId) {
    return constraintTimings.get(constraintId);
  }

  /**
   * Get all constraint timing records.
   *
   * @return an unmodifiable map of constraint identifier to timing record
   */
  @NonNull
  public Map<String, TimingRecord> getConstraintTimings() {
    return Collections.unmodifiableMap(constraintTimings);
  }

  /**
   * Get the timing record for a specific let-statement.
   *
   * @param let
   *          the let-statement to look up
   * @return the timing record, or {@code null} if the let was not recorded
   */
  @Nullable
  public TimingRecord getLetTiming(@NonNull ILet let) {
    return letTimings.get(let);
  }

  /**
   * Get all let-statement timing records.
   *
   * @return an unmodifiable map of let-statement to timing record
   */
  @NonNull
  public Map<ILet, TimingRecord> getLetTimings() {
    return Collections.unmodifiableMap(letTimings);
  }

  /**
   * Get the overall validation timing record.
   *
   * @return the validation timing record, or {@code null} if validation was not
   *         recorded
   */
  @Nullable
  public TimingRecord getValidationTiming() {
    return validationTiming;
  }
}
