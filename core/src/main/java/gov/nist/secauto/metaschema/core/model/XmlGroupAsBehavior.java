/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

public enum XmlGroupAsBehavior {
  /**
   * In XML, child element instances will be wrapped by a grouping element.
   */
  GROUPED,
  /**
   * In XML, child element instances will exist in an unwrapped form.
   */
  UNGROUPED
}
