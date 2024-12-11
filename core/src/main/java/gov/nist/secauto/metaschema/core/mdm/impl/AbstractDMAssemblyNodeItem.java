/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.mdm.impl;

import gov.nist.secauto.metaschema.core.mdm.IDMAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.mdm.IDMFieldNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.AbstractNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFlagNodeItem;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstance;
import gov.nist.secauto.metaschema.core.model.IFieldInstance;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.IResourceLocation;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractDMAssemblyNodeItem
    extends AbstractNodeItem
    implements IDMAssemblyNodeItem {
  @NonNull
  private final Map<IEnhancedQName, IFlagNodeItem> flags = new ConcurrentHashMap<>();
  @NonNull
  private final Map<IEnhancedQName, List<IDMModelNodeItem<?, ?>>> modelItems
      = new ConcurrentHashMap<>();

  protected AbstractDMAssemblyNodeItem() {
    // nothing to do
  }

  @Override
  public Object getValue() {
    return this;
  }

  @Override
  public String stringValue() {
    return "";
  }

  @Override
  protected String getValueSignature() {
    return "";
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
      @NonNull IResourceLocation resourceLocation,
      @NonNull IAnyAtomicItem value) {
    IFlagNodeItem flag = new FlagImpl(instance, this, resourceLocation, value);
    flags.put(instance.getQName(), flag);
    return flag;
  }

  @Override
  public Collection<List<IDMModelNodeItem<?, ?>>> getModelItems() {
    return ObjectUtils.notNull(modelItems.values());
  }

  @Override
  public List<? extends IDMModelNodeItem<?, ?>> getModelItemsByName(IEnhancedQName name) {
    List<? extends IDMModelNodeItem<?, ?>> retval = modelItems.get(name);
    return retval == null ? CollectionUtil.emptyList() : retval;
  }

  @Override
  public IDMFieldNodeItem newField(IFieldInstance instance, IResourceLocation resourceLocation, IAnyAtomicItem value) {
    List<IDMModelNodeItem<?, ?>> result = modelItems.computeIfAbsent(
        instance.getQName(),
        name -> Collections.synchronizedList(new LinkedList<IDMModelNodeItem<?, ?>>()));
    IDMFieldNodeItem field = new FieldImpl(instance, this, resourceLocation, value);
    result.add(field);
    return field;
  }

  @Override
  public IDMAssemblyNodeItem newAssembly(IAssemblyInstance instance, IResourceLocation resourceLocation) {
    List<IDMModelNodeItem<?, ?>> result = modelItems.computeIfAbsent(
        instance.getQName(),
        name -> Collections.synchronizedList(new LinkedList<IDMModelNodeItem<?, ?>>()));
    IDMAssemblyNodeItem assembly = new AssemblyImpl(instance, this, resourceLocation);
    result.add(assembly);
    return assembly;
  }
}
