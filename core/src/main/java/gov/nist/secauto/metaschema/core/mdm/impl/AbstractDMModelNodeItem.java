/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.mdm.impl;

import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFlagNodeItem;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.core.model.INamedModelInstance;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractDMModelNodeItem<D extends IModelDefinition, I extends INamedModelInstance>
    extends AbstractDMNodeItem
    implements IDMModelNodeItem<D, I> {
  @NonNull
  private final Map<IEnhancedQName, IFlagNodeItem> flags = new ConcurrentHashMap<>();

  protected AbstractDMModelNodeItem() {
    // only allow extending classes to create instances
  }

  @Override
  public Object getValue() {
    return this;
  }

  @Override
  public Collection<? extends IFlagNodeItem> getFlags() {
    return ObjectUtils.notNull(flags.values());
  }

  @Override
  public IFlagNodeItem getFlagByName(IEnhancedQName name) {
    return flags.get(name);
  }

  @Override
  public IFlagNodeItem newFlag(
      @NonNull IFlagInstance instance,
      @NonNull IAnyAtomicItem value) {
    IFlagNodeItem flag = new ChildFlagNodeItem(instance, this, value);
    flags.put(instance.getQName(), flag);
    return flag;
  }
}
