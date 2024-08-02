/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen;

import edu.umd.cs.findbugs.annotations.NonNull;

public class SchemaGenerationException
    extends IllegalStateException {

  /**
   * the serial version UID.
   */
  private static final long serialVersionUID = 1L;

  public SchemaGenerationException() {
    // use defaults
  }

  public SchemaGenerationException(String message, @NonNull Throwable cause) {
    super(message, cause);
  }

  public SchemaGenerationException(String message) {
    super(message);
  }

  public SchemaGenerationException(@NonNull Throwable cause) {
    super(cause);
  }

}
