/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model.constraint;

import dev.metaschema.core.datatype.IDataTypeAdapter;
import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.MetapathException;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.metapath.item.node.INodeItem;
import dev.metaschema.core.model.constraint.IConstraint.Level;
import dev.metaschema.core.model.validation.IValidationFinding.Kind;
import dev.metaschema.core.model.validation.IValidationResult;
import dev.metaschema.core.util.ObjectUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A validation result handler that collects the resulting findings for later
 * retrieval using the {@link #getFindings()} method.
 * <p>
 * This class is thread-safe and can be used with parallel constraint
 * validation.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class FindingCollectingConstraintValidationHandler
    extends AbstractConstraintValidationHandler
    implements IValidationResult {
  private static final Logger LOGGER = LogManager.getLogger(FindingCollectingConstraintValidationHandler.class);
  @NonNull
  private final Queue<ConstraintValidationFinding> findings = new ConcurrentLinkedQueue<>();
  @NonNull
  private final AtomicReference<Level> highestLevel = new AtomicReference<>(IConstraint.Level.INFORMATIONAL);

  @Override
  @NonNull
  public List<ConstraintValidationFinding> getFindings() {
    // Sort by document location for consistent CLI output
    return findings.stream()
        .sorted(Comparator.comparing(f -> f.getTarget().getMetapath()))
        .collect(Collectors.toUnmodifiableList());
  }

  @Override
  @NonNull
  public Level getHighestSeverity() {
    return highestLevel.get();
  }

  /**
   * Add a finding to the collection of findings maintained by this instance.
   *
   * @param finding
   *          the finding to add
   */
  protected void addFinding(@NonNull ConstraintValidationFinding finding) {
    findings.add(finding);

    Level severity = finding.getSeverity();
    highestLevel.updateAndGet(current -> severity.ordinal() > current.ordinal() ? severity : current);
  }

  @NonNull
  private static Kind toKind(@NonNull Level level) {
    Kind retval = null;
    switch (level) {
    case CRITICAL:
    case ERROR:
      retval = Kind.FAIL;
      break;
    case INFORMATIONAL:
    case DEBUG:
    case NONE:
      retval = Kind.INFORMATIONAL;
      break;
    case WARNING:
      retval = Kind.PASS;
      break;
    }
    assert retval != null : String.format("Unsupported level '%s'.", level);

    return retval;
  }

  @Override
  public void handleCardinalityMinimumViolation(
      @NonNull ICardinalityConstraint constraint,
      @NonNull INodeItem target,
      @NonNull ISequence<? extends INodeItem> testedItems,
      @NonNull DynamicContext dynamicContext) throws ConstraintValidationException {
    addFinding(ConstraintValidationFinding.builder(constraint, target)
        .severity(constraint.getLevel())
        .kind(toKind(constraint.getLevel()))
        .target(target)
        .subjects(testedItems)
        .message(newCardinalityMinimumViolationMessage(constraint, target, testedItems, dynamicContext))
        .build());
  }

  @Override
  public void handleCardinalityMaximumViolation(
      @NonNull ICardinalityConstraint constraint,
      @NonNull INodeItem target,
      @NonNull ISequence<? extends INodeItem> testedItems,
      @NonNull DynamicContext dynamicContext) throws ConstraintValidationException {
    addFinding(ConstraintValidationFinding.builder(constraint, target)
        .severity(constraint.getLevel())
        .kind(toKind(constraint.getLevel()))
        .target(target)
        .subjects(testedItems)
        .message(newCardinalityMaximumViolationMessage(constraint, target, testedItems, dynamicContext))
        .build());
  }

  @Override
  public void handleIndexDuplicateKeyViolation(
      @NonNull IIndexConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem oldItem,
      @NonNull INodeItem target,
      @NonNull DynamicContext dynamicContext) throws ConstraintValidationException {
    addFinding(ConstraintValidationFinding.builder(constraint, node)
        .severity(constraint.getLevel())
        .kind(toKind(constraint.getLevel()))
        .target(target)
        .message(newIndexDuplicateKeyViolationMessage(constraint, node, oldItem, target, dynamicContext))
        .build());
  }

  @Override
  public void handleUniqueKeyViolation(
      @NonNull IUniqueConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem oldItem,
      @NonNull INodeItem target,
      @NonNull DynamicContext dynamicContext) throws ConstraintValidationException {
    addFinding(ConstraintValidationFinding.builder(constraint, node)
        .severity(constraint.getLevel())
        .kind(toKind(constraint.getLevel()))
        .target(target)
        .message(newUniqueKeyViolationMessage(constraint, node, oldItem, target, dynamicContext))
        .build());
  }

  @SuppressWarnings("null")
  @Override
  public void handleKeyMatchError(
      @NonNull IKeyConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem target,
      @NonNull MetapathException cause,
      @NonNull DynamicContext dynamicContext) {
    addFinding(ConstraintValidationFinding.builder(constraint, node)
        .severity(constraint.getLevel())
        .kind(toKind(constraint.getLevel()))
        .target(target)
        .message(cause.getLocalizedMessage())
        .cause(cause)
        .build());
  }

  @Override
  public void handleMatchPatternViolation(
      @NonNull IMatchesConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem target,
      @NonNull String value,
      @NonNull Pattern pattern,
      @NonNull DynamicContext dynamicContext) throws ConstraintValidationException {
    addFinding(ConstraintValidationFinding.builder(constraint, node)
        .severity(constraint.getLevel())
        .kind(toKind(constraint.getLevel()))
        .target(target)
        .message(newMatchPatternViolationMessage(constraint, node, target, value, pattern, dynamicContext))
        .build());
  }

  @Override
  public void handleMatchDatatypeViolation(
      @NonNull IMatchesConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem target,
      @NonNull String value,
      @NonNull IDataTypeAdapter<?> adapter,
      @NonNull IllegalArgumentException cause,
      @NonNull DynamicContext dynamicContext) throws ConstraintValidationException {
    addFinding(ConstraintValidationFinding.builder(constraint, node)
        .severity(constraint.getLevel())
        .kind(toKind(constraint.getLevel()))
        .target(target)
        .message(newMatchDatatypeViolationMessage(constraint, node, target, value, adapter, dynamicContext))
        .cause(cause)
        .build());
  }

  @Override
  public void handleExpectViolation(
      @NonNull IExpectConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem target,
      @NonNull DynamicContext dynamicContext) throws ConstraintValidationException {
    addFinding(ConstraintValidationFinding.builder(constraint, node)
        .severity(constraint.getLevel())
        .kind(toKind(constraint.getLevel()))
        .target(target)
        .message(newExpectViolationMessage(constraint, node, target, dynamicContext))
        .build());
  }

  @Override
  public void handleReportViolation(
      @NonNull IReportConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem target,
      @NonNull DynamicContext dynamicContext) throws ConstraintValidationException {
    addFinding(ConstraintValidationFinding.builder(constraint, node)
        .severity(constraint.getLevel())
        .kind(toKind(constraint.getLevel()))
        .target(target)
        .message(newReportViolationMessage(constraint, node, target, dynamicContext))
        .build());
  }

  @Override
  public void handleAllowedValuesViolation(
      @NonNull List<IAllowedValuesConstraint> failedConstraints,
      @NonNull INodeItem target,
      @NonNull DynamicContext dynamicContext) {
    Level maxLevel = ObjectUtils.notNull(failedConstraints.stream()
        .map(IAllowedValuesConstraint::getLevel)
        .reduce(Level.NONE, (l1, l2) -> l1.ordinal() >= l2.ordinal() ? l1 : l2));

    addFinding(ConstraintValidationFinding.builder(failedConstraints, target)
        .severity(maxLevel)
        .kind(toKind(maxLevel))
        .target(target)
        .message(newAllowedValuesViolationMessage(failedConstraints, target))
        .build());
  }

  @Override
  public void handleIndexDuplicateViolation(
      IIndexConstraint constraint,
      INodeItem node,
      @NonNull DynamicContext dynamicContext) {
    addFinding(ConstraintValidationFinding.builder(constraint, node)
        .kind(Kind.FAIL)
        .severity(Level.CRITICAL)
        .target(node)
        .message(newIndexDuplicateViolationMessage(constraint, node))
        .build());
  }

  @Override
  public void handleIndexMiss(
      @NonNull IIndexHasKeyConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem target,
      @NonNull List<String> key,
      @NonNull DynamicContext dynamicContext) throws ConstraintValidationException {
    addFinding(ConstraintValidationFinding.builder(constraint, node)
        .severity(constraint.getLevel())
        .kind(toKind(constraint.getLevel()))
        .target(target)
        .message(newIndexMissMessage(constraint, node, target, key, dynamicContext))
        .build());
  }

  @Override
  public void handleMissingIndexViolation(
      @NonNull IIndexHasKeyConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem target,
      @NonNull String message,
      @NonNull DynamicContext dynamicContext) {
    addFinding(ConstraintValidationFinding.builder(constraint, node)
        .severity(constraint.getLevel())
        .kind(toKind(constraint.getLevel()))
        .target(target)
        .message(newMissingIndexViolationMessage(constraint, node, target, message, dynamicContext))
        .build());
  }

  @Override
  public void handlePass(
      @NonNull IConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem target,
      @NonNull DynamicContext dynamicContext) {
    addFinding(ConstraintValidationFinding.builder(constraint, node)
        .kind(Kind.PASS)
        .severity(Level.NONE)
        .target(target)
        .build());
  }

  @Override
  public void handleError(
      @NonNull IConstraint constraint,
      @NonNull INodeItem node,
      @NonNull String message,
      @NonNull Throwable exception,
      @NonNull DynamicContext dynamicContext) {
    LOGGER.atError().withThrowable(exception).log(message);
    addFinding(ConstraintValidationFinding.builder(constraint, node)
        .kind(Kind.FAIL)
        .severity(Level.CRITICAL)
        .target(node)
        .message(message)
        .cause(exception)
        .build());
  }
}
