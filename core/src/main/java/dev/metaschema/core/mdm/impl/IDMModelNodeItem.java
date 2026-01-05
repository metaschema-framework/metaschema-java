/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.mdm.impl;

import dev.metaschema.core.mdm.IDMFlagNodeItem;
import dev.metaschema.core.mdm.IDMNodeItem;
import dev.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import dev.metaschema.core.metapath.item.node.IModelNodeItem;
import dev.metaschema.core.model.IFlagInstance;
import dev.metaschema.core.model.IModelDefinition;
import dev.metaschema.core.model.INamedModelInstance;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a Metapath node item that is backed by a simple Metaschema
 * module-based data model.
 * <p>
 * Implementations of this interface are expected to support child flag node
 * items.
 * <p>
 * Child flags can be created using the
 * {@link #newFlag(dev.metaschema.core.model.IFlagInstance, IAnyAtomicItem)}
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
