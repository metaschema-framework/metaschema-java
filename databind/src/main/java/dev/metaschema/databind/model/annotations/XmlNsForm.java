/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.annotations;

/**
 * Specifies the namespace qualification behavior for XML elements.
 */
public enum XmlNsForm {
  /**
   * Elements are not namespace-qualified.
   */
  UNQUALIFIED,
  /**
   * Elements are namespace-qualified.
   */
  QUALIFIED,
  /**
   * The namespace form uses the default behavior.
   */
  UNSET;
}
