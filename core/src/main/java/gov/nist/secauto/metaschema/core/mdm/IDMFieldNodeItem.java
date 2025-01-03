/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.mdm;

import gov.nist.secauto.metaschema.core.mdm.impl.DefinitionFieldNodeItem;
import gov.nist.secauto.metaschema.core.mdm.impl.IDMModelNodeItem;
import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFieldNodeItem;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldInstance;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a Metapath field node item that is backed by a simple Metaschema
 * module-based data model.
 * <p>
 * The {@link #newInstance(IFieldDefinition, IAnyAtomicItem, StaticContext)}
 * method can be used to create a node from an {@link IAssemblyDefinition} that
 * is orphaned from a document model.
 */
public interface IDMFieldNodeItem
    extends IFieldNodeItem, IDMModelNodeItem<IFieldDefinition, IFieldInstance> {
  /**
   * Create new field node item that is detached from a parent node item.
   *
   * @param definition
   *          the Metaschema field definition describing the field
   * @param value
   *          the field's initial value
   * @param staticContext
   *          the atomic field value
   * @return the new field node item
   */
  @NonNull
  static IDMFieldNodeItem newInstance(
      @NonNull IFieldDefinition definition,
      @NonNull IAnyAtomicItem value,
      @NonNull StaticContext staticContext) {
    return new DefinitionFieldNodeItem(definition, value, staticContext);
  }
}
