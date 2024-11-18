/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.type.impl;

import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.qname.QNameCache;

import edu.umd.cs.findbugs.annotations.NonNull;

public final class AnyAtomicItemType
    extends NonAdapterAtomicItemType
    implements IAtomicOrUnionType {
  @NonNull
  private static final AnyAtomicItemType INSTANCE = new AnyAtomicItemType();

  @NonNull
  public static AnyAtomicItemType instance() {
    return INSTANCE;
  }

  private AnyAtomicItemType() {
    super(
        IAnyAtomicItem.class,
        QNameCache.instance().of(MetapathConstants.NS_METAPATH, "any-atomic-type"));
  }
}
