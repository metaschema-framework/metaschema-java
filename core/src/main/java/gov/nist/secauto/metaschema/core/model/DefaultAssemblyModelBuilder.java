/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import gov.nist.secauto.metaschema.core.model.impl.DefaultContainerModelAssemblySupport;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An assembly model builder.
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
 * @see DefaultChoiceGroupModelBuilder for a choice group model builder
 * @see DefaultChoiceModelBuilder for a choice model builder
 */
@SuppressWarnings("PMD.UseConcurrentHashMap")
public class DefaultAssemblyModelBuilder<
    MI extends IModelInstance,
    NMI extends INamedModelInstance,
    FI extends IFieldInstance,
    AI extends IAssemblyInstance,
    CI extends IChoiceInstance,
    CGI extends IChoiceGroupInstance>
    extends DefaultChoiceModelBuilder<MI, NMI, FI, AI> {
  // collections to store model instances
  @NonNull
  private final List<CI> choiceInstances = new LinkedList<>();
  @NonNull
  private final Map<String, CGI> choiceGroupInstances = new LinkedHashMap<>();

  /**
   * Append the instance.
   *
   * @param instance
   *          the instance to append
   */
  @SuppressWarnings("unchecked")
  public void append(@NonNull CI instance) {
    getModelInstances().add((MI) instance);
    choiceInstances.add(instance);
  }

  /**
   * Append the instance.
   *
   * @param instance
   *          the instance to append
   */
  @SuppressWarnings("unchecked")
  public void append(@NonNull CGI instance) {
    getModelInstances().add((MI) instance);
    choiceGroupInstances.put(instance.getGroupAsName(), instance);
  }

  /**
   * Get the appended choice instances.
   *
   * @return the instances or an empty list if no instances were appended
   */
  @NonNull
  protected List<CI> getChoiceInstances() {
    return choiceInstances;
  }

  /**
   * Get the appended choice group instances.
   *
   * @return the instances or an empty map if no instances were appended
   */
  @NonNull
  protected Map<String, CGI> getChoiceGroupInstances() {
    return choiceGroupInstances;
  }

  /**
   * Build an immutable assembly model container based on the appended instances.
   *
   * @return the container
   */
  @NonNull
  public IContainerModelAssemblySupport<MI, NMI, FI, AI, CI, CGI> buildAssembly() {
    return getModelInstances().isEmpty()
        ? IContainerModelAssemblySupport.empty()
        : new DefaultContainerModelAssemblySupport<>(
            CollectionUtil.unmodifiableList(getModelInstances()),
            CollectionUtil.unmodifiableMap(getNamedModelInstances()),
            CollectionUtil.unmodifiableMap(getFieldInstances()),
            CollectionUtil.unmodifiableMap(getAssemblyInstances()),
            CollectionUtil.unmodifiableList(getChoiceInstances()),
            CollectionUtil.unmodifiableMap(getChoiceGroupInstances()));
  }
}
