/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.function;

import gov.nist.secauto.metaschema.core.metapath.item.atomic.ITemporalItem;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface ITemporalMapKey extends IMapKey {
  @NonNull
  ITemporalItem asTemporalItem();
}
