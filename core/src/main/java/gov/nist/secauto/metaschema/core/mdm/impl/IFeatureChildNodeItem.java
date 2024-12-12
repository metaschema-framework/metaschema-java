/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.mdm.impl;

import gov.nist.secauto.metaschema.core.mdm.IDMNodeItem;
import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.item.node.IModelNodeItem;
import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.core.model.INamedInstance;

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
