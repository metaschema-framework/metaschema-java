/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import gov.nist.secauto.metaschema.core.model.impl.EmptyFlagContainer;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public interface IContainerFlagSupport<FI extends IFlagInstance> {
  /**
   * Provides an empty instance.
   *
   * @param <T>
   *          the Java type of the flag instances
   * @return an empty instance
   */
  @SuppressWarnings("unchecked")
  @NonNull
  static <T extends IFlagInstance> IContainerFlagSupport<T> empty() {
    return (IContainerFlagSupport<T>) EmptyFlagContainer.EMPTY;
  }

  /**
   * Create a new flag container without a JSON key.
   *
   * @param <T>
   *          the Java type of the flag instances
   * @return the flag container
   */
  @NonNull
  static <T extends IFlagInstance> IFlagContainerBuilder<T> builder() {
    return new FlagContainerBuilder<>(null);
  }

  /**
   * Create a new flag container using the provided flag qualified name as the
   * JSON key.
   *
   * @param <T>
   *          the Java type of the flag instances
   * @param jsonKey
   *          the qualified name of the JSON key
   * @return the flag container
   */
  @NonNull
  static <T extends IFlagInstance> IFlagContainerBuilder<T> builder(@NonNull IEnhancedQName jsonKey) {
    return new FlagContainerBuilder<>(jsonKey);
  }

  /**
   * Get a mapping of flag effective name to flag instance.
   *
   * @return the mapping of flag effective name to flag instance
   */
  @NonNull
  Map<IEnhancedQName, FI> getFlagInstanceMap();

  /**
   * Get the JSON key flag instance.
   *
   * @return the flag instance or {@code null} if no JSON key is configured
   */
  @Nullable
  FI getJsonKeyFlagInstance();
}
