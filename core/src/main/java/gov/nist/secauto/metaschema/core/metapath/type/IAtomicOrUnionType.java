/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.type;

import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IAtomicOrUnionType extends IItemType {

  @NonNull
  IEnhancedQName getQName();

  @Override
  @NonNull
  Class<? extends IAnyAtomicItem> getItemClass();

  @Override
  default String toSignature() {
    return getQName().toEQName(null);
  }
}
