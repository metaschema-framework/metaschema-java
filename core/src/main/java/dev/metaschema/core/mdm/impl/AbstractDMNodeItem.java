/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.mdm.impl;

import dev.metaschema.core.mdm.IDMNodeItem;
import dev.metaschema.core.metapath.item.node.AbstractNodeItem;
import dev.metaschema.core.model.IResourceLocation;

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
