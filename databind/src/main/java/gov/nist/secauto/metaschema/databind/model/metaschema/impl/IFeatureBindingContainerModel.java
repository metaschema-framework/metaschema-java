/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema.impl;

import gov.nist.secauto.metaschema.core.model.IAssemblyInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.IContainerModelAbsolute;
import gov.nist.secauto.metaschema.core.model.IContainerModelSupport;
import gov.nist.secauto.metaschema.core.model.IFeatureContainerModel;
import gov.nist.secauto.metaschema.core.model.IFieldInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.IModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

import java.util.Collection;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IFeatureBindingContainerModel
    extends IContainerModelAbsolute, IFeatureContainerModel<
        IModelInstanceAbsolute,
        INamedModelInstanceAbsolute,
        IFieldInstanceAbsolute,
        IAssemblyInstanceAbsolute> {
  @Override
  @NonNull
  IContainerModelSupport<
      IModelInstanceAbsolute,
      INamedModelInstanceAbsolute,
      IFieldInstanceAbsolute,
      IAssemblyInstanceAbsolute> getModelContainer();

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
}
