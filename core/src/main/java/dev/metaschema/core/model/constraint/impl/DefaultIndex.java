/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model.constraint.impl;

import dev.metaschema.core.metapath.item.node.INodeItem;
import dev.metaschema.core.model.constraint.ConstraintInitializationException;
import dev.metaschema.core.model.constraint.IIndex;
import dev.metaschema.core.model.constraint.IIndexConstraint;
import dev.metaschema.core.model.constraint.IIndexHasKeyConstraint;
import dev.metaschema.core.model.constraint.IKeyField;
import dev.metaschema.core.model.constraint.IUniqueConstraint;
import dev.metaschema.core.util.CollectionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * The default implementation of an index that can support the
 * {@link IIndexConstraint}, {@link IIndexHasKeyConstraint}, and
 * {@link IUniqueConstraint}.
 */
public class DefaultIndex implements IIndex {
  @NonNull
  private final List<IKeyField> keyFields;
  @NonNull
  private final Map<List<String>, INodeItem> keyToItemMap = new ConcurrentHashMap<>();

  /**
   * Construct a new index.
   *
   * @param keyFields
   *          the key field components to use to generate keys by default
   */
  public DefaultIndex(@NonNull List<? extends IKeyField> keyFields) {
    this.keyFields = CollectionUtil.unmodifiableList(new ArrayList<>(keyFields));
  }

  @Override
  public List<IKeyField> getKeyFields() {
    return keyFields;
  }

  @Override
  public INodeItem put(@NonNull INodeItem item, @NonNull List<String> key) {
    INodeItem oldItem = null;
    if (!IIndex.isAllNulls(key)) {
      // only add keys with some information (values)
      oldItem = keyToItemMap.put(key, item);
    }
    return oldItem;
  }

  @Override
  public INodeItem get(List<String> key) {
    int requiredSize = getKeyFields().size();
    if (requiredSize != key.size()) {
      throw new ConstraintInitializationException(
          String.format("Provided key '%s' is not the size '%d' required by the index.",
              key.stream()
                  .map(value -> new StringBuilder().append('"').append(value).append('"').toString())
                  .collect(Collectors.joining(",", "{", "}")),
              requiredSize));
    }
    return keyToItemMap.get(key);
  }
}
