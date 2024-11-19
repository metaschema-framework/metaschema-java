/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

import java.util.Collection;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IFeatureContainerModelGrouped<
    NMI extends INamedModelInstanceGrouped,
    FI extends IFieldInstanceGrouped,
    AI extends IAssemblyInstanceGrouped>
    extends IContainerModelGrouped, IContainerModel {
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
  default NMI getNamedModelInstanceByName(IEnhancedQName name) {
    return getModelContainer().getNamedModelInstanceMap().get(name);
  }

  @SuppressWarnings("null")
  @Override
  default Collection<NMI> getNamedModelInstances() {
    return getModelContainer().getNamedModelInstanceMap().values();
  }

  @Override
  default FI getFieldInstanceByName(IEnhancedQName name) {
    return getModelContainer().getFieldInstanceMap().get(name);
  }

  @SuppressWarnings("null")
  @Override
  default Collection<FI> getFieldInstances() {
    return getModelContainer().getFieldInstanceMap().values();
  }

  @Override
  default AI getAssemblyInstanceByName(IEnhancedQName name) {
    return getModelContainer().getAssemblyInstanceMap().get(name);
  }

  @SuppressWarnings("null")
  @Override
  default Collection<AI> getAssemblyInstances() {
    return getModelContainer().getAssemblyInstanceMap().values();
  }
}
