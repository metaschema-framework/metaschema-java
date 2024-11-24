/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.type;

import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;

/**
 * Provides type information that be used to discover type information for,
 * test, and cast various node-based item objects.
 *
 * @param <T>
 *          the Java type of the node-based item supported by the implementation
 */
public interface IKindTest<T extends INodeItem> extends IItemType {
  @Override
  Class<T> getItemClass();
}
