/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.mdm;

import gov.nist.secauto.metaschema.core.mdm.impl.DefinitionAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.mdm.impl.IDMModelNodeItem;
import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstance;
import gov.nist.secauto.metaschema.core.model.IFieldInstance;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An assembly node item implementation that is backed by a simple Metaschema
 * module-based data model.
 */
public interface IDMAssemblyNodeItem
    extends IAssemblyNodeItem, IDMModelNodeItem<IAssemblyDefinition, IAssemblyInstance> {
  /**
   * Create new assembly node item that is detached from a parent node item.
   *
   * @param definition
   *          the Metaschema field definition describing the assembly
   * @param staticContext
   *          the atomic field value
   * @return the new field node item
   */
  @NonNull
  static IDMAssemblyNodeItem newInstance(
      @NonNull IAssemblyDefinition definition,
      @NonNull StaticContext staticContext) {
    return new DefinitionAssemblyNodeItem(definition, staticContext);
  }

  /**
   * Create and add a new field to the underlying data model.
   *
   * @param instance
   *          the Metaschema field instance describing the field
   * @param value
   *          the atomic field value
   * @return the new field node item
   */
  @NonNull
  IDMFieldNodeItem newField(
      @NonNull IFieldInstance instance,
      @NonNull IAnyAtomicItem value);

  /**
   * Create and add a new assembly to the underlying data model.
   *
   * @param instance
   *          the Metaschema assembly instance describing the assembly
   * @return the new assembly node item
   */
  @NonNull
  IDMAssemblyNodeItem newAssembly(
      @NonNull IAssemblyInstance instance);
}
