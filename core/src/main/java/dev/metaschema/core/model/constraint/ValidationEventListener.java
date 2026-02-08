/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model.constraint;

import java.net.URI;

import dev.metaschema.core.metapath.item.node.INodeItem;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Listener interface for validation pipeline events.
 * <p>
 * Implementations receive callbacks at four granularity levels: overall
 * validation, phase, individual constraint evaluation, and let-statement
 * evaluation. This enables instrumentation such as timing collection without
 * coupling measurement logic to the validation code.
 * <p>
 * A no-op implementation is provided by {@link NoOpValidationEventListener} for
 * use when instrumentation is not needed.
 *
 * @see NoOpValidationEventListener
 * @see ValidationPhase
 */
public interface ValidationEventListener {

  /**
   * Called before validation of a document begins.
   *
   * @param document
   *          the URI of the document being validated
   */
  void beforeValidation(@NonNull URI document);

  /**
   * Called after validation of a document completes.
   *
   * @param document
   *          the URI of the document that was validated
   */
  void afterValidation(@NonNull URI document);

  /**
   * Called before a validation phase begins.
   *
   * @param phase
   *          the phase about to start
   */
  void beforePhase(@NonNull ValidationPhase phase);

  /**
   * Called after a validation phase completes.
   *
   * @param phase
   *          the phase that completed
   */
  void afterPhase(@NonNull ValidationPhase phase);

  /**
   * Called before a single constraint is evaluated against a target node.
   *
   * @param constraint
   *          the constraint being evaluated
   * @param target
   *          the node item the constraint is evaluated against
   */
  void beforeConstraintEvaluation(@NonNull IConstraint constraint, @NonNull INodeItem target);

  /**
   * Called after a single constraint evaluation completes.
   *
   * @param constraint
   *          the constraint that was evaluated
   * @param target
   *          the node item the constraint was evaluated against
   */
  void afterConstraintEvaluation(@NonNull IConstraint constraint, @NonNull INodeItem target);

  /**
   * Called before a let-statement variable binding is evaluated.
   *
   * @param let
   *          the let expression being evaluated
   */
  void beforeLetEvaluation(@NonNull ILet let);

  /**
   * Called after a let-statement variable binding evaluation completes.
   *
   * @param let
   *          the let expression that was evaluated
   */
  void afterLetEvaluation(@NonNull ILet let);
}
