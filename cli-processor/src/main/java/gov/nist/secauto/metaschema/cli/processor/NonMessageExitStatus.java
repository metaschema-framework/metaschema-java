/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor;

import edu.umd.cs.findbugs.annotations.NonNull;

public class NonMessageExitStatus
    extends AbstractExitStatus {

  /**
   * Construct a new message status.
   */
  NonMessageExitStatus(@NonNull ExitCode code) {
    super(code);
  }

  @Override
  protected String getMessage() {
    // always null
    return null;
  }
}
