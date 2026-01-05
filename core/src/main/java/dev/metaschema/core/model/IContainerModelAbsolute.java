/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model;

import java.util.Collection;

/**
 * Represents a model container with absolute (non-grouped) instances.
 * <p>
 * Absolute instances are identified by their effective name (index) rather than
 * a use name. This interface narrows the return types of
 * {@link IContainerModel} methods to absolute instance types.
 */
public interface IContainerModelAbsolute extends IContainerModel {

  @Override
  Collection<? extends IModelInstanceAbsolute> getModelInstances();

  @Override
  Collection<? extends INamedModelInstanceAbsolute> getNamedModelInstances();

  @Override
  INamedModelInstanceAbsolute getNamedModelInstanceByName(Integer name);

  @Override
  Collection<? extends IFieldInstanceAbsolute> getFieldInstances();

  @Override
  IFieldInstanceAbsolute getFieldInstanceByName(Integer name);

  @Override
  Collection<? extends IAssemblyInstanceAbsolute> getAssemblyInstances();

  @Override
  IAssemblyInstanceAbsolute getAssemblyInstanceByName(Integer name);
}
