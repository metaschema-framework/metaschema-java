/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Supports model instance operations on assembly model instances.
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
public abstract class AbstractContainerModelSupport<
    MI extends IModelInstance,
    NMI extends INamedModelInstance,
    FI extends IFieldInstance,
    AI extends IAssemblyInstance>
    implements IContainerModelSupport<MI, NMI, FI, AI> {

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
  public AbstractContainerModelSupport() {
    this(
        new LinkedHashMap<>(),
        new LinkedHashMap<>(),
        new LinkedHashMap<>());
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
  protected AbstractContainerModelSupport(
      @NonNull Map<IEnhancedQName, NMI> namedModelInstances,
      @NonNull Map<IEnhancedQName, FI> fieldInstances,
      @NonNull Map<IEnhancedQName, AI> assemblyInstances) {
    this.namedModelInstances = namedModelInstances;
    this.fieldInstances = fieldInstances;
    this.assemblyInstances = assemblyInstances;
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
  @SuppressWarnings("PMD.UseConcurrentHashMap")
  protected AbstractContainerModelSupport(
      @NonNull Stream<NMI> instances,
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

    Map<IEnhancedQName, NMI> namedModelInstances = new LinkedHashMap<>();
    Map<IEnhancedQName, FI> fieldInstances = new LinkedHashMap<>();
    Map<IEnhancedQName, AI> assemblyInstances = new LinkedHashMap<>();

    instances.forEachOrdered(instance -> {
      IEnhancedQName key = instance.getQName();
      namedModelInstances.put(key, instance);

      if (fieldClass.isInstance(instance)) {
        fieldInstances.put(key, fieldClass.cast(instance));
      } else if (assemblyClass.isInstance(instance)) {
        assemblyInstances.put(key, assemblyClass.cast(instance));
      }
    });

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

  @SuppressWarnings({ "PMD.EmptyFinalizer", "checkstyle:NoFinalizer" })
  @Override
  protected final void finalize() {
    // Address SEI CERT Rule OBJ-11:
    // https://wiki.sei.cmu.edu/confluence/display/java/OBJ11-J.+Be+wary+of+letting+constructors+throw+exceptions
  }
}
