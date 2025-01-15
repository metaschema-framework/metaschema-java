/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.function;

import gov.nist.secauto.metaschema.core.metapath.item.atomic.ICalendarTemporalItem;

public interface ICalendarMapKey extends IMapKey {
  @Override
  ICalendarTemporalItem getKey();
}
