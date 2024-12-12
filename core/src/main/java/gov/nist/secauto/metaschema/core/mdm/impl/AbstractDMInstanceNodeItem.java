/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.mdm.impl;

import gov.nist.secauto.metaschema.core.mdm.IDMNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.AbstractInstanceNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IModelNodeItem;
import gov.nist.secauto.metaschema.core.model.IDefinition;
import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.core.model.INamedInstance;
import gov.nist.secauto.metaschema.core.model.IResourceLocation;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public abstract class AbstractDMInstanceNodeItem<
    D extends IDefinition,
    I extends INamedInstance,
    P extends IModelNodeItem<? extends IModelDefinition, ? extends INamedInstance>>
    extends AbstractInstanceNodeItem<D, I, P>
    implements IDMNodeItem {
  @Nullable
  private IResourceLocation resourceLocation;

  protected AbstractDMInstanceNodeItem(
      @NonNull I instance,
      @NonNull P parent) {
    super(instance, parent);
    this.resourceLocation = null;
  }

  @Override
  public IResourceLocation getLocation() {
    return resourceLocation;
  }

  @Override
  public void setLocation(IResourceLocation location) {
    this.resourceLocation = location;
  }

}
