/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import java.util.Collection;

import javax.xml.namespace.QName;

public interface IContainerModelAbsolute extends IContainerModel {

  @Override
  Collection<? extends IModelInstanceAbsolute> getModelInstances();

  @Override
  Collection<? extends INamedModelInstanceAbsolute> getNamedModelInstances();

  @Override
  INamedModelInstanceAbsolute getNamedModelInstanceByName(QName name);

  @Override
  Collection<? extends IFieldInstanceAbsolute> getFieldInstances();

  @Override
  IFieldInstanceAbsolute getFieldInstanceByName(QName name);

  @Override
  Collection<? extends IAssemblyInstanceAbsolute> getAssemblyInstances();

  @Override
  IAssemblyInstanceAbsolute getAssemblyInstanceByName(QName name);
}
