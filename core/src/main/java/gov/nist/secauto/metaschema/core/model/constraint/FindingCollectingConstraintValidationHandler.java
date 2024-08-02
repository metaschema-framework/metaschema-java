/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.MetapathException;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraint.Level;
import gov.nist.secauto.metaschema.core.model.validation.IValidationFinding.Kind;
import gov.nist.secauto.metaschema.core.model.validation.IValidationResult;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import edu.umd.cs.findbugs.annotations.NonNull;

public class FindingCollectingConstraintValidationHandler
    extends AbstractConstraintValidationHandler
    implements IValidationResult {
  @NonNull
  private final List<ConstraintValidationFinding> findings = new LinkedList<>();
  @NonNull
  private Level highestLevel = IConstraint.Level.INFORMATIONAL;

  @Override
  @NonNull
  public List<ConstraintValidationFinding> getFindings() {
    return CollectionUtil.unmodifiableList(findings);
  }

  @Override
  @NonNull
  public Level getHighestSeverity() {
    return highestLevel;
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
    if (severity.ordinal() > highestLevel.ordinal()) {
      highestLevel = severity;
    }
  }

  @NonNull
  private static Kind toKind(@NonNull Level level) {
    Kind retval;
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
    default:
      throw new IllegalArgumentException(String.format("Unsupported level '%s'.", level));
    }

    return retval;
  }

  @Override
  public void handleCardinalityMinimumViolation(
      @NonNull ICardinalityConstraint constraint,
      @NonNull INodeItem node,
      @NonNull ISequence<? extends INodeItem> targets) {
    addFinding(ConstraintValidationFinding.builder(constraint, node)
        .severity(constraint.getLevel())
        .kind(toKind(constraint.getLevel()))
        .target(node)
        .subjects(targets.getValue())
        .message(newCardinalityMinimumViolationMessage(constraint, node, targets))
        .build());
  }

  @Override
  public void handleCardinalityMaximumViolation(
      @NonNull ICardinalityConstraint constraint,
      @NonNull INodeItem node,
      @NonNull ISequence<? extends INodeItem> targets) {
    addFinding(ConstraintValidationFinding.builder(constraint, node)
        .severity(constraint.getLevel())
        .kind(toKind(constraint.getLevel()))
        .target(node)
        .subjects(targets.getValue())
        .message(newCardinalityMaximumViolationMessage(constraint, node, targets))
        .build());
  }

  @Override
  public void handleIndexDuplicateKeyViolation(
      @NonNull IIndexConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem oldItem,
      @NonNull INodeItem target) {
    addFinding(ConstraintValidationFinding.builder(constraint, node)
        .severity(constraint.getLevel())
        .kind(toKind(constraint.getLevel()))
        .target(target)
        .message(newIndexDuplicateKeyViolationMessage(constraint, node, oldItem, target))
        .build());
  }

  @Override
  public void handleUniqueKeyViolation(
      @NonNull IUniqueConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem oldItem,
      @NonNull INodeItem target) {
    addFinding(ConstraintValidationFinding.builder(constraint, node)
        .severity(constraint.getLevel())
        .kind(toKind(constraint.getLevel()))
        .target(target)
        .message(newUniqueKeyViolationMessage(constraint, node, oldItem, target))
        .build());
  }

  @SuppressWarnings("null")
  @Override
  public void handleKeyMatchError(
      @NonNull IKeyConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem target,
      @NonNull MetapathException cause) {
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
      @NonNull Pattern pattern) {
    addFinding(ConstraintValidationFinding.builder(constraint, node)
        .severity(constraint.getLevel())
        .kind(toKind(constraint.getLevel()))
        .target(target)
        .message(newMatchPatternViolationMessage(constraint, node, target, value, pattern))
        .build());
  }

  @Override
  public void handleMatchDatatypeViolation(
      @NonNull IMatchesConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem target,
      @NonNull String value,
      @NonNull IDataTypeAdapter<?> adapter,
      @NonNull IllegalArgumentException cause) {
    addFinding(ConstraintValidationFinding.builder(constraint, node)
        .severity(constraint.getLevel())
        .kind(toKind(constraint.getLevel()))
        .target(target)
        .message(newMatchDatatypeViolationMessage(constraint, node, target, value, adapter))
        .cause(cause)
        .build());
  }

  @Override
  public void handleExpectViolation(
      @NonNull IExpectConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem target,
      @NonNull DynamicContext dynamicContext) {
    addFinding(ConstraintValidationFinding.builder(constraint, node)
        .severity(constraint.getLevel())
        .kind(toKind(constraint.getLevel()))
        .target(target)
        .message(newExpectViolationMessage(constraint, node, target, dynamicContext))
        .build());
  }

  @Override
  public void handleAllowedValuesViolation(
      @NonNull List<IAllowedValuesConstraint> failedConstraints,
      @NonNull INodeItem target) {
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
  public void handleIndexDuplicateViolation(IIndexConstraint constraint, INodeItem node) {
    addFinding(ConstraintValidationFinding.builder(constraint, node)
        .kind(Kind.FAIL)
        .target(node)
        .message(newIndexDuplicateViolationMessage(constraint, node))
        .severity(Level.CRITICAL)
        .build());
  }

  @Override
  public void handleIndexMiss(IIndexHasKeyConstraint constraint, INodeItem node, INodeItem target, List<String> key) {
    addFinding(ConstraintValidationFinding.builder(constraint, node)
        .severity(constraint.getLevel())
        .kind(toKind(constraint.getLevel()))
        .target(target)
        .message(newIndexMissMessage(constraint, node, target, key))
        .build());
  }

  @Override
  public void handleMissingIndexViolation(
      IIndexHasKeyConstraint constraint,
      INodeItem node,
      INodeItem target,
      String message) {
    addFinding(ConstraintValidationFinding.builder(constraint, node)
        .severity(constraint.getLevel())
        .kind(toKind(constraint.getLevel()))
        .target(target)
        .message(newMissingIndexViolationMessage(constraint, node, target, message))
        .build());
  }

  @Override
  public void handlePass(IConstraint constraint, INodeItem node, INodeItem target) {
    addFinding(ConstraintValidationFinding.builder(constraint, node)
        .target(target)
        .severity(Level.NONE)
        .kind(Kind.PASS)
        .build());
  }
}
