/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import gov.nist.secauto.metaschema.core.model.impl.DefaultContainerModelSupport;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import java.util.LinkedList;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A choice model builder.
 * <p>
 * Is extended to support other model builders (i.e. assembly model builders).
 *
 * @param <MI>
 *          the model instance Java type
 * @param <NMI>
 *          the named model instance Java type
 * @param <FI>
 *          the field instance Java type
 * @param <AI>
 *          the assembly instance Java type
 * @see DefaultChoiceGroupModelBuilder for a choice group model builder
 * @see DefaultAssemblyModelBuilder for an assembly model builder
 */
public class DefaultChoiceModelBuilder<
    MI extends IModelInstance,
    NMI extends INamedModelInstance,
    FI extends IFieldInstance,
    AI extends IAssemblyInstance>
    extends DefaultChoiceGroupModelBuilder<NMI, FI, AI> {
  // collections to store model instances
  @NonNull
  private final List<MI> modelInstances = new LinkedList<>();

  @SuppressWarnings("unchecked")
  @Override
  public void append(FI instance) {
    modelInstances.add((MI) instance);
    super.append(instance);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void append(AI instance) {
    modelInstances.add((MI) instance);
    super.append(instance);
  }

  /**
   * Get the appended model instances.
   *
   * @return the instances or an empty list if no instances were appended
   */
  @NonNull
  public List<MI> getModelInstances() {
    return modelInstances;
  }

  /**
   * Build an immutable choice model container based on the appended instances.
   *
   * @return the container
   */
  @NonNull
  public IContainerModelSupport<MI, NMI, FI, AI> buildChoice() {
    return getModelInstances().isEmpty()
        ? IContainerModelSupport.empty()
        : new DefaultContainerModelSupport<>(
            CollectionUtil.unmodifiableList(getModelInstances()),
            CollectionUtil.unmodifiableMap(getNamedModelInstances()),
            CollectionUtil.unmodifiableMap(getFieldInstances()),
            CollectionUtil.unmodifiableMap(getAssemblyInstances()));
  }
}
