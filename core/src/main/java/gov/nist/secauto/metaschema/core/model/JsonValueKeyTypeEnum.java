/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

public enum JsonValueKeyTypeEnum {
  /**
   * No value key is defined, and a type specific value key will be used.
   */
  NONE,
  /**
   * A static value key string is defined which will be used.
   */
  STATIC_LABEL,
  /**
   * A flag is idenfied as the value key, who's value will be used.
   */
  FLAG;
}
