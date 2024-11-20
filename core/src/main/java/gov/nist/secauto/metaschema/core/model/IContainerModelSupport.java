/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import gov.nist.secauto.metaschema.core.model.impl.DefaultContainerModelSupport;

import java.util.Collection;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

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
public interface IContainerModelSupport<
    MI extends IModelInstance,
    NMI extends INamedModelInstance,
    FI extends IFieldInstance,
    AI extends IAssemblyInstance> {

  /**
   * Get an empty, immutable container.
   *
   * @param <MI>
   *          the model instance Java type
   * @param <NMI>
   *          the named model instance Java type
   * @param <FI>
   *          the field instance Java type
   * @param <AI>
   *          the assembly instance Java type
   * @return the empty container
   */
  @NonNull
  static <
      MI extends IModelInstance,
      NMI extends INamedModelInstance,
      FI extends IFieldInstance,
      AI extends IAssemblyInstance> IContainerModelSupport<MI, NMI, FI, AI> empty() {
    return DefaultContainerModelSupport.empty();
  }

  /**
   * Get a listing of all model instances.
   *
   * @return the listing
   */
  @NonNull
  Collection<MI> getModelInstances();

  /**
   * Get a mapping of all named model instances, mapped from their effective name
   * to the instance.
   *
   * @return the mapping
   */
  @NonNull
  Map<Integer, NMI> getNamedModelInstanceMap();

  /**
   * Get a mapping of all field instances, mapped from their effective name to the
   * instance.
   *
   * @return the mapping
   */
  @NonNull
  Map<Integer, FI> getFieldInstanceMap();

  /**
   * Get a mapping of all assembly instances, mapped from their effective name to
   * the instance.
   *
   * @return the mapping
   */
  @NonNull
  Map<Integer, AI> getAssemblyInstanceMap();
}
