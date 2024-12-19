/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.mdm.impl;

import gov.nist.secauto.metaschema.core.mdm.IDMFlagNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.AbstractNodeItem;
import gov.nist.secauto.metaschema.core.model.IFlagDefinition;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.IResourceLocation;

import edu.umd.cs.findbugs.annotations.NonNull;

public class ChildFlagNodeItem
    extends AbstractNodeItem
    implements IDMFlagNodeItem, IFeatureChildNodeItem<IDMModelNodeItem<?, ?>> {
  @NonNull
  private final IFlagInstance instance;
  @NonNull
  private final IDMModelNodeItem<?, ?> parent;
  @NonNull
  private IAnyAtomicItem value;

  public ChildFlagNodeItem(
      @NonNull IFlagInstance instance,
      @NonNull IDMModelNodeItem<?, ?> parent,
      @NonNull IAnyAtomicItem value) {
    this.instance = instance;
    this.parent = parent;
    this.value = value;
  }

  @Override
  public IDMModelNodeItem<?, ?> getParentNodeItem() {
    return parent;
  }

  @Override
  public IAnyAtomicItem toAtomicItem() {
    return value;
  }

  @Override
  public Object getValue() {
    return this;
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
  public IFlagDefinition getDefinition() {
    return getInstance().getDefinition();
  }

  @Override
  public IFlagInstance getInstance() {
    return instance;
  }

  @Override
  public void setLocation(IResourceLocation location) {
    // TODO Auto-generated method stub

  }
}
