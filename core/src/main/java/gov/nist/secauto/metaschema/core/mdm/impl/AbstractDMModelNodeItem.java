/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.mdm.impl;

import gov.nist.secauto.metaschema.core.mdm.IDMFlagNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.core.model.INamedModelInstance;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

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
