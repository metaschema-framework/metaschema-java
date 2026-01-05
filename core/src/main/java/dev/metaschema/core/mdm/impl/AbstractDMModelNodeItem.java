/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.mdm.impl;

import dev.metaschema.core.mdm.IDMFlagNodeItem;
import dev.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import dev.metaschema.core.model.IFlagInstance;
import dev.metaschema.core.model.IModelDefinition;
import dev.metaschema.core.model.INamedModelInstance;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.ObjectUtils;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * This abstract Metapath node item implementation supports creating a
 * Metaschema module-based data model that supports child flags.
 *
 * @param <D>
 *          the Java type of the definition associated with a Metaschema module
 * @param <I>
 *          the Java type of the instance associated with a Metaschema module
 */
public abstract class AbstractDMModelNodeItem<D extends IModelDefinition, I extends INamedModelInstance>
    extends AbstractDMNodeItem
    implements IDMModelNodeItem<D, I> {
  @NonNull
  private final Map<IEnhancedQName, IDMFlagNodeItem> flags = new ConcurrentHashMap<>();

  /**
   * Construct a new node item.
   */
  protected AbstractDMModelNodeItem() {
    // only allow extending classes to create instances
  }

  @Override
  public Object getValue() {
    return this;
  }

  @Override
  public Collection<? extends IDMFlagNodeItem> getFlags() {
    return ObjectUtils.notNull(flags.values());
  }

  @Override
  public IDMFlagNodeItem getFlagByName(IEnhancedQName name) {
    return flags.get(name);
  }

  @Override
  public IDMFlagNodeItem newFlag(
      @NonNull IFlagInstance instance,
      @NonNull IAnyAtomicItem value) {
    IDMFlagNodeItem flag = new ChildFlagNodeItem(instance, this, value);
    flags.put(instance.getQName(), flag);
    return flag;
  }
}
