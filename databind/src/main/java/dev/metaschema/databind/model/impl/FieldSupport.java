/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.impl;

import java.lang.reflect.Field;

import edu.umd.cs.findbugs.annotations.NonNull;

public final class FieldSupport {

  /**
   * Ensure that the provided field can be accessed.
   *
   * @param field
   *          the field to check
   */
  @SuppressWarnings("PMD.AvoidAccessibilityAlteration")
  public static void bindField(@NonNull Field field) {
    field.setAccessible(true);
  }

  private FieldSupport() {
    // disable construction
  }
}
