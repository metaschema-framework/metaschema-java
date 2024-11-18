/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.type;

import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.type.impl.AnyAtomicItemType;
import gov.nist.secauto.metaschema.core.metapath.type.impl.AnyItemType;
import gov.nist.secauto.metaschema.core.metapath.type.impl.AnyRawItemType;
import gov.nist.secauto.metaschema.core.metapath.type.impl.NodeItemType;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IItemType {
  @NonNull
  static IItemType item() {
    return AnyItemType.instance();
  }

  static IItemType function() {
    return AnyRawItemType.ANY_FUNCTION;
  }

  // static IFunctionTest function(@NonNull ISequenceType result, @NonNull
  // ISequenceType... args) {
  //
  // }

  @SuppressWarnings("unchecked")
  @NonNull
  static IItemType map() {
    return AnyRawItemType.ANY_MAP;
  }

  // static IMapTest map(@NonNull IAtomicOrUnionType key, @NonNull ISequenceType
  // value) {
  //
  // }

  @SuppressWarnings("unchecked")
  @NonNull
  static IItemType array() {
    return AnyRawItemType.ANY_ARRAY;
  }

  // @NonNull
  // static <T extends IItem> IArrayType<T> array(@NonNull ISequenceType<T> value)
  // {
  // return new ArrayTypeImpl<T, ?>(value);
  // }

  @NonNull
  static IAtomicOrUnionType anyAtomic() {
    return AnyAtomicItemType.instance();
  }

  @SuppressWarnings("unchecked")
  @NonNull
  static IItemType node() {
    return NodeItemType.ANY_NODE;
  }

  static IItemType document() {
    return NodeItemType.ANY_DOCUMENT;
  }

  static IItemType assembly() {
    return NodeItemType.ANY_ASSEMBLY;
  }

  default boolean isInstance(IItem item) {
    return getItemClass().isInstance(item);
  }

  @NonNull
  Class<? extends IItem> getItemClass();

  @NonNull
  String toSignature();

}
