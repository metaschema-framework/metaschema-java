/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model;

import java.util.List;
import java.util.Map;

import dev.metaschema.core.model.impl.DefaultContainerModelAssemblySupport;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

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
 * @param <CI>
 *          the choice instance Java type
 * @param <CGI>
 *          the choice group instance Java type
 */
public interface IContainerModelAssemblySupport<
    MI extends IModelInstance,
    NMI extends INamedModelInstance,
    FI extends IFieldInstance,
    AI extends IAssemblyInstance,
    CI extends IChoiceInstance,
    CGI extends IChoiceGroupInstance> extends IContainerModelSupport<MI, NMI, FI, AI> {

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
   * @param <CI>
   *          the choice instance Java type
   * @param <CGI>
   *          the choice group instance Java type
   * @return the empty container
   */
  @SuppressWarnings("unchecked")
  @NonNull
  static <
      MI extends IModelInstance,
      NMI extends INamedModelInstance,
      FI extends IFieldInstance,
      AI extends IAssemblyInstance,
      CI extends IChoiceInstance,
      CGI extends IChoiceGroupInstance> IContainerModelAssemblySupport<MI, NMI, FI, AI, CI, CGI> empty() {
    return DefaultContainerModelAssemblySupport.EMPTY;
  }

  /**
   * Get a listing of all choice instances.
   *
   * @return the listing
   */
  @NonNull
  List<CI> getChoiceInstances();

  /**
   * Get a listing of all choice group instances.
   *
   * @return the listing
   */
  @NonNull
  Map<String, CGI> getChoiceGroupInstanceMap();

  /**
   * Get the {@code any} instance for this container, if one is defined.
   * <p>
   * An {@code any} instance represents unmodeled content that may appear within
   * an assembly's model.
   *
   * @return the {@code any} instance, or {@code null} if no {@code any} instance
   *         is defined
   */
  @Nullable
  default IAnyInstance getAnyInstance() {
    return null;
  }
}
