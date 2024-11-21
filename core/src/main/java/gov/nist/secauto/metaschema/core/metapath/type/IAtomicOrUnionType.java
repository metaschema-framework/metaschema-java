/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.type;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.type.impl.NonAdapterAtomicItemType;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IAtomicOrUnionType extends IItemType {
  @SuppressWarnings("PMD.ShortMethodName")
  @NonNull
  static IAtomicOrUnionType of(
      Class<? extends IAnyAtomicItem> itemClass,
      @NonNull IEnhancedQName qname) {
    return new NonAdapterAtomicItemType(ObjectUtils.notNull(itemClass), qname);
  }

  @NonNull
  IEnhancedQName getQName();

  /**
   * Get the data type adapter associated with this type.
   *
   * @return the adapter or {@code null} if no adapter is associated with this
   *         type, such as the case with an abstract type
   */
  IDataTypeAdapter<?> getAdapter();

  @Override
  @NonNull
  Class<? extends IAnyAtomicItem> getItemClass();

  @Override
  default String toSignature() {
    return getQName().toEQName(null);
  }
}
