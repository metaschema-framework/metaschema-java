/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.type.impl;

import dev.metaschema.core.metapath.MetapathConstants;
import dev.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import dev.metaschema.core.metapath.item.atomic.IDurationItem;
import dev.metaschema.core.metapath.item.atomic.IIPAddressItem;
import dev.metaschema.core.metapath.item.atomic.INumericItem;
import dev.metaschema.core.metapath.type.IAtomicOrUnionType;
import dev.metaschema.core.qname.EQNameFactory;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides static instances for all abstract atomic types.
 */
@SuppressWarnings("PMD.DataClass")
public final class TypeConstants {

  /**
   * The Metaschema data type that represents all atomic types.
   */
  @NonNull
  public static final IAtomicOrUnionType<IAnyAtomicItem> ANY_ATOMIC_TYPE
      = IAtomicOrUnionType.of(
          IAnyAtomicItem.class,
          IAnyAtomicItem::cast,
          EQNameFactory.instance().newQName(MetapathConstants.NS_METAPATH, "any-atomic-type"));
  /**
   * The Metaschema data type that represents all duration types.
   */
  @NonNull
  public static final IAtomicOrUnionType<IDurationItem> DURATION_TYPE
      = IAtomicOrUnionType.of(
          IDurationItem.class,
          IDurationItem::cast,
          EQNameFactory.instance().newQName(MetapathConstants.NS_METAPATH, "duration"));
  /**
   * The Metaschema data type that represents all IP address types.
   */
  @NonNull
  public static final IAtomicOrUnionType<IIPAddressItem> IP_ADDRESS_TYPE
      = IAtomicOrUnionType.of(
          IIPAddressItem.class,
          IIPAddressItem::cast,
          EQNameFactory.instance().newQName(MetapathConstants.NS_METAPATH, "ip-address"));
  /**
   * The Metaschema data type that represents all numeric types.
   */
  @NonNull
  public static final IAtomicOrUnionType<INumericItem> NUMERIC_TYPE
      = IAtomicOrUnionType.of(
          INumericItem.class,
          INumericItem::cast,
          EQNameFactory.instance().newQName(MetapathConstants.NS_METAPATH, "numeric"));

  private TypeConstants() {
    // disable construction
  }
}
