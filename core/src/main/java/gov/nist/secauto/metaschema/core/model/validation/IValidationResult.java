/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.validation;

import gov.nist.secauto.metaschema.core.model.constraint.IConstraint.Level;

import java.util.Collections;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides data that is the result of a completed content validation.
 */
public interface IValidationResult {
  @NonNull
  IValidationResult PASSING_RESULT = new IValidationResult() {

    @Override
    public boolean isPassing() {
      return true;
    }

    @Override
    public Level getHighestSeverity() {
      return Level.INFORMATIONAL;
    }

    @SuppressWarnings("null")
    @Override
    public List<? extends IValidationFinding> getFindings() {
      return Collections.emptyList();
    }
  };

  /**
   * Determines if the result of validation was valid or not.
   *
   * @return {@code true} if the result was determined to be valid or
   *         {@code false} otherwise
   */
  default boolean isPassing() {
    return getHighestSeverity().ordinal() < Level.ERROR.ordinal();
  }

  /**
   * Get the highest finding severity level for the validation. The level
   * {@link Level#INFORMATIONAL} will be returned if no validation findings were
   * identified.
   *
   * @return the highest finding severity level
   */
  @NonNull
  Level getHighestSeverity();

  /**
   * Get the list of validation findings, which may be empty.
   *
   * @return the list
   */
  @NonNull
  List<? extends IValidationFinding> getFindings();
}
