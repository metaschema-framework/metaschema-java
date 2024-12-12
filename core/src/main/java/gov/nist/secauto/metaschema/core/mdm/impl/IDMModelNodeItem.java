/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.mdm.impl;

import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFlagNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IModelNodeItem;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.core.model.INamedModelInstance;
import gov.nist.secauto.metaschema.core.model.IResourceLocation;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IDMModelNodeItem<D extends IModelDefinition, I extends INamedModelInstance>
    extends IModelNodeItem<D, I> {
  @NonNull
  IFlagNodeItem newFlag(
      @NonNull IFlagInstance instance,
      @NonNull IResourceLocation resourceLocation,
      @NonNull IAnyAtomicItem value);
}
