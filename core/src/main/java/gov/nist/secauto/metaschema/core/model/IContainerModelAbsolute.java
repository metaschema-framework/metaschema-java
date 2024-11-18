/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

import java.util.Collection;

public interface IContainerModelAbsolute extends IContainerModel {

  @Override
  Collection<? extends IModelInstanceAbsolute> getModelInstances();

  @Override
  Collection<? extends INamedModelInstanceAbsolute> getNamedModelInstances();

  @Override
  INamedModelInstanceAbsolute getNamedModelInstanceByName(IEnhancedQName name);

  @Override
  Collection<? extends IFieldInstanceAbsolute> getFieldInstances();

  @Override
  IFieldInstanceAbsolute getFieldInstanceByName(IEnhancedQName name);

  @Override
  Collection<? extends IAssemblyInstanceAbsolute> getAssemblyInstances();

  @Override
  IAssemblyInstanceAbsolute getAssemblyInstanceByName(IEnhancedQName name);
}
