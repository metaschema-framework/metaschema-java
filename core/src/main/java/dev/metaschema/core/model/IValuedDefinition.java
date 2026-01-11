/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model;

import dev.metaschema.core.datatype.IDataTypeAdapter;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * This marker interface identifies Metaschema definition types that have
 * associated values (i.e., field, flag).
 */
public interface IValuedDefinition extends IDefinition {
  /**
   * Retrieves the data type of the definition's value.
   *
   * @return the data type
   */
  @NonNull
  IDataTypeAdapter<?> getJavaTypeAdapter();
}
