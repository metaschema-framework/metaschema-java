/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

public enum JsonGroupAsBehavior {
  /**
   * In JSON, the group of instances will be represented as a JSON object, with
   * each instance's JSON key used as the property and the remaining data
   * represented as a child object of that property.
   */
  KEYED,
  /**
   * In JSON, the group of instances will be represented as a single JSON object
   * if there is one, or as an array of JSON objects if there is more than one. An
   * empty array will be used when no items exist in the group.
   */
  SINGLETON_OR_LIST,
  /**
   * In JSON, the group of instances will be represented as an array of JSON
   * objects if there is more than one. An empty array will be used when no items
   * exist in the group.
   */
  LIST,
  /**
   * In JSON, the group of instances will be represented as a single JSON object.
   */
  NONE
}
