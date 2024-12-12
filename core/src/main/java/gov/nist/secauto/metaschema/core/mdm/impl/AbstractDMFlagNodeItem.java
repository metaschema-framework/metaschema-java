/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.mdm.impl;

import gov.nist.secauto.metaschema.core.mdm.IDMFlagNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IModelNodeItem;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import java.util.Collection;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * This abstract Metapath flag node item implementation supports creating a
 * Metaschema module-based data model.
 */
public abstract class AbstractDMFlagNodeItem
    extends AbstractDMNodeItem
    implements IDMFlagNodeItem {
  @NonNull
  private IAnyAtomicItem value;

  /**
   * Construct a new node item.
   *
   * @param value
   *          the initial field value
   */
  protected AbstractDMFlagNodeItem(
      @NonNull IAnyAtomicItem value) {
    // only allow extending classes to create instances
    this.value = value;
  }

  @Override
  public IAnyAtomicItem toAtomicItem() {
    return value;
  }

  /**
   * Change the field's value to the provided value.
   *
   * @param value
   *          the new field value
   */
  public void setValue(@NonNull IAnyAtomicItem value) {
    this.value = getValueItemType().cast(value);
  }

  /**
   * Change the field's value to the provided value.
   * <p>
   * This method expects the provided value to align with the object type
   * supported by the underlying atomic type.
   *
   * @param value
   *          the new field value
   */
  public void setValue(@NonNull Object value) {
    this.value = getValueItemType().newItem(value);
  }

  @Override
  public String stringValue() {
    return toAtomicItem().asString();
  }

  @Override
  protected String getValueSignature() {
    return toAtomicItem().toSignature();
  }

  @Override
  public Collection<? extends List<? extends IModelNodeItem<?, ?>>> getModelItems() {
    // no model items
    return CollectionUtil.emptyList();
  }

  @Override
  public List<? extends IModelNodeItem<?, ?>> getModelItemsByName(IEnhancedQName name) {
    // no model items
    return CollectionUtil.emptyList();
  }

}
