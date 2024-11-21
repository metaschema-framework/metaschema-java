/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint.impl;

import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.model.constraint.ConstraintInitializationException;
import gov.nist.secauto.metaschema.core.model.constraint.IIndex;
import gov.nist.secauto.metaschema.core.model.constraint.IKeyField;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;

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
