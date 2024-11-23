/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.type.impl;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

import java.util.Objects;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class NonAdapterAtomicItemType implements IAtomicOrUnionType {

  @NonNull
  private final Class<? extends IAnyAtomicItem> itemClass;
  @NonNull
  private final IEnhancedQName qname;

  public NonAdapterAtomicItemType(
      @NonNull Class<? extends IAnyAtomicItem> itemClass,
      @NonNull IEnhancedQName qname) {
    this.itemClass = itemClass;
    this.qname = qname;
  }

  @Override
  public Class<? extends IAnyAtomicItem> getItemClass() {
    return itemClass;
  }

  @Override
  public IEnhancedQName getQName() {
    return qname;
  }

  @Override
  public String toString() {
    return toSignature();
  }

  @Override
  @Nullable
  public IDataTypeAdapter<?> getAdapter() {
    // always null
    return null;
  }

  @Override
  public int hashCode() {
    return Objects.hash(itemClass, qname);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof NonAdapterAtomicItemType)) {
      return false;
    }
    NonAdapterAtomicItemType other = (NonAdapterAtomicItemType) obj;
    return Objects.equals(getItemClass(), other.getItemClass())
        && Objects.equals(getQName(), other.getQName());
  }
}
