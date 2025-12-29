/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Indicates an unrecoverable error occurred during schema generation.
 * <p>
 * This exception is thrown when the schema generator encounters a condition
 * that prevents it from completing the schema generation process.
 */
public class SchemaGenerationException
    extends IllegalStateException {

  /**
   * the serial version UID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new schema generation exception with no detail message.
   */
  public SchemaGenerationException() {
    // use defaults
  }

  /**
   * Constructs a new schema generation exception with the specified detail
   * message and cause.
   *
   * @param message
   *          the detail message providing context about the failure
   * @param cause
   *          the underlying cause of the exception
   */
  public SchemaGenerationException(String message, @NonNull Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new schema generation exception with the specified detail
   * message.
   *
   * @param message
   *          the detail message providing context about the failure
   */
  public SchemaGenerationException(String message) {
    super(message);
  }

  /**
   * Constructs a new schema generation exception with the specified cause.
   *
   * @param cause
   *          the underlying cause of the exception
   */
  public SchemaGenerationException(@NonNull Throwable cause) {
    super(cause);
  }

}
