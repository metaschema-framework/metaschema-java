/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.mdm.impl;

import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.item.node.AbstractInstanceNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IModelNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.model.IDefinition;
import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.core.model.INamedInstance;
import gov.nist.secauto.metaschema.core.model.IResourceLocation;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractDMInstanceNodeItem<
    D extends IDefinition,
    I extends INamedInstance,
    P extends IModelNodeItem<? extends IModelDefinition, ? extends INamedInstance>>
    extends AbstractInstanceNodeItem<D, I, P>
    implements INodeItem {
  @NonNull
  private final IResourceLocation resourceLocation;

  protected AbstractDMInstanceNodeItem(
      @NonNull I instance,
      @NonNull P parent,
      @NonNull IResourceLocation resourceLocation) {
    super(instance, parent);
    this.resourceLocation = resourceLocation;
  }

  @Override
  public IResourceLocation getLocation() {
    return resourceLocation;
  }

  @Override
  public StaticContext getStaticContext() {
    return getParentNodeItem().getStaticContext();
  }
}
