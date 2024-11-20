/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.impl;

import gov.nist.secauto.metaschema.core.model.AbstractContainerModelSupport;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstance;
import gov.nist.secauto.metaschema.core.model.IContainerModelSupport;
import gov.nist.secauto.metaschema.core.model.IFieldInstance;
import gov.nist.secauto.metaschema.core.model.IModelInstance;
import gov.nist.secauto.metaschema.core.model.INamedModelInstance;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import java.util.LinkedHashMap;
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
    extends AbstractContainerModelSupport<MI, NMI, FI, AI> {

  @NonNull
  private final List<MI> modelInstances;

  @SuppressWarnings("rawtypes")
  @NonNull
  private static final DefaultContainerModelSupport EMPTY = new DefaultContainerModelSupport<>(
      CollectionUtil.emptyList(),
      CollectionUtil.emptyMap(),
      CollectionUtil.emptyMap(),
      CollectionUtil.emptyMap());

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
  @SuppressWarnings("unchecked")
  @NonNull
  public static <
      MI extends IModelInstance,
      NMI extends INamedModelInstance,
      FI extends IFieldInstance,
      AI extends IAssemblyInstance> IContainerModelSupport<MI, NMI, FI, AI> empty() {
    return EMPTY;
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
  @SuppressFBWarnings(value = "SING_SINGLETON_HAS_NONPRIVATE_CONSTRUCTOR", justification = "false positive")
  public DefaultContainerModelSupport(
      @NonNull List<MI> modelInstances,
      @NonNull Map<Integer, NMI> namedModelInstances,
      @NonNull Map<Integer, FI> fieldInstances,
      @NonNull Map<Integer, AI> assemblyInstances) {
    super(namedModelInstances, fieldInstances, assemblyInstances);
    this.modelInstances = modelInstances;
  }

  @Override
  public List<MI> getModelInstances() {
    return modelInstances;
  }

}
