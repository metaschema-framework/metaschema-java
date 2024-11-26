/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.impl;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ICollectionValue;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.function.FunctionUtils;
import gov.nist.secauto.metaschema.core.metapath.function.IArgument;
import gov.nist.secauto.metaschema.core.metapath.function.library.MapGet;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IIntegerItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.IMapItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.IMapKey;
import gov.nist.secauto.metaschema.core.metapath.type.ISequenceType;
import gov.nist.secauto.metaschema.core.metapath.type.Occurrence;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * The base class for {@link IMapItem} implementations, that provide an
 * implementation of common utility methods.
 *
 * @param <VALUE>
 *          the Java type of the value items contained within the map
 */
public abstract class AbstractMapItem<VALUE extends ICollectionValue>
    extends ImmutableCollections.AbstractImmutableDelegatedMap<IMapKey, VALUE>
    implements IMapItem<VALUE> {
  /**
   * The function qualified name.
   */
  @NonNull
  private static final IEnhancedQName QNAME = IEnhancedQName.of("map");
  /**
   * The function properties.
   */
  @NonNull
  private static final Set<FunctionProperty> PROPERTIES = ObjectUtils.notNull(
      EnumSet.of(FunctionProperty.DETERMINISTIC));
  /**
   * The function arguments.
   */
  @NonNull
  private static final List<IArgument> ARGUMENTS = ObjectUtils.notNull(List.of(
      IArgument.builder().name("key").type(IAnyAtomicItem.type()).one().build()));
  @NonNull
  private static final ISequenceType RESULT = ISequenceType.of(
      IAnyAtomicItem.type(), Occurrence.ZERO_OR_ONE);

  @NonNull
  private static final IMapItem<?> EMPTY = new MapItemN<>();

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
    return (IMapItem<V>) EMPTY;
  }

  @Override
  public boolean isDeterministic() {
    return true;
  }

  @Override
  public boolean isContextDepenent() {
    return false;
  }

  @Override
  public boolean isFocusDepenent() {
    return false;
  }

  @Override
  public IEnhancedQName getQName() {
    return QNAME;
  }

  @Override
  public Set<FunctionProperty> getProperties() {
    return PROPERTIES;
  }

  @Override
  public List<IArgument> getArguments() {
    return ARGUMENTS;
  }

  @Override
  public int arity() {
    return 1;
  }

  @Override
  public boolean isArityUnbounded() {
    return false;
  }

  @Override
  public ISequenceType getResult() {
    return RESULT;
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
