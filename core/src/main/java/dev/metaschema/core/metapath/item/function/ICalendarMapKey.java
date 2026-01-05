/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.item.function;

import dev.metaschema.core.metapath.item.atomic.ICalendarTemporalItem;

/**
 * An {@link IMapItem} key based on an {@link ICalendarTemporalItem}.
 */
@FunctionalInterface
public interface ICalendarMapKey extends ITemporalMapKey {
  @Override
  ICalendarTemporalItem getKey();
}
