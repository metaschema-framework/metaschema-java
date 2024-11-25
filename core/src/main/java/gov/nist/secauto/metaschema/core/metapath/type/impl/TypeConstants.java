/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.type.impl;

import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDurationItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IIPAddressItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.INumericItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.ITemporalItem;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.qname.EQNameFactory;

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
  /**
   * The Metaschema data type that represents all temporal types that work with
   * dates and times.
   */
  @NonNull
  public static final IAtomicOrUnionType<ITemporalItem> TEMPORAL_TYPE
      = IAtomicOrUnionType.of(
          ITemporalItem.class,
          ITemporalItem::cast,
          EQNameFactory.instance().newQName(MetapathConstants.NS_METAPATH, "temporal"));

  private TypeConstants() {
    // disable construction
  }
}
