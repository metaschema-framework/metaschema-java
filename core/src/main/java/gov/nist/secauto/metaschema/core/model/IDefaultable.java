/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * A marker interface for Metaschema constructs that can have a default value.
 */
public interface IDefaultable {

  /**
   * Retrieves the default data value for this model construct.
   * <p>
   * Child implementations are expected to override this method to provide a more
   * reasonable default value.
   *
   * @return the default value or {@code null} if there is no default
   */
  // from: IModelElement
  @Nullable
  default Object getDefaultValue() {
    // no value by default
    return null;
  }

  /**
   * Get the effective default value for the model construct.
   * <p>
   * This should consider default values in any related referenced definitions or
   * child constructs as needed to determine the default to use.
   *
   * @return the effective default value or {@code null} if there is no effective
   *         default value
   */
  // from IInstance
  @Nullable
  default Object getEffectiveDefaultValue() {
    return getDefaultValue();
  }

  /**
   * Get the actual default value to use for the model construct.
   * <p>
   * This will consider the effective default value in the use context to
   * determine the appropriate default to use. Factors such as the required
   * instance cardinality may affect if the effective default or an empty
   * collection is used.
   *
   * @return the actual default value or {@code null} if there is no actual
   *         default value
   */
  @Nullable
  default Object getResolvedDefaultValue() {
    return getEffectiveDefaultValue();
  }
}
