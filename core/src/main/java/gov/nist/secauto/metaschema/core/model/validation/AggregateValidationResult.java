/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.validation;

import gov.nist.secauto.metaschema.core.model.constraint.IConstraint.Level;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides the means to aggregate multiple validation result sets into a single
 * result set.
 */
public final class AggregateValidationResult implements IValidationResult {
  @NonNull
  private final List<IValidationFinding> findings;
  @NonNull
  private final Level highestSeverity;

  private AggregateValidationResult(@NonNull List<IValidationFinding> findings, @NonNull Level highestSeverity) {
    this.findings = CollectionUtil.unmodifiableList(findings);
    this.highestSeverity = highestSeverity;
  }

  /**
   * Aggregate multiple provided results into a single result set.
   *
   * @param results
   *          the results to aggregate
   * @return the combined results
   */
  public static IValidationResult aggregate(@NonNull IValidationResult... results) {
    Stream<? extends IValidationFinding> stream = Stream.empty();
    for (IValidationResult result : results) {
      stream = Stream.concat(stream, result.getFindings().stream());
    }
    assert stream != null;
    return aggregate(stream);
  }

  private static IValidationResult aggregate(@NonNull Stream<? extends IValidationFinding> findingStream) {
    AtomicReference<Level> highestSeverity = new AtomicReference<>(Level.INFORMATIONAL);

    List<IValidationFinding> findings = new LinkedList<>();
    findingStream.sequential().forEachOrdered(finding -> {
      findings.add(finding);
      Level severity = finding.getSeverity();
      if (highestSeverity.get().ordinal() < severity.ordinal()) {
        highestSeverity.set(severity);
      }
    });

    return new AggregateValidationResult(findings, ObjectUtils.notNull(highestSeverity.get()));
  }

  @Override
  public Level getHighestSeverity() {
    return highestSeverity;
  }

  @Override
  public List<? extends IValidationFinding> getFindings() {
    return findings;
  }
}
