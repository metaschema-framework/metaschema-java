/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.mdm.impl;

import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.model.IFlagDefinition;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A Metapath flag node item that is orphaned from a document-based data model.
 */
public class DefinitionFlagNodeItem
    extends AbstractDMFlagNodeItem {
  @NonNull
  private final IFlagDefinition definition;
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
  public DefinitionFlagNodeItem(
      @NonNull IFlagDefinition definition,
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
  public IFlagDefinition getDefinition() {
    return definition;
  }

  @Override
  public IFlagInstance getInstance() {
    // always null
    return null;
  }

  @Override
  public StaticContext getStaticContext() {
    return staticContext;
  }
}
