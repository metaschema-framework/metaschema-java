/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

/**
 * Represents a location within a resource.
 */
public interface IResourceLocation {
  /**
   * Get the line for a location within a resource.
   *
   * @return the line number or {@code -1} if unknown
   */
  int getLine();

  /**
   * Get the line column for a location within a resource.
   *
   * @return the column number or {@code -1} if unknown
   */
  int getColumn();

  /**
   * Get the zero-based character offset for a location within a resource.
   *
   * @return the character offset or {@code -1} if unknown
   */
  long getCharOffset();

  /**
   * Get the zero-based byte offset for a location within a resource.
   *
   * @return the byte offset or {@code -1} if unknown
   */
  long getByteOffset();
}
