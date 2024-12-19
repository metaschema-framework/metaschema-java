/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.mdm.impl;

import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldInstance;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A Metapath field node item that is orphaned from a document-based data model.
 */
public class DefinitionFieldNodeItem
    extends AbstractDMFieldNodeItem {
  @NonNull
  private final IFieldDefinition definition;
  @NonNull
  private final StaticContext staticContext;

  /**
   * Construct a new node item.
   *
   * @param definition
   *          the Metaschema module definition associated with this node
   * @param value
   *          the initial field value
   * @param staticContext
   *          the static context to use when evaluating Metapath expressions
   *          against this node
   */
  public DefinitionFieldNodeItem(
      @NonNull IFieldDefinition definition,
      @NonNull IAnyAtomicItem value,
      @NonNull StaticContext staticContext) {
    super(value);
    this.definition = definition;
    this.staticContext = staticContext;
  }

  @Override
  public int getPosition() {
    return 1;
  }

  @Override
  public INodeItem getParentNodeItem() {
    // always null
    return null;
  }

  @Override
  public IAssemblyNodeItem getParentContentNodeItem() {
    // always null
    return null;
  }

  @Override
  public IFieldDefinition getDefinition() {
    return definition;
  }

  @Override
  public IFieldInstance getInstance() {
    // always null
    return null;
  }

  @Override
  public StaticContext getStaticContext() {
    return staticContext;
  }
}
