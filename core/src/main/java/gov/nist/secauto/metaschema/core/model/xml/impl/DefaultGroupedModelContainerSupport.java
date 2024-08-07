/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml.impl;

import gov.nist.secauto.metaschema.core.model.IAssemblyInstanceGrouped;
import gov.nist.secauto.metaschema.core.model.IContainerModelSupport;
import gov.nist.secauto.metaschema.core.model.IFieldInstanceGrouped;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceGrouped;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Supports grouped model instance operations on assembly model instances.
 * <p>
 * This implementation uses underlying {@link LinkedHashMap} instances to
 * preserve ordering.
 * <p>
 * Since a choice group only contains named model instances (i.e., fields,
 * assemblies), model instance operations are supported by the map returned by
 * {@link #getNamedModelInstanceMap()}.
 *
 * @param <NMI>
 *          the named model instance Java type
 * @param <FI>
 *          the field instance Java type
 * @param <AI>
 *          the assembly instance Java type
 */
public class DefaultGroupedModelContainerSupport<
    NMI extends INamedModelInstanceGrouped,
    FI extends IFieldInstanceGrouped,
    AI extends IAssemblyInstanceGrouped>
    implements IContainerModelSupport<NMI, NMI, FI, AI> {

  @NonNull
  private final Map<QName, NMI> namedModelInstances;
  @NonNull
  private final Map<QName, FI> fieldInstances;
  @NonNull
  private final Map<QName, AI> assemblyInstances;

  /**
   * Construct an empty, mutable container.
   */
  @SuppressWarnings("PMD.UseConcurrentHashMap")
  public DefaultGroupedModelContainerSupport() {
    this(
        new LinkedHashMap<>(),
        new LinkedHashMap<>(),
        new LinkedHashMap<>());
  }

  /**
   * Construct an immutable container from a collection of named model instances.
   *
   * @param instances
   *          the collection of named model instances to add to the new container.
   * @param fieldClass
   *          the Java type for field instances
   * @param assemblyClass
   *          the Java type for assembly instances
   */
  @SuppressWarnings({ "PMD.UseConcurrentHashMap" })
  @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Use of final fields")
  private DefaultGroupedModelContainerSupport(
      @NonNull Collection<NMI> instances,
      @NonNull Class<FI> fieldClass,
      @NonNull Class<AI> assemblyClass) {
    assert !fieldClass.isAssignableFrom(assemblyClass) : String.format(
        "The field class '%s' must not be assignment compatible to the assembly class '%s'.",
        fieldClass.getName(),
        assemblyClass.getName());

    Map<QName, NMI> namedModelInstances = new LinkedHashMap<>();
    Map<QName, FI> fieldInstances = new LinkedHashMap<>();
    Map<QName, AI> assemblyInstances = new LinkedHashMap<>();
    for (NMI instance : instances) {
      QName key = instance.getXmlQName();
      namedModelInstances.put(key, instance);

      if (fieldClass.isInstance(instance)) {
        fieldInstances.put(key, fieldClass.cast(instance));
      } else if (assemblyClass.isInstance(instance)) {
        assemblyInstances.put(key, assemblyClass.cast(instance));
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
   * @param namedModelInstances
   *          a collection of named model instances
   * @param fieldInstances
   *          a collection of field instances
   * @param assemblyInstances
   *          a collection of assembly instances
   */
  protected DefaultGroupedModelContainerSupport(
      @NonNull Map<QName, NMI> namedModelInstances,
      @NonNull Map<QName, FI> fieldInstances,
      @NonNull Map<QName, AI> assemblyInstances) {
    this.namedModelInstances = namedModelInstances;
    this.fieldInstances = fieldInstances;
    this.assemblyInstances = assemblyInstances;
  }

  @SuppressWarnings("null")
  @Override
  public Collection<NMI> getModelInstances() {
    return namedModelInstances.values();
  }

  @Override
  public Map<QName, NMI> getNamedModelInstanceMap() {
    return namedModelInstances;
  }

  @Override
  public Map<QName, FI> getFieldInstanceMap() {
    return fieldInstances;
  }

  @Override
  public Map<QName, AI> getAssemblyInstanceMap() {
    return assemblyInstances;
  }
}
