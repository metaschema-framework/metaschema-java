/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import java.util.Collection;

import javax.xml.namespace.QName;

/**
 * Common interface for model container support classes.
 *
 * @param <MI>
 *          the model instance Java type
 * @param <NMI>
 *          the named model instance Java type
 * @param <FI>
 *          the field instance Java type
 * @param <AI>
 *          the assembly instance Java type
 */
public interface IFeatureContainerModelAbsolute<
    MI extends IModelInstanceAbsolute,
    NMI extends INamedModelInstanceAbsolute,
    FI extends IFieldInstanceAbsolute,
    AI extends IAssemblyInstanceAbsolute>
    extends IContainerModelAbsolute, IFeatureContainerModel<MI, NMI, FI, AI> {
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
}
