/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.mdm.impl;

import gov.nist.secauto.metaschema.core.mdm.IDMNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.AbstractNodeItem;
import gov.nist.secauto.metaschema.core.model.IResourceLocation;

import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * This abstract Metapath assmebly node item implementation supports creating a
 * Metaschema module-based data model.
 */
public abstract class AbstractDMNodeItem
    extends AbstractNodeItem
    implements IDMNodeItem {
  @Nullable
  private IResourceLocation resourceLocation; // null

  /**
   * Construct a new node item.
   */
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
