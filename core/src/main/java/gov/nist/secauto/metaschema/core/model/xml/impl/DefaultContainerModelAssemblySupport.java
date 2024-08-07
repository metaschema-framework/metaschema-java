/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml.impl;

import gov.nist.secauto.metaschema.core.model.IAssemblyInstance;
import gov.nist.secauto.metaschema.core.model.IChoiceGroupInstance;
import gov.nist.secauto.metaschema.core.model.IChoiceInstance;
import gov.nist.secauto.metaschema.core.model.IContainerModelAssemblySupport;
import gov.nist.secauto.metaschema.core.model.IFieldInstance;
import gov.nist.secauto.metaschema.core.model.IModelInstance;
import gov.nist.secauto.metaschema.core.model.INamedModelInstance;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

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
 * @param <CI>
 *          the choice instance Java type
 * @param <CGI>
 *          the choice group instance Java type
 */
public class DefaultContainerModelAssemblySupport<
    MI extends IModelInstance,
    NMI extends INamedModelInstance,
    FI extends IFieldInstance,
    AI extends IAssemblyInstance,
    CI extends IChoiceInstance,
    CGI extends IChoiceGroupInstance>
    extends DefaultContainerModelSupport<MI, NMI, FI, AI>
    implements IContainerModelAssemblySupport<MI, NMI, FI, AI, CI, CGI> {

  @SuppressWarnings("rawtypes")
  public static final DefaultContainerModelAssemblySupport EMPTY = new DefaultContainerModelAssemblySupport<>(
      CollectionUtil.emptyList(),
      CollectionUtil.emptyMap(),
      CollectionUtil.emptyMap(),
      CollectionUtil.emptyMap(),
      CollectionUtil.emptyList(),
      CollectionUtil.emptyMap());

  @NonNull
  private final List<CI> choiceInstances;
  @NonNull
  private final Map<String, CGI> choiceGroupInstances;

  /**
   * Construct an empty, mutable container.
   */
  @SuppressWarnings("PMD.UseConcurrentHashMap")
  public DefaultContainerModelAssemblySupport() {
    this(
        new LinkedList<>(),
        new LinkedHashMap<>(),
        new LinkedHashMap<>(),
        new LinkedHashMap<>(),
        new LinkedList<>(),
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
   * @param choiceClass
   *          the Java type for choice instances
   * @param choiceGroupClass
   *          the Java type for choice group instances
   */
  @SuppressWarnings({ "PMD.UseConcurrentHashMap" })
  public DefaultContainerModelAssemblySupport(
      @NonNull Collection<MI> instances,
      @NonNull Class<NMI> namedModelClass,
      @NonNull Class<FI> fieldClass,
      @NonNull Class<AI> assemblyClass,
      @NonNull Class<CI> choiceClass,
      @NonNull Class<CGI> choiceGroupClass) {
    super(instances, namedModelClass, fieldClass, assemblyClass);
    List<CI> choiceInstances = new LinkedList<>();
    Map<String, CGI> choiceGroupInstances = new LinkedHashMap<>();
    for (MI instance : instances) {
      if (choiceClass.isInstance(instance)) {
        CI choice = choiceClass.cast(instance);
        choiceInstances.add(choice);
      } else if (choiceGroupClass.isInstance(instance)) {
        CGI choiceGroup = choiceGroupClass.cast(instance);
        String key = ObjectUtils.requireNonNull(choiceGroup.getGroupAsName());
        choiceGroupInstances.put(key, choiceGroup);
      }
    }

    this.choiceInstances = choiceInstances.isEmpty()
        ? CollectionUtil.emptyList()
        : CollectionUtil.unmodifiableList(choiceInstances);
    this.choiceGroupInstances = choiceGroupInstances.isEmpty()
        ? CollectionUtil.emptyMap()
        : CollectionUtil.unmodifiableMap(choiceGroupInstances);
  }

  /**
   * Construct an new container using the provided collections.
   *
   * @param instances
   *          a collection of model instances
   * @param namedModelInstances
   *          a collection of named model instances
   * @param fieldInstances
   *          a collection of field instances
   * @param assemblyInstances
   *          a collection of assembly instances
   * @param choiceInstances
   *          a collection of choice instances
   * @param choiceGroupInstances
   *          a collection of choice group instances
   */
  protected DefaultContainerModelAssemblySupport(
      @NonNull List<MI> instances,
      @NonNull Map<QName, NMI> namedModelInstances,
      @NonNull Map<QName, FI> fieldInstances,
      @NonNull Map<QName, AI> assemblyInstances,
      @NonNull List<CI> choiceInstances,
      @NonNull Map<String, CGI> choiceGroupInstances) {
    super(instances, namedModelInstances, fieldInstances, assemblyInstances);
    this.choiceInstances = choiceInstances;
    this.choiceGroupInstances = choiceGroupInstances;
  }

  @Override
  public List<CI> getChoiceInstances() {
    return choiceInstances;
  }

  @Override
  public Map<String, CGI> getChoiceGroupInstanceMap() {
    return choiceGroupInstances;
  }
}
