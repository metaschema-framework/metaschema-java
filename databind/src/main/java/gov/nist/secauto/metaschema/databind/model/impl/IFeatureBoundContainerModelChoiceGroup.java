/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.impl;

import gov.nist.secauto.metaschema.core.model.IContainerModelSupport;
import gov.nist.secauto.metaschema.core.model.IFeatureContainerModelGrouped;
import gov.nist.secauto.metaschema.databind.model.IBoundContainerModelChoiceGroup;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelGroupedAssembly;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelGroupedField;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelGroupedNamed;

import java.util.Collection;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IFeatureBoundContainerModelChoiceGroup
    extends IBoundContainerModelChoiceGroup, IFeatureContainerModelGrouped<
        IBoundInstanceModelGroupedNamed,
        IBoundInstanceModelGroupedField,
        IBoundInstanceModelGroupedAssembly> {

  @Override
  @NonNull
  IContainerModelSupport<
      IBoundInstanceModelGroupedNamed,
      IBoundInstanceModelGroupedNamed,
      IBoundInstanceModelGroupedField,
      IBoundInstanceModelGroupedAssembly> getModelContainer();

  @Override
  default Collection<IBoundInstanceModelGroupedNamed> getModelInstances() {
    return getModelContainer().getModelInstances();
  }

  @Override
  default IBoundInstanceModelGroupedNamed getNamedModelInstanceByName(QName name) {
    return getModelContainer().getNamedModelInstanceMap().get(name);
  }

  @SuppressWarnings("null")
  @Override
  default Collection<IBoundInstanceModelGroupedNamed> getNamedModelInstances() {
    return getModelContainer().getNamedModelInstanceMap().values();
  }

  @Override
  default IBoundInstanceModelGroupedField getFieldInstanceByName(QName name) {
    return getModelContainer().getFieldInstanceMap().get(name);
  }

  @SuppressWarnings("null")
  @Override
  default Collection<IBoundInstanceModelGroupedField> getFieldInstances() {
    return getModelContainer().getFieldInstanceMap().values();
  }

  @Override
  default IBoundInstanceModelGroupedAssembly getAssemblyInstanceByName(QName name) {
    return getModelContainer().getAssemblyInstanceMap().get(name);
  }

  @SuppressWarnings("null")
  @Override
  default Collection<IBoundInstanceModelGroupedAssembly> getAssemblyInstances() {
    return getModelContainer().getAssemblyInstanceMap().values();
  }
}
