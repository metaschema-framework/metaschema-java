/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.mdm.impl;

import gov.nist.secauto.metaschema.core.mdm.IDMNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.AbstractNodeItem;
import gov.nist.secauto.metaschema.core.model.IResourceLocation;

import edu.umd.cs.findbugs.annotations.Nullable;

public abstract class AbstractDMNodeItem
    extends AbstractNodeItem
    implements IDMNodeItem {
  @Nullable
  private IResourceLocation resourceLocation = null;

  protected AbstractDMNodeItem() {
    // only allow extending classes to create instances
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
