/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import java.util.Collection;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Indicates that the Metaschema type that has a complex model that can contain
 * field and assembly instances.
 */
public interface IContainerModel extends IContainer {

  @Override
  default boolean hasChildren() {
    return !getModelInstances().isEmpty();
  }

  /**
   * Retrieve the Metaschema module definition containing this container.
   *
   * @return the containing Metaschema module definition
   */
  @NonNull
  IAssemblyDefinition getOwningDefinition();

  /**
   * Get all model instances within the container.
   *
   * @return an ordered collection of model instances
   */
  @NonNull
  Collection<? extends IModelInstance> getModelInstances();

  /**
   * Get all named model instances within the container.
   *
   * @return an ordered mapping of use name to model instance
   */
  @NonNull
  Collection<? extends INamedModelInstance> getNamedModelInstances();

  /**
   * Get the model instance contained within the model with the associated use
   * name.
   *
   * @param name
   *          the effective name of the model instance
   * @return the matching model instance, or {@code null} if no match was found
   * @see INamedModelInstance#getEffectiveName()
   */
  @Nullable
  INamedModelInstance getNamedModelInstanceByName(QName name);

  /**
   * Get all field instances within the container.
   *
   * @return a mapping of use name to field instance
   */
  @NonNull
  Collection<? extends IFieldInstance> getFieldInstances();

  /**
   * Get the field instance contained within the model with the associated use
   * name.
   *
   * @param name
   *          the use name of the field instance
   * @return the matching field instance, or {@code null} if no match was found
   * @see IFieldInstance#getUseName()
   */
  @Nullable
  IFieldInstance getFieldInstanceByName(QName name);

  /**
   * Get all assembly instances within the container.
   *
   * @return a mapping of use name to assembly instance
   */
  @NonNull
  Collection<? extends IAssemblyInstance> getAssemblyInstances();

  /**
   * Get the assembly instance contained within the model with the associated use
   * name.
   *
   * @param name
   *          the effective name of the assembly instance
   * @return the matching assembly instance, or {@code null} if no match was found
   * @see INamedModelInstance#getEffectiveName()
   */
  @Nullable
  IAssemblyInstance getAssemblyInstanceByName(QName name);
}
