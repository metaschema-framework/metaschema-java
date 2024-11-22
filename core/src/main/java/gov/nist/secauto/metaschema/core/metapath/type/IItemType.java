/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.type;

import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.type.impl.AnyItemType;
import gov.nist.secauto.metaschema.core.metapath.type.impl.AnyRawItemType;
import gov.nist.secauto.metaschema.core.metapath.type.impl.ArrayTestImpl;
import gov.nist.secauto.metaschema.core.metapath.type.impl.MapTestImpl;
import gov.nist.secauto.metaschema.core.metapath.type.impl.NodeItemType;
import gov.nist.secauto.metaschema.core.metapath.type.impl.TypeConstants;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

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

  @NonNull
  static IItemType map() {
    return AnyRawItemType.ANY_MAP;
  }

  @NonNull
  static IMapTest map(@NonNull IAtomicOrUnionType key, @NonNull ISequenceType value) {
    return new MapTestImpl(key, value);
  }

  @NonNull
  static IItemType array() {
    return AnyRawItemType.ANY_ARRAY;
  }

  @NonNull
  static IItemType array(@NonNull ISequenceType value) {
    return new ArrayTestImpl(value);
  }

  @NonNull
  static IAtomicOrUnionType anyAtomic() {
    return TypeConstants.ANY_ATOMIC_TYPE;
  }

  @NonNull
  static IItemType node() {
    return NodeItemType.ANY_NODE;
  }

  @NonNull
  static IItemType module() {
    return NodeItemType.ANY_MODULE;
  }

  @NonNull
  static IItemType document() {
    return NodeItemType.ANY_DOCUMENT;
  }

  @NonNull
  static IItemType assembly() {
    return NodeItemType.ANY_ASSEMBLY;
  }

  @NonNull
  static IItemType field() {
    return NodeItemType.ANY_FIELD;
  }

  @NonNull
  static IItemType flag() {
    return NodeItemType.ANY_FLAG;
  }

  @NonNull
  static IItemType flag(IEnhancedQName name, IAtomicOrUnionType dataType) {
    throw new UnsupportedOperationException();
  }

  default boolean isInstance(IItem item) {
    return getItemClass().isInstance(item);
  }

  @NonNull
  Class<? extends IItem> getItemClass();

  @NonNull
  String toSignature();
}
