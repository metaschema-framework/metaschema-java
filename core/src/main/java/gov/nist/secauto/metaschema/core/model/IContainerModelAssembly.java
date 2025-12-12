/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import java.util.List;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents a model container for assembly definitions.
 * <p>
 * Assembly containers extend absolute containers by adding support for choice
 * instances and choice group instances, which allow for alternative content
 * models within the assembly.
 */
public interface IContainerModelAssembly extends IContainerModelAbsolute {

  /**
   * Get all choice instances within the container.
   *
   * @return a list of choice instances
   */
  @NonNull
  List<? extends IChoiceInstance> getChoiceInstances();

  /**
   * Get the choice group model instance contained within the model with the
   * associated group as name.
   *
   * @param name
   *          the group as name of the choice group instance
   * @return the matching choice group instance, or {@code null} if no match was
   *         found
   * @see IChoiceGroupInstance#getGroupAsName()
   */
  @Nullable
  IChoiceGroupInstance getChoiceGroupInstanceByName(String name);

  /**
   * Get all choice group instances within the container.
   *
   * @return a list of choice instances
   */
  @NonNull
  Map<String, ? extends IChoiceGroupInstance> getChoiceGroupInstances();
}
