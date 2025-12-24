/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.format;

/**
 * Enumeration of path format selection options for validation output.
 * <p>
 * This enum allows users to control how node paths are formatted in validation
 * messages and other output. The selection can be explicit (forcing a specific
 * format) or automatic (selecting based on document format).
 *
 * @see IPathFormatter#METAPATH_PATH_FORMATER
 * @see IPathFormatter#XPATH_PATH_FORMATTER
 * @see IPathFormatter#JSON_POINTER_PATH_FORMATTER
 */
public enum PathFormatSelection {
  /**
   * Auto-select the path format based on document format.
   * <p>
   * When this option is selected:
   * <ul>
   * <li>JSON documents use JSON Pointer format (RFC 6901)</li>
   * <li>YAML documents use JSON Pointer format (RFC 6901)</li>
   * <li>XML documents use XPath 3.1 EQName format</li>
   * </ul>
   * <p>
   * This is the default selection when no explicit format is specified.
   */
  AUTO,

  /**
   * Always use Metapath format regardless of document type.
   * <p>
   * Produces paths like: {@code /root/assembly[1]/field[1]/@flag}
   */
  METAPATH,

  /**
   * Always use XPath 3.1 EQName format regardless of document type.
   * <p>
   * Produces namespace-qualified paths like:
   * {@code /Q{http://example.com}root/Q{http://example.com}assembly[1]}
   */
  XPATH,

  /**
   * Always use RFC 6901 JSON Pointer format regardless of document type.
   * <p>
   * Produces paths like: {@code /root/assemblies/0/id}
   */
  JSON_POINTER
}
