/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.mdm.impl;

import gov.nist.secauto.metaschema.core.mdm.IDMFieldNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFlagNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IModelNodeItem;
import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldInstance;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.IResourceLocation;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import edu.umd.cs.findbugs.annotations.NonNull;

public class FieldImpl
    extends AbstractDMInstanceNodeItem<IFieldDefinition, IFieldInstance, IAssemblyNodeItem>
    implements IDMFieldNodeItem {
  @NonNull
  private IAnyAtomicItem value;
  @NonNull
  private final Map<IEnhancedQName, IFlagNodeItem> flags = new ConcurrentHashMap<>();

  public FieldImpl(
      @NonNull IFieldInstance instance,
      @NonNull IAssemblyNodeItem parent,
      @NonNull IResourceLocation resourceLocation,
      @NonNull IAnyAtomicItem value) {
    super(instance, parent, resourceLocation);
    this.value = value;
  }

  @Override
  public IAnyAtomicItem toAtomicItem() {
    return value;
  }

  public void setValue(@NonNull IAnyAtomicItem value) {
    this.value = getValueItemType().cast(value);
  }

  public void setValue(@NonNull Object value) {
    this.value = getValueItemType().newItem(value);
  }

  @Override
  public Object getValue() {
    return toAtomicItem().getValue();
  }

  @Override
  public String stringValue() {
    return toAtomicItem().asString();
  }

  @Override
  protected String getValueSignature() {
    return toAtomicItem().toSignature();
  }

  @Override
  public int getPosition() {
    return getParentNodeItem().getModelItemsByName(getQName()).indexOf(this);
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
  public Collection<? extends List<? extends IModelNodeItem<?, ?>>> getModelItems() {
    // no model items
    return CollectionUtil.emptyList();
  }

  @Override
  public List<? extends IModelNodeItem<?, ?>> getModelItemsByName(IEnhancedQName name) {
    // no model items
    return CollectionUtil.emptyList();
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
}
