/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model.constraint;

import java.net.URI;

import dev.metaschema.core.metapath.item.node.INodeItem;

/**
 * A no-op implementation of {@link ValidationEventListener} that ignores all
 * events.
 * <p>
 * This is the default listener used by {@link ValidationConfig} when no
 * instrumentation is configured, ensuring zero overhead in the common case.
 */
public class NoOpValidationEventListener implements ValidationEventListener {

  /** Singleton instance. */
  public static final NoOpValidationEventListener INSTANCE = new NoOpValidationEventListener();

  @Override
  public void beforeValidation(URI document) {
    // no-op
  }

  @Override
  public void afterValidation(URI document) {
    // no-op
  }

  @Override
  public void beforePhase(ValidationPhase phase) {
    // no-op
  }

  @Override
  public void afterPhase(ValidationPhase phase) {
    // no-op
  }

  @Override
  public void beforeConstraintEvaluation(IConstraint constraint, INodeItem target) {
    // no-op
  }

  @Override
  public void afterConstraintEvaluation(IConstraint constraint, INodeItem target) {
    // no-op
  }

  @Override
  public void beforeLetEvaluation(ILet let) {
    // no-op
  }

  @Override
  public void afterLetEvaluation(ILet let) {
    // no-op
  }
}
