/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An {@link ExitStatus} implementation that represents a status without an
 * associated message.
 * <p>
 * This implementation is useful when only the exit code needs to be
 * communicated, without additional context or explanation.
 */
public class NonMessageExitStatus
    extends AbstractExitStatus {

  /**
   * Construct a new exit status without an associated message.
   *
   * @param code
   *          the non-null exit code representing the status
   */
  NonMessageExitStatus(@NonNull ExitCode code) {
    super(code);
  }

  /**
   * {@inheritDoc}
   *
   * @return {@code null} as this implementation does not support messages
   */
  @Override
  protected String getMessage() {
    return null;
  }
}
