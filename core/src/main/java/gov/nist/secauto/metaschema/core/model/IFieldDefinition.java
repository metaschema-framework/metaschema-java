/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public interface IFieldDefinition extends IModelDefinition, IValuedDefinition, IField {
  @Override
  default IFieldInstance getInlineInstance() {
    // not inline by default
    return null;
  }

  /**
   * Retrieves the key to use as the field name for this field's value in JSON.
   *
   * @return a string or a FlagInstance value
   */
  @Nullable
  default Object getJsonValueKey() {
    Object retval = getJsonValueKeyFlagInstance();
    if (retval == null) {
      retval = getEffectiveJsonValueKeyName();
    }
    return retval;
  }

  /**
   * Check if a JSON value key flag is configured.
   *
   * @return {@code true} if a JSON value key flag is configured, or {@code false}
   *         otherwise
   */
  default boolean hasJsonValueKeyFlagInstance() {
    return getJsonValueKeyFlagInstance() != null;
  }

  /**
   * Retrieves the flag instance who's value will be used as the "value key".
   *
   * @return the configured flag instance, or {@code null} if a flag is not
   *         configured as the "value key"
   */
  @Nullable
  IFlagInstance getJsonValueKeyFlagInstance();

  /**
   * Retrieves the configured static label to use as the value key, or the type
   * specific name if a label is not configured.
   *
   * @return the value key label
   */
  @Nullable
  String getJsonValueKeyName();

  /**
   * Retrieves the configured static label to use as the value key, or the type
   * specific name if a label is not configured.
   *
   * @return the value key label
   */
  @NonNull
  default String getEffectiveJsonValueKeyName() {
    String retval = getJsonValueKeyName();
    if (retval == null || retval.isEmpty()) {
      retval = getJavaTypeAdapter().getDefaultJsonValueKey();
    }
    return retval;
  }

  /**
   * Get the value of the field's value from the field item object.
   *
   * @param item
   *          the field item
   * @return the field's value or {@code null} if it has no value
   */
  default Object getFieldValue(@NonNull Object item) {
    // no value by default
    return null;
  }
}
