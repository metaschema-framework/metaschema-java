/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.type;

import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;

public interface IKindTest<T extends INodeItem> extends IItemType {

  @Override
  Class<T> getItemClass();

}
