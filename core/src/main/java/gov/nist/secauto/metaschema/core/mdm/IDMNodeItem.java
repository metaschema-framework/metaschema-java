/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.mdm;

import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.model.IResourceLocation;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a Metapath node item that is backed by a simple Metaschema
 * module-based data model.
 */
public interface IDMNodeItem extends INodeItem {
  /**
   * Provides a means to change the location information for the node item.
   *
   * @param location
   *          information about the location of the node within the containing
   *          resource
   */
  void setLocation(@NonNull IResourceLocation location);
}
