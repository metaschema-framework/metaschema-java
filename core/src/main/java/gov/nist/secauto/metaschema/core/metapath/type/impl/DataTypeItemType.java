/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.type.impl;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

import edu.umd.cs.findbugs.annotations.NonNull;

public class DataTypeItemType
    implements IAtomicOrUnionType {

  @NonNull
  private final IDataTypeAdapter<?> adapter;
  @NonNull
  private final Class<? extends IAnyAtomicItem> itemClass;

  public DataTypeItemType(
      @NonNull IDataTypeAdapter<?> adapter,
      @NonNull Class<? extends IAnyAtomicItem> itemClass) {
    this.adapter = adapter;
    this.itemClass = itemClass;
  }

  @Override
  @NonNull
  public IDataTypeAdapter<?> getAdapter() {
    return adapter;
  }

  @Override
  public IEnhancedQName getQName() {
    return getAdapter().getPreferredName();
  }

  @Override
  public Class<? extends IAnyAtomicItem> getItemClass() {
    return itemClass;
  }

  @Override
  public String toString() {
    return toSignature();
  }
}
