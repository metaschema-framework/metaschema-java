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
 * Represents a Metapath assembly node item that is backed by a simple
 * Metaschema module-based data model.
 * <p>
 * The {@link #newInstance(IAssemblyDefinition, StaticContext)} method can be
 * used to create a node from an {@link IAssemblyDefinition} that is orphaned
 * from a document model.
 * <p>
 * Child nodes can be created using the
 * {@link #newFlag(gov.nist.secauto.metaschema.core.model.IFlagInstance, IAnyAtomicItem)},
 * {@link #newAssembly(IAssemblyInstance)}, and
 * {@link #newField(IFieldInstance, IAnyAtomicItem)} methods. These children are
 * added to this assembly.
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
