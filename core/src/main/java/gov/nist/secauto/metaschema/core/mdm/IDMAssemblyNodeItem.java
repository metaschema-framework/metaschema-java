/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.mdm;

import gov.nist.secauto.metaschema.core.mdm.impl.IDMInstanceNodeItem;
import gov.nist.secauto.metaschema.core.mdm.impl.IDMModelNodeItem;
import gov.nist.secauto.metaschema.core.metapath.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.node.IAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstance;
import gov.nist.secauto.metaschema.core.model.IFieldInstance;
import gov.nist.secauto.metaschema.core.model.IResourceLocation;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IDMAssemblyNodeItem
    extends IAssemblyNodeItem, IDMModelNodeItem<IAssemblyDefinition, IAssemblyInstance>, IDMInstanceNodeItem {
  @NonNull
  IDMFieldNodeItem newField(
      @NonNull IFieldInstance instance,
      @NonNull IResourceLocation resourceLocation,
      @NonNull IAnyAtomicItem value);

  @NonNull
  IDMAssemblyNodeItem newAssembly(
      @NonNull IAssemblyInstance instance,
      @NonNull IResourceLocation resourceLocation);
}
