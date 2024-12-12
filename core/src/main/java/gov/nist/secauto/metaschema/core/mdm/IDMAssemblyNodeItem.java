/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.mdm;

import gov.nist.secauto.metaschema.core.mdm.impl.IDMModelNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstance;
import gov.nist.secauto.metaschema.core.model.IFieldInstance;
import gov.nist.secauto.metaschema.core.model.IResourceLocation;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An assembly node item implementation that is backed by a simple Metaschema
 * module-based data model.
 */
public interface IDMAssemblyNodeItem
    extends IAssemblyNodeItem, IDMModelNodeItem<IAssemblyDefinition, IAssemblyInstance> {
  /**
   * Create and add a new field to the underlying data model.
   *
   * @param instance
   *          the Metaschema field instance describing the field
   * @param resourceLocation
   *          information about the location of the field within the containing
   *          resource
   * @param value
   *          the atomic field value
   * @return the new field node item
   */
  @NonNull
  IDMFieldNodeItem newField(
      @NonNull IFieldInstance instance,
      @NonNull IResourceLocation resourceLocation,
      @NonNull IAnyAtomicItem value);

  /**
   * Create and add a new assembly to the underlying data model.
   *
   * @param instance
   *          the Metaschema assembly instance describing the assembly
   * @param resourceLocation
   *          information about the location of the assembly within the containing
   *          resource
   * @return the new assembly node item
   */
  @NonNull
  IDMAssemblyNodeItem newAssembly(
      @NonNull IAssemblyInstance instance,
      @NonNull IResourceLocation resourceLocation);
}
