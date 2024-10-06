/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.model.IResourceLocation;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraint.Level;
import gov.nist.secauto.metaschema.core.model.validation.IValidationFinding;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.net.URI;
import java.util.Comparator;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents an individual constraint validation issue.
 */
public class ConstraintValidationFinding implements IValidationFinding { // NOPMD - intentional
  @NonNull
  private final List<? extends IConstraint> constraints;
  @Nullable
  private final String message;
  @NonNull
  private final INodeItem node;
  @NonNull
  private final INodeItem target;
  @NonNull
  private final List<? extends INodeItem> subjects;
  private final Throwable cause;
  @NonNull
  private final Kind kind;
  @NonNull
  private final Level severity;

  private ConstraintValidationFinding(
      @NonNull List<? extends IConstraint> constraints,
      @NonNull INodeItem node,
      @Nullable String message,
      @NonNull INodeItem target,
      @NonNull List<? extends INodeItem> subjects,
      @NonNull Kind kind,
      @NonNull Level severity,
      @Nullable Throwable cause) {
    this.constraints = constraints;
    this.node = node;
    this.message = message;
    this.target = target;
    this.subjects = subjects;
    this.kind = kind;
    this.severity = severity;
    this.cause = cause;
  }

  @Override
  public String getIdentifier() {
    return constraints.size() == 1 ? constraints.get(0).getId() : null;
  }

  /**
   * Get the constraints associated with the finding.
   *
   * @return the constraints
   */
  @NonNull
  public List<? extends IConstraint> getConstraints() {
    return constraints;
  }

  @Override
  public String getMessage() {
    return message;
  }

  /**
   * Get the context node used to evaluate the constraints.
   *
   * @return the context node
   */
  @NonNull
  public INodeItem getNode() {
    return node;
  }

  /**
   * Get the target of the finding.
   *
   * @return the target node
   */
  @NonNull
  public INodeItem getTarget() {
    return target;
  }

  /**
   * Get the subjects of the finding, which are resolved by evaluating the
   * constraint target expression.
   *
   * @return the subject nodes
   */
  @NonNull
  public List<? extends INodeItem> getSubjects() {
    return subjects;
  }

  @Override
  public IResourceLocation getLocation() {
    // first try the target
    INodeItem node = getTarget();
    IResourceLocation retval = node.getLocation();
    if (retval == null) {
      // if no location, try the parent
      node = node.getParentContentNodeItem();
      if (node != null) {
        retval = node.getLocation();
      }
    }
    return retval;
  }

  @Override
  public String getPathKind() {
    return "metapath";
  }

  @Override
  public String getPath() {
    return getTarget().getMetapath();
  }

  @Override
  public Throwable getCause() {
    return cause;
  }

  @Override
  public Kind getKind() {
    return kind;
  }

  @Override
  public Level getSeverity() {
    return severity;
  }

  @Override
  public URI getDocumentUri() {
    return getTarget().getBaseUri();
  }

  /**
   * Construct a new finding builder.
   *
   * @param constraints
   *          the constraints associated with this finding
   * @param node
   *          the context node used to evaluate the constraints
   * @return a new builder
   */
  @NonNull
  public static Builder builder(@NonNull List<? extends IConstraint> constraints, @NonNull INodeItem node) {
    return new Builder(constraints, node);
  }

  /**
   * Construct a new finding builder.
   *
   * @param constraint
   *          the constraint associated with this finding
   * @param node
   *          the context node used to evaluate the constraints
   * @return a new builder
   */
  @NonNull
  public static Builder builder(@NonNull IConstraint constraint, @NonNull INodeItem node) {
    return new Builder(CollectionUtil.singletonList(constraint), node);
  }

  public static final class Builder {
    @NonNull
    private final List<? extends IConstraint> constraints;
    @NonNull
    private final INodeItem node;
    @NonNull
    private INodeItem target;
    private String message;
    private List<? extends INodeItem> subjects;
    private Throwable cause;
    private Kind kind;
    private Level severity;

    private Builder(@NonNull List<? extends IConstraint> constraints, @NonNull INodeItem node) {
      this.constraints = constraints;
      this.node = node;
      this.target = node;
    }

    /**
     * Use the provided target for the validation finding.
     *
     * @param target
     *          the finding target
     * @return this builder
     */
    public Builder target(@NonNull INodeItem target) {
      this.target = target;
      return this;
    }

    /**
     * Use the provided message for the validation finding.
     *
     * @param message
     *          the message target
     * @return this builder
     */
    @NonNull
    public Builder message(@NonNull String message) {
      this.message = message;
      return this;
    }

    /**
     * Use the provided subjects for the validation finding.
     *
     * @param subjects
     *          the finding subjects
     * @return this builder
     */
    @NonNull
    public Builder subjects(@NonNull List<? extends INodeItem> subjects) {
      this.subjects = CollectionUtil.unmodifiableList(subjects);
      return this;
    }

    /**
     * Use the provided cause for the validation finding.
     *
     * @param cause
     *          the finding cause
     * @return this builder
     */
    @NonNull
    public Builder cause(@NonNull Throwable cause) {
      this.cause = cause;
      return this;
    }

    /**
     * Use the provided kind for the validation finding.
     *
     * @param kind
     *          the finding kind
     * @return this builder
     */
    @NonNull
    public Builder kind(@NonNull Kind kind) {
      this.kind = kind;
      return this;
    }

    /**
     * Use the provided severity for the validation finding.
     *
     * @param severity
     *          the finding severity
     * @return this builder
     */
    @NonNull
    public Builder severity(@NonNull Level severity) {
      this.severity = severity;
      return this;
    }

    /**
     * Generate the finding using the previously provided data.
     *
     * @return a new finding
     */
    @NonNull
    public ConstraintValidationFinding build() {
      Level severity = ObjectUtils.notNull(this.severity == null ? constraints.stream()
          .map(IConstraint::getLevel)
          .max(Comparator.comparing(Level::ordinal))
          .get() : this.severity);

      List<? extends INodeItem> subjects = this.subjects == null ? CollectionUtil.emptyList() : this.subjects;

      assert subjects != null : "subjects must not be null";
      assert kind != null : "kind must not be null";

      return new ConstraintValidationFinding(
          constraints,
          node,
          message,
          target,
          subjects,
          kind,
          severity,
          cause);
    }
  }
}
