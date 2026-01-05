/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model;

/**
 * Defines the type of JSON value key used to identify data values.
 */
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
   * A flag is identified as the value key, whose value will be used.
   */
  FLAG;
}
