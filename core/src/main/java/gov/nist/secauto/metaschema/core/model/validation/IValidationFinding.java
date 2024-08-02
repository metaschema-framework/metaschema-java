/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.validation;

import gov.nist.secauto.metaschema.core.model.IResourceLocation;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraint;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Provides information about an individual finding that is the result of a
 * completed content validation.
 */
public interface IValidationFinding {
  /**
   * The finding type.
   */
  enum Kind {
    /**
     * The finding does not apply to the intended purpose of the validation.
     */
    NOT_APPLICABLE,
    /**
     * The finding represents a successful result.
     */
    PASS,
    /**
     * The finding represents an unsuccessful result.
     */
    FAIL,
    /**
     * The finding is providing information that does not indicate success or
     * failure.
     */
    INFORMATIONAL;
  }

  /**
   * Get the unique identifier for the finding.
   *
   * @return the identifier
   */
  @Nullable
  String getIdentifier();

  /**
   * Get the finding's severity.
   *
   * @return the severity
   */
  @NonNull
  IConstraint.Level getSeverity();

  /**
   * Get the finding type.
   *
   * @return the finding type
   */
  @NonNull
  Kind getKind();

  /**
   * Get the document's URI.
   *
   * @return the document's URI or {@code null} if it is not known
   */
  @Nullable
  URI getDocumentUri();

  /**
   * Get the location in the associated resource associated with the finding.
   *
   * @return the location or {@code null} if no location is known
   */
  @Nullable
  IResourceLocation getLocation();

  /**
   * Get the path expression type provided by the {@link #getPath()} method.
   *
   * @return the path type identifier or {@code null} if unknown
   */
  @Nullable
  String getPathKind();

  /**
   * A format specific path to the finding in the source document.
   *
   * @return the path or {@code null} if unknown
   */
  @Nullable
  String getPath();

  /**
   * Get the finding message.
   *
   * @return the message or {@code null} if there is no message
   */
  @Nullable
  String getMessage();

  /**
   * Get the exception associated with the finding.
   *
   * @return the {@link Throwable} or {@code null} if no thowable is associated
   *         with the finding
   */
  Throwable getCause();
}
