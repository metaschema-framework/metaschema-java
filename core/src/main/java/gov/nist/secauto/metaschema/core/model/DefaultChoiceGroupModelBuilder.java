/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import gov.nist.secauto.metaschema.core.model.impl.DefaultContainerModelChoiceGroupSupport;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A choice group model builder.
 * <p>
 * Is extended to support other model builders (i.e. choice and assembly model
 * builders).
 *
 * @param <NMI>
 *          the named model instance Java type
 * @param <FI>
 *          the field instance Java type
 * @param <AI>
 *          the assembly instance Java type
 * @see DefaultChoiceModelBuilder for a choice model builder
 * @see DefaultAssemblyModelBuilder for an assembly model builder
 */
@SuppressWarnings("PMD.UseConcurrentHashMap")
public class DefaultChoiceGroupModelBuilder<
    NMI extends INamedModelInstance,
    FI extends IFieldInstance,
    AI extends IAssemblyInstance> {

  // collections to store model instances
  @NonNull
  private final Map<IEnhancedQName, NMI> namedModelInstances = new LinkedHashMap<>();
  @NonNull
  private final Map<IEnhancedQName, FI> fieldInstances = new LinkedHashMap<>();
  @NonNull
  private final Map<IEnhancedQName, AI> assemblyInstances = new LinkedHashMap<>();

  /**
   * Append the instance.
   *
   * @param instance
   *          the instance to append
   */
  @SuppressWarnings("unchecked")
  public void append(@NonNull FI instance) {
    IEnhancedQName key = instance.getQName();
    namedModelInstances.put(key, (NMI) instance);
    fieldInstances.put(key, instance);
  }

  /**
   * Append the instance.
   *
   * @param instance
   *          the instance to append
   */
  @SuppressWarnings("unchecked")
  public void append(@NonNull AI instance) {
    IEnhancedQName key = instance.getQName();
    namedModelInstances.put(key, (NMI) instance);
    assemblyInstances.put(key, instance);
  }

  /**
   * Get the appended named model instances.
   *
   * @return the instances or an empty map if no instances were appended
   */
  @NonNull
  protected Map<IEnhancedQName, NMI> getNamedModelInstances() {
    return namedModelInstances;
  }

  /**
   * Get the appended field instances.
   *
   * @return the instances or an empty map if no instances were appended
   */
  @NonNull
  protected Map<IEnhancedQName, FI> getFieldInstances() {
    return fieldInstances;
  }

  /**
   * Get the appended assembly instances.
   *
   * @return the instances or an empty map if no instances were appended
   */
  @NonNull
  protected Map<IEnhancedQName, AI> getAssemblyInstances() {
    return assemblyInstances;
  }

  /**
   * Build an immutable choice group model container based on the appended
   * instances.
   *
   * @return the container
   */
  @NonNull
  public IContainerModelSupport<NMI, NMI, FI, AI> buildChoiceGroup() {
    return getNamedModelInstances().isEmpty()
        ? IContainerModelSupport.empty()
        : new DefaultContainerModelChoiceGroupSupport<>(
            CollectionUtil.unmodifiableMap(getNamedModelInstances()),
            CollectionUtil.unmodifiableMap(getFieldInstances()),
            CollectionUtil.unmodifiableMap(getAssemblyInstances()));
  }
}
