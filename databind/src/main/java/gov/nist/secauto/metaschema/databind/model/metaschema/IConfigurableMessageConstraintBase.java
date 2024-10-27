/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema;

import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents constraint metadata that is common to all constraints.
 */
public interface IConfigurableMessageConstraintBase extends IConstraintBase {
  /**
   * Get a custom message to use when the constraint is not satisfied.
   * <p>
   * A custom message allow for more meaningful information, tailored to the test
   * case, to be provided in the case a constraint is not satisfied.
   *
   * @return the message or {@code null} if a default message is to be used
   */
  @Nullable
  String getMessage();
}
