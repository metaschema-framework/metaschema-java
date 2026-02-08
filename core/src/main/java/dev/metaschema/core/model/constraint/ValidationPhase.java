/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model.constraint;

/**
 * Represents the distinct phases of the validation pipeline.
 * <p>
 * Used by {@link ValidationEventListener} to report phase-level timing events.
 */
public enum ValidationPhase {
  /** Schema validation against XML or JSON schema. */
  SCHEMA_VALIDATION,
  /** Constraint evaluation against Metaschema constraints. */
  CONSTRAINT_VALIDATION,
  /** Post-validation finalization, including cross-document constraints. */
  FINALIZATION
}
