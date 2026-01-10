/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.item.function.impl;

import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.function.FunctionUtils;
import dev.metaschema.core.metapath.function.IArgument;
import dev.metaschema.core.metapath.impl.IFeatureCollectionFunctionItem;
import dev.metaschema.core.metapath.item.ICollectionValue;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.metapath.item.atomic.IIntegerItem;
import dev.metaschema.core.metapath.item.function.IArrayItem;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.ObjectUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;
import nl.talsmasoftware.lazy4j.Lazy;

/**
 * The base class for {@link IArrayItem} implementations, that provides an
 * implementation of common methods.
 *
 * @param <ITEM>
 *          the Java type of the items contained within the sequence
 */
public abstract class AbstractArrayItem<ITEM extends ICollectionValue>
    extends ImmutableCollections.AbstractImmutableDelegatedList<ITEM>
    implements IArrayItem<ITEM>, IFeatureCollectionFunctionItem {
  @NonNull
  private static final IEnhancedQName QNAME = IEnhancedQName.of("array");
  /**
   * The function arguments, lazily initialized to prevent class initialization
   * deadlock when multiple threads trigger class loading simultaneously.
   */
  @NonNull
  private static final Lazy<List<IArgument>> ARGUMENTS = ObjectUtils.notNull(Lazy.of(() -> ObjectUtils.notNull(List.of(
      IArgument.builder().name("position").type(IIntegerItem.type()).one().build()))));
  /**
   * An empty array item singleton, lazily initialized to prevent class
   * initialization deadlock.
   */
  @NonNull
  private static final Lazy<IArrayItem<?>> EMPTY = ObjectUtils.notNull(Lazy.of(ArrayItemN::new));

  /**
   * Get an immutable array item that is empty.
   *
   * @param <T>
   *          the item Java type
   * @return the empty array item
   */
  @SuppressWarnings("unchecked")
  @NonNull
  public static <T extends ICollectionValue> IArrayItem<T> empty() {
    return (IArrayItem<T>) EMPTY.get();
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

    IIntegerItem position = arg.getFirstItem(true);
    if (position == null) {
      return ISequence.empty();
    }

    int index = position.toIntValueExact() - 1;
    ICollectionValue result = getValue().get(index);
    return result.toSequence();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getValue());
  }

  @Override
  public boolean equals(Object other) {
    return other == this
        || other instanceof IArrayItem && getValue().equals(((IArrayItem<?>) other).getValue());
  }

  @Override
  public boolean deepEquals(ICollectionValue other, DynamicContext dynamicContext) {
    if (!(other instanceof IArrayItem)) {
      return false;
    }

    IArrayItem<?> otherArray = (IArrayItem<?>) other;
    if (size() != otherArray.size()) {
      return false;
    }

    Iterator<? extends ICollectionValue> thisIterator = iterator();
    Iterator<? extends ICollectionValue> otherIterator = otherArray.iterator();
    boolean retval = true;
    while (thisIterator.hasNext() && otherIterator.hasNext()) {
      ICollectionValue i1 = thisIterator.next();
      ICollectionValue i2 = otherIterator.next();
      if (!i1.deepEquals(i2, dynamicContext)) {
        retval = false;
        break;
      }
    }
    return retval;
  }

  @Override
  public String toSignature() {
    return ObjectUtils.notNull(stream()
        .map(ICollectionValue::toSignature)
        .collect(Collectors.joining(",", "[", "]")));
  }

  @Override
  public String toString() {
    return toSignature();
  }
}
