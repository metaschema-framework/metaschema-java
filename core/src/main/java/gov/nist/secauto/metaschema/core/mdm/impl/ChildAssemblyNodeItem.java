/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.mdm.impl;

import gov.nist.secauto.metaschema.core.mdm.IDMAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstance;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A Metapath assembly node item that is the child of another assembly node
 * item.
 */
public class ChildAssemblyNodeItem
    extends AbstractDMAssemblyNodeItem
    implements IFeatureChildNodeItem<IDMAssemblyNodeItem> {
  @NonNull
  private final IAssemblyInstance instance;
  @NonNull
  private final IDMAssemblyNodeItem parent;

  /**
   * Construct a new node item.
   *
   * @param instance
   *          the Metaschema module instance associated with this node
   * @param parent
   *          the parent node item containing this node item
   */
  public ChildAssemblyNodeItem(
      @NonNull IAssemblyInstance instance,
      @NonNull IDMAssemblyNodeItem parent) {
    this.instance = instance;
    this.parent = parent;
  }

  @Override
  public int getPosition() {
    return getParentNodeItem().getModelItemsByName(getQName()).indexOf(this);
  }

  @Override
  @NonNull
  public IDMAssemblyNodeItem getParentNodeItem() {
    return getParentContentNodeItem();
  }

  @Override
  @NonNull
  public IDMAssemblyNodeItem getParentContentNodeItem() {
    return parent;
  }

  @Override
  public IAssemblyDefinition getDefinition() {
    return getInstance().getDefinition();
  }

  @Override
  public IAssemblyInstance getInstance() {
    return instance;
  }
}
