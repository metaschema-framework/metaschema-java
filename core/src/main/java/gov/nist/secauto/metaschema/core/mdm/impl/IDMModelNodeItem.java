/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.mdm.impl;

import gov.nist.secauto.metaschema.core.mdm.IDMFlagNodeItem;
import gov.nist.secauto.metaschema.core.mdm.IDMNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IModelNodeItem;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.core.model.INamedModelInstance;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a Metapath node item that is backed by a simple Metaschema
 * module-based data model.
 * <p>
 * Implementations of this interface are expected to support child flag node
 * items.
 * <p>
 * Child flags can be created using the
 * {@link #newFlag(gov.nist.secauto.metaschema.core.model.IFlagInstance, IAnyAtomicItem)}
 * method. These children are added to this assembly.
 *
 * @param <D>
 *          the Java type of the definition associated with a Metaschema module
 * @param <I>
 *          the Java type of the instance associated with a Metaschema module
 */
public interface IDMModelNodeItem<D extends IModelDefinition, I extends INamedModelInstance>
    extends IModelNodeItem<D, I>, IDMNodeItem {
  /**
   * Create and add a new flag to the underlying data model.
   *
   * @param instance
   *          the Metaschema flag instance describing the field
   * @param value
   *          the atomic flag value
   * @return the new flag node item
   */
  @NonNull
  IDMFlagNodeItem newFlag(
      @NonNull IFlagInstance instance,
      @NonNull IAnyAtomicItem value);
}
