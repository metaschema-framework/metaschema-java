/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.mdm.impl;

import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstance;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A Metapath assembly node item that is orphaned from a document-based data
 * model.
 */
public class DefinitionAssemblyNodeItem
    extends AbstractDMAssemblyNodeItem {
  @NonNull
  private final IAssemblyDefinition definition;
  @NonNull
  private final StaticContext staticContext;

  /**
   * Construct a new node item.
   *
   * @param definition
   *          the Metaschema module definition associated with this node
   * @param staticContext
   *          the static context to use when evaluating Metapath expressions
   *          against this node
   */
  public DefinitionAssemblyNodeItem(
      @NonNull IAssemblyDefinition definition,
      @NonNull StaticContext staticContext) {
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
  public IAssemblyDefinition getDefinition() {
    return definition;
  }

  @Override
  public IAssemblyInstance getInstance() {
    // always null
    return null;
  }

  @Override
  public StaticContext getStaticContext() {
    return staticContext;
  }
}
