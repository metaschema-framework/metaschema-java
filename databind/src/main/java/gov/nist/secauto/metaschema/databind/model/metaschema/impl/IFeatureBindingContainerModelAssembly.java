/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema.impl;

import gov.nist.secauto.metaschema.core.model.IAssemblyInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.IChoiceGroupInstance;
import gov.nist.secauto.metaschema.core.model.IChoiceInstance;
import gov.nist.secauto.metaschema.core.model.IContainerModelAssemblySupport;
import gov.nist.secauto.metaschema.core.model.IFeatureContainerModelAssembly;
import gov.nist.secauto.metaschema.core.model.IFieldInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.IModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IFeatureBindingContainerModelAssembly
    extends IFeatureBindingContainerModel,
    IFeatureContainerModelAssembly<
        IModelInstanceAbsolute,
        INamedModelInstanceAbsolute,
        IFieldInstanceAbsolute,
        IAssemblyInstanceAbsolute,
        IChoiceInstance,
        IChoiceGroupInstance> {
  @Override
  @NonNull
  IContainerModelAssemblySupport<
      IModelInstanceAbsolute,
      INamedModelInstanceAbsolute,
      IFieldInstanceAbsolute,
      IAssemblyInstanceAbsolute,
      IChoiceInstance,
      IChoiceGroupInstance> getModelContainer();

  @Override
  default Collection<IModelInstanceAbsolute> getModelInstances() {
    return getModelContainer().getModelInstances();
  }

  @Override
  default INamedModelInstanceAbsolute getNamedModelInstanceByName(IEnhancedQName name) {
    return getModelContainer().getNamedModelInstanceMap().get(name);
  }

  @SuppressWarnings("null")
  @Override
  default Collection<INamedModelInstanceAbsolute> getNamedModelInstances() {
    return getModelContainer().getNamedModelInstanceMap().values();
  }

  @Override
  default IFieldInstanceAbsolute getFieldInstanceByName(IEnhancedQName name) {
    return getModelContainer().getFieldInstanceMap().get(name);
  }

  @SuppressWarnings("null")
  @Override
  default Collection<IFieldInstanceAbsolute> getFieldInstances() {
    return getModelContainer().getFieldInstanceMap().values();
  }

  @Override
  default IAssemblyInstanceAbsolute getAssemblyInstanceByName(IEnhancedQName name) {
    return getModelContainer().getAssemblyInstanceMap().get(name);
  }

  @SuppressWarnings("null")
  @Override
  default Collection<IAssemblyInstanceAbsolute> getAssemblyInstances() {
    return getModelContainer().getAssemblyInstanceMap().values();
  }

  @Override
  default List<IChoiceInstance> getChoiceInstances() {
    return getModelContainer().getChoiceInstances();
  }

  @Override
  default IChoiceGroupInstance getChoiceGroupInstanceByName(String name) {
    return getModelContainer().getChoiceGroupInstanceMap().get(name);
  }

  @Override
  default Map<String, IChoiceGroupInstance> getChoiceGroupInstances() {
    return getModelContainer().getChoiceGroupInstanceMap();
  }
}
