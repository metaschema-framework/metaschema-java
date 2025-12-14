/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.function.impl;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.function.FunctionUtils;
import gov.nist.secauto.metaschema.core.metapath.function.IArgument;
import gov.nist.secauto.metaschema.core.metapath.function.library.MapGet;
import gov.nist.secauto.metaschema.core.metapath.impl.IFeatureCollectionFunctionItem;
import gov.nist.secauto.metaschema.core.metapath.item.ICollectionValue;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IIntegerItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.IMapItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.IMapKey;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;
import nl.talsmasoftware.lazy4j.Lazy;

/**
 * The base class for {@link IMapItem} implementations, that provide an
 * implementation of common utility methods.
 *
 * @param <VALUE>
 *          the Java type of the value items contained within the map
 */
public abstract class AbstractMapItem<VALUE extends ICollectionValue>
    extends ImmutableCollections.AbstractImmutableDelegatedMap<IMapKey, VALUE>
    implements IMapItem<VALUE>, IFeatureCollectionFunctionItem {
  /**
   * The function qualified name.
   */
  @NonNull
  private static final IEnhancedQName QNAME = IEnhancedQName.of("map");
  /**
   * The function arguments, lazily initialized to prevent class initialization
   * deadlock when multiple threads trigger class loading simultaneously.
   */
  @NonNull
  private static final Lazy<List<IArgument>> ARGUMENTS = ObjectUtils.notNull(Lazy.of(() -> ObjectUtils.notNull(List.of(
      IArgument.builder().name("key").type(IAnyAtomicItem.type()).one().build()))));
  /**
   * An empty map item singleton, lazily initialized to prevent class
   * initialization deadlock.
   */
  @NonNull
  private static final Lazy<IMapItem<?>> EMPTY = ObjectUtils.notNull(Lazy.of(MapItemN::new));

  /**
   * Get an immutable map item that is empty.
   *
   * @param <V>
   *          the Java type of the collection value
   * @return the empty map item
   */

  @SuppressWarnings("unchecked")
  @NonNull
  public static <V extends ICollectionValue> IMapItem<V> empty() {
    return (IMapItem<V>) EMPTY.get();
  }

  @Override
  public IEnhancedQName getQName() {
    return QNAME;
  }

  @Override
  public List<IArgument> getArguments() {
    return ARGUMENTS.get();
  }

  @Override
  public ISequence<?> execute(List<? extends ISequence<?>> arguments, DynamicContext dynamicContext,
      ISequence<?> focus) {
    ISequence<? extends IIntegerItem> arg = FunctionUtils.asType(
        ObjectUtils.notNull(arguments.get(0)));

    IAnyAtomicItem key = arg.getFirstItem(true);
    if (key == null) {
      return ISequence.empty(); // NOPMD - readability
    }

    ICollectionValue result = MapGet.get(this, key);
    return result == null ? ISequence.empty() : result.toSequence();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getValue());
  }

  @Override
  public boolean equals(Object other) {
    return other == this
        || other instanceof IMapItem && getValue().equals(((IMapItem<?>) other).getValue());
  }

  @Override
  public boolean deepEquals(ICollectionValue other, DynamicContext dynamicContext) {
    if (!(other instanceof IMapItem)) {
      return false;
    }

    IMapItem<?> otherItem = (IMapItem<?>) other;
    if (size() != otherItem.size()) {
      return false;
    }

    Iterator<Map.Entry<IMapKey, VALUE>> thisIterator = entrySet().iterator();
    Iterator<? extends Map.Entry<IMapKey, ? extends ICollectionValue>> otherIterator = otherItem.entrySet().iterator();
    boolean retval = true;
    while (thisIterator.hasNext() && otherIterator.hasNext()) {
      Map.Entry<IMapKey, ? extends ICollectionValue> i1 = thisIterator.next();
      Map.Entry<IMapKey, ? extends ICollectionValue> i2 = otherIterator.next();

      retval = i1.getKey().isSameKey(ObjectUtils.notNull(i2.getKey()))
          && i1.getValue().deepEquals(i2.getValue(), dynamicContext);
      if (!retval) {
        break;
      }
    }
    return retval;
  }

  @Override
  public String toSignature() {
    return ObjectUtils.notNull(entrySet().stream()
        .map(entry -> entry.getKey().getKey().toSignature() + "=" + entry.getValue().toSignature())
        .collect(Collectors.joining(",", "[", "]")));
  }

  @Override
  public String toString() {
    return toSignature();
  }
}
