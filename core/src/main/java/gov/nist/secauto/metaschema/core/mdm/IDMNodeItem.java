/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.mdm;

import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.model.IResourceLocation;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IDMNodeItem extends INodeItem {
  /**
   *
   * @param location
   *          information about the location of the node within the containing
   *          resource
   */
  void setLocation(@NonNull IResourceLocation location);
}
