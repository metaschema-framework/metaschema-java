/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ICollectionValue;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.function.FunctionUtils;
import gov.nist.secauto.metaschema.core.metapath.function.IArgument;
import gov.nist.secauto.metaschema.core.metapath.function.IFunction;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.IMapItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.IMapKey;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implements the XPath 3.1 <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#func-map-remove">map:remove</a>
 * function.
 */
public final class MapRemove {
  private static final String NAME = "remove";
  @NonNull
  static final IFunction SIGNATURE = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS_MAP)
      .deterministic()
      .contextIndependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("map")
          .type(IMapItem.class)
          .one()
          .build())
      .argument(IArgument.builder()
          .name("keys")
          .type(IAnyAtomicItem.class)
          .zeroOrMore()
          .build())
      .returnType(IMapItem.class)
      .returnOne()
      .functionHandler(MapRemove::execute)
      .build();

  private MapRemove() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static <V extends ICollectionValue> ISequence<?> execute(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    IMapItem<V> map = FunctionUtils.asType(ObjectUtils.requireNonNull(arguments.get(0).getFirstItem(true)));
    ISequence<? extends IAnyAtomicItem> keys = FunctionUtils.asType(ObjectUtils.requireNonNull(arguments.get(1)));

    return ISequence.of(removeItems(map, keys));
  }

  /**
   * An implementation of XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-map-remove">map:remove</a>.
   *
   * @param <V>
   *          the type of items in the given Metapath map
   * @param map
   *          the map of Metapath items that is to be modified
   * @param keys
   *          the keys to remove from the map
   * @return the modified map
   */
  @NonNull
  public static <V extends ICollectionValue> IMapItem<V> removeItems(
      @NonNull IMapItem<V> map,
      @NonNull Collection<? extends IAnyAtomicItem> keys) {
    Set<IMapKey> keySet = keys.stream()
        .map(IAnyAtomicItem::asMapKey)
        .collect(Collectors.toSet());

    Map<IMapKey, V> remaining = ObjectUtils.notNull(map.entrySet().stream()
        .filter(entry -> !keySet.contains(entry.getKey()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

    return IMapItem.ofCollection(remaining);
  }
}
