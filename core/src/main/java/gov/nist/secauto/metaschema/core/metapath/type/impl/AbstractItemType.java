/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.type.impl;

import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.type.IItemType;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractItemType<I extends IItem> implements IItemType {
  @NonNull
  private final Class<? extends I> itemClass;

  protected AbstractItemType(@NonNull Class<? extends I> itemClass) {
    this.itemClass = itemClass;
  }

  @Override
  public Class<? extends I> getItemClass() {
    return itemClass;
  }
}
