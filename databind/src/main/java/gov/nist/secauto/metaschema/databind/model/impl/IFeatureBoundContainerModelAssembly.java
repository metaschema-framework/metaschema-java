/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.impl;

import gov.nist.secauto.metaschema.core.model.IChoiceInstance;
import gov.nist.secauto.metaschema.core.model.IContainerModelAssemblySupport;
import gov.nist.secauto.metaschema.core.model.IFeatureContainerModelAssembly;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.databind.model.IBoundContainerModelAssembly;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModel;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelAssembly;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelChoiceGroup;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelField;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelNamed;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IFeatureBoundContainerModelAssembly<
    MI extends IBoundInstanceModel<?>,
    NMI extends IBoundInstanceModelNamed<?>,
    FI extends IBoundInstanceModelField<?>,
    AI extends IBoundInstanceModelAssembly,
    CGI extends IBoundInstanceModelChoiceGroup>
    extends IBoundContainerModelAssembly, IFeatureContainerModelAssembly<MI, NMI, FI, AI, IChoiceInstance, CGI> {
  @Override
  @NonNull
  IContainerModelAssemblySupport<MI, NMI, FI, AI, IChoiceInstance, CGI> getModelContainer();

  @Override
  default Collection<MI> getModelInstances() {
    return getModelContainer().getModelInstances();
  }

  @Override
  default NMI getNamedModelInstanceByName(QName name) {
    return getModelContainer().getNamedModelInstanceMap().get(name);
  }

  @SuppressWarnings("null")
  @Override
  default Collection<NMI> getNamedModelInstances() {
    return getModelContainer().getNamedModelInstanceMap().values();
  }

  @Override
  default FI getFieldInstanceByName(QName name) {
    return getModelContainer().getFieldInstanceMap().get(name);
  }

  @SuppressWarnings("null")
  @Override
  default Collection<FI> getFieldInstances() {
    return getModelContainer().getFieldInstanceMap().values();
  }

  @Override
  default AI getAssemblyInstanceByName(QName name) {
    return getModelContainer().getAssemblyInstanceMap().get(name);
  }

  @SuppressWarnings("null")
  @Override
  default Collection<AI> getAssemblyInstances() {
    return getModelContainer().getAssemblyInstanceMap().values();
  }

  @Override
  @NonNull
  default List<IChoiceInstance> getChoiceInstances() {
    // not supported
    return CollectionUtil.emptyList();
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
