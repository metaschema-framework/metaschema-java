/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.mdm.impl;

import dev.metaschema.core.mdm.IDMNodeItem;
import dev.metaschema.core.metapath.StaticContext;
import dev.metaschema.core.metapath.item.node.IModelNodeItem;
import dev.metaschema.core.model.IModelDefinition;
import dev.metaschema.core.model.INamedInstance;

/**
 * This feature identifies the implementing class as a node item that has a node
 * item parent, providing default methods required by all child node items.
 *
 * @param <P>
 *          the Java type of the parent node item
 */
public interface IFeatureChildNodeItem<P extends IModelNodeItem<? extends IModelDefinition, ? extends INamedInstance>>
    extends IDMNodeItem {

  @Override
  P getParentNodeItem();

  @Override
  default P getParentContentNodeItem() {
    return getParentNodeItem();
  }

  @Override
  default StaticContext getStaticContext() {
    return getParentNodeItem().getStaticContext();
  }
}
