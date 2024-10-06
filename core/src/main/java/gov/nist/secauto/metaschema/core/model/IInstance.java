/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * This marker interface indicates that this object is an instance.
 */
public interface IInstance extends IModelElement {

  /**
   * Retrieve the Metaschema module definition on which the instance was declared.
   *
   * @return the Metaschema module definition on which the instance was declared
   */
  @NonNull
  IModelDefinition getContainingDefinition();

  /**
   * Get the parent model definition that serves as the container of this
   * instance.
   *
   * @return the container
   */
  @NonNull
  IContainer getParentContainer();

  // @Override
  // default IModule getContainingModule() {
  // return getContainingDefinition().getContainingModule();
  // }

  /**
   * Generates a "coordinate" string for the provided information element
   * instance.
   *
   * A coordinate consists of the element's:
   * <ul>
   * <li>containing Metaschema module's short name</li>
   * <li>model type</li>
   * <li>name</li>
   * <li>hash code</li>
   * <li>the hash code of the definition</li>
   * </ul>
   *
   * @return the coordinate
   */
  @Override
  String toCoordinates();
}
