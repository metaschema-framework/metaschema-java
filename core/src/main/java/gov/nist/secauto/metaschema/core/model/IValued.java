/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * A marker interface for Metaschema constructs that can have a value.
 */
public interface IValued extends IDefaultable {
  /**
   * Get the current value from the provided {@code parentInstance} object.
   * <p>
   * The provided object must be of the type associated with the definition
   * containing this instance.
   *
   * @param parent
   *          the object associated with the definition containing this property
   * @return the value if available, or {@code null} otherwise
   */
  // from IInstanceAbsolute
  @Nullable
  default Object getValue(@NonNull Object parent) {
    return getResolvedDefaultValue();
  }
}
