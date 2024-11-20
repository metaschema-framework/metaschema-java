/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import java.util.Collection;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public interface IContainerModelGrouped extends IContainerModel {

  @Override
  IAssemblyDefinition getOwningDefinition();

  @Override
  @NonNull
  default Collection<? extends INamedModelInstanceGrouped> getModelInstances() {
    return getNamedModelInstances();
  }

  /**
   * Get all named model instances within the container.
   *
   * @return an ordered mapping of use name to model instance
   */
  @Override
  @NonNull
  Collection<? extends INamedModelInstanceGrouped> getNamedModelInstances();

  /**
   * Get the model instance contained within the model with the associated use
   * name.
   *
   * @param name
   *          the effective name of the model instance
   * @return the matching model instance, or {@code null} if no match was found
   * @see INamedModelInstance#getEffectiveName()
   */
  @Override
  @Nullable
  INamedModelInstanceGrouped getNamedModelInstanceByName(Integer name);

  /**
   * Get all field instances within the container.
   *
   * @return a mapping of use name to field instance
   */
  @Override
  @NonNull
  Collection<? extends IFieldInstanceGrouped> getFieldInstances();

  /**
   * Get the field instance contained within the model with the associated use
   * name.
   *
   * @param name
   *          the use name of the field instance
   * @return the matching field instance, or {@code null} if no match was found
   * @see IFieldInstance#getUseName()
   */
  @Override
  @Nullable
  IFieldInstanceGrouped getFieldInstanceByName(Integer name);

  /**
   * Get all assembly instances within the container.
   *
   * @return a mapping of use name to assembly instance
   */
  @Override
  @NonNull
  Collection<? extends IAssemblyInstanceGrouped> getAssemblyInstances();

  /**
   * Get the assembly instance contained within the model with the associated use
   * name.
   *
   * @param name
   *          the effective name of the assembly instance
   * @return the matching assembly instance, or {@code null} if no match was found
   * @see INamedModelInstance#getEffectiveName()
   */
  @Override
  @Nullable
  IAssemblyInstanceGrouped getAssemblyInstanceByName(Integer name);
}
