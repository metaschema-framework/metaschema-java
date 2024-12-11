/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.mdm.impl;

import gov.nist.secauto.metaschema.core.mdm.IDMAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstance;
import gov.nist.secauto.metaschema.core.model.IResourceLocation;

import edu.umd.cs.findbugs.annotations.NonNull;

public class AssemblyImpl
    extends AbstractDMAssemblyNodeItem {
  @NonNull
  private final IAssemblyInstance instance;
  @NonNull
  private final IDMAssemblyNodeItem parent;
  @NonNull
  private final IResourceLocation resourceLocation;

  public AssemblyImpl(
      @NonNull IAssemblyInstance instance,
      @NonNull IDMAssemblyNodeItem parent,
      @NonNull IResourceLocation resourceLocation) {
    this.instance = instance;
    this.parent = parent;
    this.resourceLocation = resourceLocation;
  }

  @Override
  public IResourceLocation getLocation() {
    return resourceLocation;
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

  @Override
  public StaticContext getStaticContext() {
    return getParentNodeItem().getStaticContext();
  }
}
