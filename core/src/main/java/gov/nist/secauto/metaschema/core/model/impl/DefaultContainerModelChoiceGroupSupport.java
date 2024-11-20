/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.impl;

import gov.nist.secauto.metaschema.core.model.AbstractContainerModelSupport;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstance;
import gov.nist.secauto.metaschema.core.model.IFieldInstance;
import gov.nist.secauto.metaschema.core.model.INamedModelInstance;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Supports choice group model instance operations on assembly model instances.
 * <p>
 * This implementation uses underlying {@link LinkedHashMap} instances to
 * preserve ordering.
 *
 * @param <NMI>
 *          the named model instance Java type
 * @param <FI>
 *          the field instance Java type
 * @param <AI>
 *          the assembly instance Java type
 */
public class DefaultContainerModelChoiceGroupSupport<
    NMI extends INamedModelInstance,
    FI extends IFieldInstance,
    AI extends IAssemblyInstance>
    extends AbstractContainerModelSupport<NMI, NMI, FI, AI> {

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
  public DefaultContainerModelChoiceGroupSupport(
      @NonNull Map<Integer, NMI> namedModelInstances,
      @NonNull Map<Integer, FI> fieldInstances,
      @NonNull Map<Integer, AI> assemblyInstances) {
    super(namedModelInstances, fieldInstances, assemblyInstances);
  }

  @Override
  public Collection<NMI> getModelInstances() {
    return ObjectUtils.notNull(getNamedModelInstanceMap().values());
  }
}
