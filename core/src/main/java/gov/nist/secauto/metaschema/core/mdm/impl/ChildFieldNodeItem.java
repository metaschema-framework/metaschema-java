/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.mdm.impl;

import gov.nist.secauto.metaschema.core.mdm.IDMFieldNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldInstance;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A Metapath assembly node item that is the child of an assembly node item.
 */
public class ChildFieldNodeItem
    extends AbstractDMFieldNodeItem
    implements IDMFieldNodeItem, IFeatureChildNodeItem<IAssemblyNodeItem> {
  @NonNull
  private final IFieldInstance instance;
  @NonNull
  private final IAssemblyNodeItem parent;

  /**
   * Construct a new node item.
   *
   * @param instance
   *          the Metaschema module instance associated with this node
   * @param parent
   *          the parent node item containing this node item
   * @param value
   *          the initial field value
   */
  public ChildFieldNodeItem(
      @NonNull IFieldInstance instance,
      @NonNull IAssemblyNodeItem parent,
      @NonNull IAnyAtomicItem value) {
    super(value);
    this.instance = instance;
    this.parent = parent;
  }

  @Override
  public Object getValue() {
    return this;
  }

  @Override
  public int getPosition() {
    return getParentNodeItem().getModelItemsByName(getQName()).indexOf(this);
  }

  @Override
  public IAssemblyNodeItem getParentNodeItem() {
    return parent;
  }

  @Override
  public IAssemblyNodeItem getParentContentNodeItem() {
    return getParentNodeItem();
  }

  @Override
  public IFieldDefinition getDefinition() {
    return instance.getDefinition();
  }

  @Override
  public IFieldInstance getInstance() {
    return instance;
  }
}
