/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model.constraint;

import java.net.URI;
import java.util.List;

import dev.metaschema.core.metapath.item.node.INodeItem;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A {@link ValidationEventListener} that delegates all events to multiple
 * listeners.
 * <p>
 * This enables multiple independent consumers (e.g., {@link TimingCollector}
 * for aggregate timing and a SARIF handler for per-result timing) to receive
 * the same validation events.
 *
 * @see ValidationEventListener
 */
public class CompositeValidationEventListener implements ValidationEventListener {
  @NonNull
  private final List<ValidationEventListener> listeners;

  /**
   * Construct a composite listener that delegates to the given listeners.
   *
   * @param listeners
   *          the listeners to delegate to, in order
   */
  public CompositeValidationEventListener(@NonNull List<ValidationEventListener> listeners) {
    this.listeners = List.copyOf(listeners);
  }

  @Override
  public void beforeValidation(@NonNull URI document) {
    for (ValidationEventListener listener : listeners) {
      listener.beforeValidation(document);
    }
  }

  @Override
  public void afterValidation(@NonNull URI document) {
    for (ValidationEventListener listener : listeners) {
      listener.afterValidation(document);
    }
  }

  @Override
  public void beforePhase(@NonNull ValidationPhase phase) {
    for (ValidationEventListener listener : listeners) {
      listener.beforePhase(phase);
    }
  }

  @Override
  public void afterPhase(@NonNull ValidationPhase phase) {
    for (ValidationEventListener listener : listeners) {
      listener.afterPhase(phase);
    }
  }

  @Override
  public void beforeConstraintEvaluation(@NonNull IConstraint constraint, @NonNull INodeItem target) {
    for (ValidationEventListener listener : listeners) {
      listener.beforeConstraintEvaluation(constraint, target);
    }
  }

  @Override
  public void afterConstraintEvaluation(@NonNull IConstraint constraint, @NonNull INodeItem target) {
    for (ValidationEventListener listener : listeners) {
      listener.afterConstraintEvaluation(constraint, target);
    }
  }

  @Override
  public void beforeLetEvaluation(@NonNull ILet let) {
    for (ValidationEventListener listener : listeners) {
      listener.beforeLetEvaluation(let);
    }
  }

  @Override
  public void afterLetEvaluation(@NonNull ILet let) {
    for (ValidationEventListener listener : listeners) {
      listener.afterLetEvaluation(let);
    }
  }
}
