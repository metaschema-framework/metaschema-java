/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model;

import java.util.Collection;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides grouped container model functionality through delegation.
 * <p>
 * This interface provides default implementations for grouped container model
 * operations by delegating to an {@link IContainerModelSupport} instance.
 *
 * @param <NMI>
 *          the Java type of named model instances
 * @param <FI>
 *          the Java type of field instances
 * @param <AI>
 *          the Java type of assembly instances
 */
public interface IFeatureContainerModelGrouped<
    NMI extends INamedModelInstanceGrouped,
    FI extends IFieldInstanceGrouped,
    AI extends IAssemblyInstanceGrouped>
    extends IContainerModelGrouped {
  /**
   * Get the model container implementation instance.
   *
   * @return the model container instance
   */
  @NonNull
  IContainerModelSupport<NMI, NMI, FI, AI> getModelContainer();

  @Override
  default boolean hasChildren() {
    return !getModelContainer().getModelInstances().isEmpty();
  }

  @Override
  default Collection<NMI> getModelInstances() {
    return getNamedModelInstances();
  }

  @Override
  default NMI getNamedModelInstanceByName(Integer name) {
    return getModelContainer().getNamedModelInstanceMap().get(name);
  }

  @SuppressWarnings("null")
  @Override
  default Collection<NMI> getNamedModelInstances() {
    return getModelContainer().getNamedModelInstanceMap().values();
  }

  @Override
  default FI getFieldInstanceByName(Integer name) {
    return getModelContainer().getFieldInstanceMap().get(name);
  }

  @SuppressWarnings("null")
  @Override
  default Collection<FI> getFieldInstances() {
    return getModelContainer().getFieldInstanceMap().values();
  }

  @Override
  default AI getAssemblyInstanceByName(Integer name) {
    return getModelContainer().getAssemblyInstanceMap().get(name);
  }

  @SuppressWarnings("null")
  @Override
  default Collection<AI> getAssemblyInstances() {
    return getModelContainer().getAssemblyInstanceMap().values();
  }
}
