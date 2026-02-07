/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model;

import java.util.List;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Provides assembly-specific container model functionality through delegation.
 * <p>
 * This interface extends absolute container model features with
 * assembly-specific capabilities, including choice and choice group instances.
 * Implementations delegate to an {@link IContainerModelAssemblySupport}
 * instance.
 *
 * @param <MI>
 *          the Java type of model instances
 * @param <NMI>
 *          the Java type of named model instances
 * @param <FI>
 *          the Java type of field instances
 * @param <AI>
 *          the Java type of assembly instances
 * @param <CI>
 *          the Java type of choice instances
 * @param <CGI>
 *          the Java type of choice group instances
 */
public interface IFeatureContainerModelAssembly<
    MI extends IModelInstanceAbsolute,
    NMI extends INamedModelInstanceAbsolute,
    FI extends IFieldInstanceAbsolute,
    AI extends IAssemblyInstanceAbsolute,
    CI extends IChoiceInstance,
    CGI extends IChoiceGroupInstance>
    extends IContainerModelAssembly,
    IFeatureContainerModelAbsolute<MI, NMI, FI, AI> {
  /**
   * Get the model container implementation instance.
   *
   * @return the model container instance
   */
  @Override
  @NonNull
  IContainerModelAssemblySupport<MI, NMI, FI, AI, CI, CGI> getModelContainer();

  @Override
  default List<CI> getChoiceInstances() {
    return getModelContainer().getChoiceInstances();
  }

  @Override
  default CGI getChoiceGroupInstanceByName(String name) {
    return getModelContainer().getChoiceGroupInstanceMap().get(name);
  }

  @Override
  default Map<String, CGI> getChoiceGroupInstances() {
    return getModelContainer().getChoiceGroupInstanceMap();
  }

  @Override
  @Nullable
  default IAnyInstance getAnyInstance() {
    return getModelContainer().getAnyInstance();
  }
}
