/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.mdm;

import gov.nist.secauto.metaschema.core.metapath.node.IAtomicValuedNodeItem;
import gov.nist.secauto.metaschema.core.metapath.node.IFlagNodeItem;

/**
 * Represents a Metapath flag node item that is backed by a simple Metaschema
 * module-based data model.
 */
public interface IDMFlagNodeItem extends IFlagNodeItem, IDMNodeItem, IAtomicValuedNodeItem {
  // no additional methods
}
