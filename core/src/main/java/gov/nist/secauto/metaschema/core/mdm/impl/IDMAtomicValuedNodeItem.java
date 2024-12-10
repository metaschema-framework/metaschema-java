/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.mdm.impl;

import gov.nist.secauto.metaschema.core.metapath.item.node.IAtomicValuedNodeItem;

public interface IDMAtomicValuedNodeItem extends IAtomicValuedNodeItem {
  @Override
  default String stringValue() {
    return toAtomicItem().asString();
  }
}
