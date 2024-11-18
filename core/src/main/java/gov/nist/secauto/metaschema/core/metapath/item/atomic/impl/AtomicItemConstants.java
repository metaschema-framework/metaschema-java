/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic.impl;

import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDurationItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IIPAddressItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IMarkupItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.INumericItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.ITemporalItem;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.impl.NonAdapterAtomicItemType;
import gov.nist.secauto.metaschema.core.qname.QNameCache;

import edu.umd.cs.findbugs.annotations.NonNull;

public final class AtomicItemConstants {

  @NonNull
  public static final IAtomicOrUnionType DURATION_ITEM_TYPE = new NonAdapterAtomicItemType(
      IDurationItem.class,
      QNameCache.instance().of(MetapathConstants.NS_METAPATH, "duration"));
  @NonNull
  public static final IAtomicOrUnionType IP_ADDRESS_ITEM_TYPE = new NonAdapterAtomicItemType(
      IIPAddressItem.class,
      QNameCache.instance().of(MetapathConstants.NS_METAPATH, "ip-address"));
  @NonNull
  public static final IAtomicOrUnionType MARKUP_ITEM_TYPE = new NonAdapterAtomicItemType(
      IMarkupItem.class,
      QNameCache.instance().of(MetapathConstants.NS_METAPATH, "markup"));
  @NonNull
  public static final IAtomicOrUnionType NUMERIC_ITEM_TYPE = new NonAdapterAtomicItemType(
      INumericItem.class,
      QNameCache.instance().of(MetapathConstants.NS_METAPATH, "numeric"));
  @NonNull
  public static final IAtomicOrUnionType TEMPORAL_ITEM_TYPE = new NonAdapterAtomicItemType(
      ITemporalItem.class,
      QNameCache.instance().of(MetapathConstants.NS_METAPATH, "temporal"));

  private AtomicItemConstants() {
    // prevent construction
  }

}
