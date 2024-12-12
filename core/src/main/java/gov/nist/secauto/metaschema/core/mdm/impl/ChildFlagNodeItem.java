/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.mdm.impl;

import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.model.IFlagDefinition;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A Metapath assembly node item that is the child of an assembly or field node
 * item.
 */
public class ChildFlagNodeItem
    extends AbstractDMFlagNodeItem
    implements IFeatureChildNodeItem<IDMModelNodeItem<?, ?>> {
  @NonNull
  private final IFlagInstance instance;
  @NonNull
  private final IDMModelNodeItem<?, ?> parent;

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
  public ChildFlagNodeItem(
      @NonNull IFlagInstance instance,
      @NonNull IDMModelNodeItem<?, ?> parent,
      @NonNull IAnyAtomicItem value) {
    super(value);
    this.instance = instance;
    this.parent = parent;
  }

  @Override
  public IDMModelNodeItem<?, ?> getParentNodeItem() {
    return parent;
  }

  @Override
  public Object getValue() {
    return this;
  }

  @Override
  public IFlagDefinition getDefinition() {
    return getInstance().getDefinition();
  }

  @Override
  public IFlagInstance getInstance() {
    return instance;
  }
}
