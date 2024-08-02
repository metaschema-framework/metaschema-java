/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import java.util.List;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

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
}
