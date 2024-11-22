/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml.impl;

import gov.nist.secauto.metaschema.core.model.IAssemblyInstance;
import gov.nist.secauto.metaschema.core.model.IContainerModelSupport;
import gov.nist.secauto.metaschema.core.model.IFieldInstance;
import gov.nist.secauto.metaschema.core.model.IModelInstance;
import gov.nist.secauto.metaschema.core.model.INamedModelInstance;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Provides model container support.
 * <p>
 * This class supports generic model instance operations on model instances.
 * <p>
 * This implementation uses underlying {@link LinkedHashMap} instances to
 * preserve ordering.
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
public class DefaultContainerModelSupport<
    MI extends IModelInstance,
    NMI extends INamedModelInstance,
    FI extends IFieldInstance,
    AI extends IAssemblyInstance>
    implements IContainerModelSupport<MI, NMI, FI, AI> {

  @SuppressWarnings("rawtypes")
  public static final DefaultContainerModelSupport EMPTY = new DefaultContainerModelSupport<>(
      CollectionUtil.emptyList(),
      CollectionUtil.emptyMap(),
      CollectionUtil.emptyMap(),
      CollectionUtil.emptyMap());

  @NonNull
  private final List<MI> modelInstances;
  @NonNull
  private final Map<IEnhancedQName, NMI> namedModelInstances;
  @NonNull
  private final Map<IEnhancedQName, FI> fieldInstances;
  @NonNull
  private final Map<IEnhancedQName, AI> assemblyInstances;

  /**
   * Construct an empty, mutable container.
   */
  @SuppressWarnings("PMD.UseConcurrentHashMap")
  public DefaultContainerModelSupport() {
    this(
        new LinkedList<>(),
        new LinkedHashMap<>(),
        new LinkedHashMap<>(),
        new LinkedHashMap<>());
  }

  /**
   * Construct an immutable container from a collection of model instances.
   *
   * @param instances
   *          the collection of model instances to add to the new container.
   * @param namedModelClass
   *          the Java type for named model instances
   * @param fieldClass
   *          the Java type for field instances
   * @param assemblyClass
   *          the Java type for assembly instances
   */
  @SuppressWarnings({ "PMD.UseConcurrentHashMap" })
  @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Use of final fields")
  protected DefaultContainerModelSupport(
      @NonNull Collection<MI> instances,
      @NonNull Class<NMI> namedModelClass,
      @NonNull Class<FI> fieldClass,
      @NonNull Class<AI> assemblyClass) {
    assert namedModelClass.isAssignableFrom(fieldClass) : String.format(
        "The field class '%s' is not assignment compatible to class '%s'.",
        fieldClass.getName(),
        namedModelClass.getName());
    assert namedModelClass.isAssignableFrom(assemblyClass) : String.format(
        "The assembly class '%s' is not assignment compatible to class '%s'.",
        assemblyClass.getName(),
        namedModelClass.getName());
    assert !fieldClass.isAssignableFrom(assemblyClass) : String.format(
        "The field class '%s' must not be assignment compatible to the assembly class '%s'.",
        fieldClass.getName(),
        assemblyClass.getName());

    this.modelInstances = ObjectUtils.notNull(List.copyOf(instances));

    Map<IEnhancedQName, NMI> namedModelInstances = new LinkedHashMap<>();
    Map<IEnhancedQName, FI> fieldInstances = new LinkedHashMap<>();
    Map<IEnhancedQName, AI> assemblyInstances = new LinkedHashMap<>();
    for (MI instance : instances) {
      if (namedModelClass.isInstance(instance)) {
        NMI named = namedModelClass.cast(instance);
        IEnhancedQName key = named.getXmlQName();
        namedModelInstances.put(key, named);

        if (fieldClass.isInstance(instance)) {
          fieldInstances.put(key, fieldClass.cast(named));
        } else if (assemblyClass.isInstance(instance)) {
          assemblyInstances.put(key, assemblyClass.cast(named));
        }
      }
    }

    this.namedModelInstances = namedModelInstances.isEmpty()
        ? CollectionUtil.emptyMap()
        : CollectionUtil.unmodifiableMap(namedModelInstances);
    this.fieldInstances = fieldInstances.isEmpty()
        ? CollectionUtil.emptyMap()
        : CollectionUtil.unmodifiableMap(fieldInstances);
    this.assemblyInstances = assemblyInstances.isEmpty()
        ? CollectionUtil.emptyMap()
        : CollectionUtil.unmodifiableMap(assemblyInstances);
  }

  /**
   * Construct an new container using the provided collections.
   *
   * @param modelInstances
   *          a collection of model instances
   * @param namedModelInstances
   *          a collection of named model instances
   * @param fieldInstances
   *          a collection of field instances
   * @param assemblyInstances
   *          a collection of assembly instances
   */
  protected DefaultContainerModelSupport(
      @NonNull List<MI> modelInstances,
      @NonNull Map<IEnhancedQName, NMI> namedModelInstances,
      @NonNull Map<IEnhancedQName, FI> fieldInstances,
      @NonNull Map<IEnhancedQName, AI> assemblyInstances) {
    this.modelInstances = modelInstances;
    this.namedModelInstances = namedModelInstances;
    this.fieldInstances = fieldInstances;
    this.assemblyInstances = assemblyInstances;
  }

  @Override
  public List<MI> getModelInstances() {
    return modelInstances;
  }

  @Override
  public Map<IEnhancedQName, NMI> getNamedModelInstanceMap() {
    return namedModelInstances;
  }

  @Override
  public Map<IEnhancedQName, FI> getFieldInstanceMap() {
    return fieldInstances;
  }

  @Override
  public Map<IEnhancedQName, AI> getAssemblyInstanceMap() {
    return assemblyInstances;
  }
}
