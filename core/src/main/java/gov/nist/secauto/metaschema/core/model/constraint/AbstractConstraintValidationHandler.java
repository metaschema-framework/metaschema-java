/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.format.IPathFormatter;
import gov.nist.secauto.metaschema.core.metapath.function.library.FnData;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.util.CustomCollectors;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides messaging for constraint violations.
 */
public abstract class AbstractConstraintValidationHandler implements IConstraintValidationHandler {
  @NonNull
  private IPathFormatter pathFormatter = IPathFormatter.METAPATH_PATH_FORMATER;

  /**
   * Get the formatter used to generate content paths for validation issue
   * locations.
   *
   * @return the formatter
   */
  @NonNull
  public IPathFormatter getPathFormatter() {
    return pathFormatter;
  }

  /**
   * Set the path formatter to use when generating contextual paths in validation
   * messages.
   *
   * @param formatter
   *          the path formatter to use
   */
  public void setPathFormatter(@NonNull IPathFormatter formatter) {
    this.pathFormatter = Objects.requireNonNull(formatter, "pathFormatter");
  }

  /**
   * Get the path of the provided item using the configured path formatter.
   *
   * @param item
   *          the node item to generate the path for
   * @return the path
   * @see #getPathFormatter()
   */
  protected String toPath(@NonNull INodeItem item) {
    return item.toPath(getPathFormatter());
  }

  /**
   * Construct a new violation message for the provided {@code constraint} applied
   * to the {@code node}.
   *
   * @param constraint
   *          the constraint the requested message pertains to
   * @param target
   *          the item the constraint targeted
   * @param testedItems
   *          the items tested by the constraint
   * @param dynamicContext
   *          the Metapath dynamic execution context to use for Metapath
   *          evaluation
   * @return the new message
   */
  @NonNull
  protected String newCardinalityMinimumViolationMessage(
      @NonNull ICardinalityConstraint constraint,
      @NonNull INodeItem target,
      @NonNull ISequence<? extends INodeItem> testedItems,
      @NonNull DynamicContext dynamicContext) {
    return constraint.getMessage() == null
        ? ObjectUtils.notNull(String.format(
            "The cardinality '%d' is below the required minimum '%d' for items matching '%s'.",
            testedItems.size(),
            constraint.getMinOccurs(),
            constraint.getTarget()))
        : constraint.generateMessage(target, dynamicContext);
  }

  /**
   * Construct a new violation message for the provided {@code constraint} applied
   * to the {@code node}.
   *
   * @param constraint
   *          the constraint the requested message pertains to
   * @param target
   *          the item the constraint targeted
   * @param testedItems
   *          the items tested by the constraint
   * @param dynamicContext
   *          the Metapath dynamic execution context to use for Metapath
   *          evaluation
   * @return the new message
   */
  @NonNull
  protected String newCardinalityMaximumViolationMessage(
      @NonNull ICardinalityConstraint constraint,
      @NonNull INodeItem target,
      @NonNull ISequence<? extends INodeItem> testedItems,
      @NonNull DynamicContext dynamicContext) {
    return constraint.getMessage() == null
        ? ObjectUtils.notNull(String.format(
            "The cardinality '%d' is greater than the required maximum '%d' at: %s.",
            testedItems.size(),
            constraint.getMinOccurs(),
            testedItems.safeStream()
                .map(item -> new StringBuilder(12)
                    .append('\'')
                    .append(toPath(ObjectUtils.notNull(item)))
                    .append('\'')
                    .toString())
                .collect(CustomCollectors.joiningWithOxfordComma("and"))))
        : constraint.generateMessage(target, dynamicContext);
  }

  /**
   * Construct a new violation message for the provided {@code constraint} applied
   * to the {@code node}.
   *
   * @param constraint
   *          the constraint the requested message pertains to
   * @param node
   *          the item the constraint targeted
   * @param oldItem
   *          the original item matching the constraint
   * @param target
   *          the new item matching the constraint
   * @param dynamicContext
   *          the Metapath dynamic execution context to use for Metapath
   *          evaluation
   * @return the new message
   */
  @NonNull
  protected String newIndexDuplicateKeyViolationMessage(
      @NonNull IIndexConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem oldItem,
      @NonNull INodeItem target,
      @NonNull DynamicContext dynamicContext) {
    // TODO: render the key paths
    return constraint.getMessage() == null
        ? ObjectUtils.notNull(String.format("Index '%s' has duplicate key for items at paths '%s' and '%s'",
            constraint.getName(),
            toPath(oldItem),
            toPath(target)))
        : constraint.generateMessage(target, dynamicContext);
  }

  /**
   * Construct a new violation message for the provided {@code constraint} applied
   * to the {@code node}.
   *
   * @param constraint
   *          the constraint the requested message pertains to
   * @param node
   *          the item the constraint targeted
   * @param oldItem
   *          the original item matching the constraint
   * @param target
   *          the new item matching the constraint
   * @param dynamicContext
   *          the Metapath dynamic execution context to use for Metapath
   *          evaluation
   * @return the new message
   */
  @NonNull
  protected String newUniqueKeyViolationMessage(
      @NonNull IUniqueConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem oldItem,
      @NonNull INodeItem target,
      @NonNull DynamicContext dynamicContext) {
    return constraint.getMessage() == null
        ? ObjectUtils.notNull(String.format("Unique constraint violation at paths '%s' and '%s'",
            toPath(oldItem),
            toPath(target)))
        : constraint.generateMessage(target, dynamicContext);
  }

  /**
   * Construct a new violation message for the provided {@code constraint} applied
   * to the {@code node}.
   *
   * @param constraint
   *          the constraint the requested message pertains to
   * @param node
   *          the item the constraint targeted
   * @param target
   *          the target matching the constraint
   * @param value
   *          the target's value
   * @param pattern
   *          the expected pattern
   * @param dynamicContext
   *          the Metapath dynamic execution context to use for Metapath
   *          evaluation
   * @return the new message
   */
  @NonNull
  protected String newMatchPatternViolationMessage(
      @NonNull IMatchesConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem target,
      @NonNull String value,
      @NonNull Pattern pattern,
      @NonNull DynamicContext dynamicContext) {
    return constraint.getMessage() == null
        ? ObjectUtils.notNull(String.format("Value '%s' did not match the pattern '%s' at path '%s'",
            value,
            pattern.pattern(),
            toPath(target)))
        : constraint.generateMessage(target, dynamicContext);
  }

  /**
   * Construct a new violation message for the provided {@code constraint} applied
   * to the {@code node}.
   *
   * @param constraint
   *          the constraint the requested message pertains to
   * @param node
   *          the item the constraint targeted
   * @param target
   *          the target matching the constraint
   * @param value
   *          the target's value
   * @param adapter
   *          the expected data type adapter
   * @param dynamicContext
   *          the Metapath dynamic execution context to use for Metapath
   *          evaluation
   * @return the new message
   */
  @NonNull
  protected String newMatchDatatypeViolationMessage(
      @NonNull IMatchesConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem target,
      @NonNull String value,
      @NonNull IDataTypeAdapter<?> adapter,
      @NonNull DynamicContext dynamicContext) {
    return constraint.getMessage() == null
        ? ObjectUtils.notNull(String.format("Value '%s' did not conform to the data type '%s' at path '%s'",
            value,
            adapter.getPreferredName(),
            toPath(target)))
        : constraint.generateMessage(target, dynamicContext);
  }

  /**
   * Construct a new violation message for the provided {@code constraint} applied
   * to the {@code node}.
   *
   * @param constraint
   *          the constraint the requested message pertains to
   * @param node
   *          the item the constraint targeted
   * @param target
   *          the target matching the constraint
   * @param dynamicContext
   *          the Metapath dynamic execution context to use for Metapath
   *          evaluation
   * @return the new message
   */
  @NonNull
  protected String newExpectViolationMessage(
      @NonNull IExpectConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem target,
      @NonNull DynamicContext dynamicContext) {
    return constraint.getMessage() == null
        ? ObjectUtils.notNull(String.format("Expect constraint '%s' did not match the data at path '%s'",
            constraint.getTest(),
            toPath(target)))
        : constraint.generateMessage(target, dynamicContext);
  }

  /**
   * Construct a new violation message for the provided {@code constraint} applied
   * to the {@code node}.
   *
   * @param constraints
   *          the constraints the requested message pertains to
   * @param target
   *          the target matching the constraint
   * @return the new message
   */
  @NonNull
  protected String newAllowedValuesViolationMessage(
      @NonNull List<IAllowedValuesConstraint> constraints,
      @NonNull INodeItem target) {
    String allowedValues = constraints.stream()
        .flatMap(constraint -> constraint.getAllowedValues().values().stream())
        .map(IAllowedValue::getValue)
        .sorted()
        .distinct()
        .collect(CustomCollectors.joiningWithOxfordComma("or"));

    return ObjectUtils.notNull(String.format("Value '%s' doesn't match one of '%s' at path '%s'",
        FnData.fnDataItem(target).asString(),
        allowedValues,
        toPath(target)));
  }

  /**
   * Construct a new violation message for the provided {@code constraint} applied
   * to the {@code node}.
   *
   * @param constraint
   *          the constraint the requested message pertains to
   * @param node
   *          the item the constraint targeted
   * @return the new message
   */
  @NonNull
  protected String newIndexDuplicateViolationMessage(
      @NonNull IIndexConstraint constraint,
      @NonNull INodeItem node) {
    return ObjectUtils.notNull(String.format("Duplicate index named '%s' found at path '%s'",
        constraint.getName(),
        node.getMetapath()));
  }

  /**
   * Construct a new violation message for the provided {@code constraint} applied
   * to the {@code node}.
   *
   * @param constraint
   *          the constraint the requested message pertains to
   * @param node
   *          the item the constraint targeted
   * @param target
   *          the target matching the constraint
   * @param key
   *          the key derived from the target that failed to be found in the index
   * @param dynamicContext
   *          the Metapath dynamic execution context to use for Metapath
   *          evaluation
   * @return the new message
   */
  @NonNull
  protected String newIndexMissMessage(
      @NonNull IIndexHasKeyConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem target,
      @NonNull List<String> key,
      @NonNull DynamicContext dynamicContext) {
    String keyValues = key.stream()
        .collect(Collectors.joining(","));

    return constraint.getMessage() == null
        ? ObjectUtils.notNull(String.format("Key reference [%s] not found in index '%s' for item at path '%s'",
            keyValues,
            constraint.getIndexName(),
            target.getMetapath()))
        : constraint.generateMessage(target, dynamicContext);
  }

  /**
   * Construct a new generic violation message for the provided {@code constraint}
   * applied to the {@code node}.
   *
   * @param constraint
   *          the constraint the requested message pertains to
   * @param node
   *          the item the constraint targeted
   * @param target
   *          the target matching the constraint
   * @param message
   *          the message to be added before information about the target path
   * @param dynamicContext
   *          the Metapath dynamic execution context to use for Metapath
   *          evaluation
   * @return the new message
   */
  @SuppressWarnings("null")
  @NonNull
  protected String newMissingIndexViolationMessage(
      @NonNull IIndexHasKeyConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem target,
      @NonNull String message,
      @NonNull DynamicContext dynamicContext) {
    return String.format("%s for constraint '%s' for item at path '%s'",
        message,
        Objects.requireNonNullElse(constraint.getId(), "?"),
        target.getMetapath());
  }
}
