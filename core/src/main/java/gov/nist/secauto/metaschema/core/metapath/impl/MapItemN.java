/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.impl;

import gov.nist.secauto.metaschema.core.metapath.ICollectionValue;
import gov.nist.secauto.metaschema.core.metapath.item.function.IMapKey;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An map item that supports an unbounded number of entries.
 *
 * @param <VALUE>
 *          the Java type of the entry values
 */
public class MapItemN<VALUE extends ICollectionValue>
    extends AbstractMapItem<VALUE> {
  @NonNull
  private final Map<IMapKey, VALUE> entries;

  /**
   * Construct a new map item with the provided entries.
   *
   * @param entries
   *          the entries to add to the map
   */
  @SafeVarargs
  public MapItemN(@NonNull Map.Entry<IMapKey, ? extends VALUE>... entries) {
    this(ObjectUtils.notNull(Map.ofEntries(entries)));
  }

  /**
   * Construct a new map item using the entries from the provided map.
   *
   * @param entries
   *          a map containing the entries to add to the map
   */
  public MapItemN(@NonNull Map<IMapKey, VALUE> entries) {
    this.entries = CollectionUtil.unmodifiableMap(entries);
  }

  @Override
  public Map<IMapKey, VALUE> getValue() {
    return entries;
  }
}
