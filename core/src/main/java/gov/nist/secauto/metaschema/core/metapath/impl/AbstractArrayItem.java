/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.impl;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.function.FunctionUtils;
import gov.nist.secauto.metaschema.core.metapath.function.IArgument;
import gov.nist.secauto.metaschema.core.metapath.item.ICollectionValue;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IIntegerItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.IArrayItem;
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
 * The base class for {@link IArrayItem} implementations, that provides an
 * implementation of common methods.
 *
 * @param <ITEM>
 *          the Java type of the items contained within the sequence
 */
public abstract class AbstractArrayItem<ITEM extends ICollectionValue>
    extends ImmutableCollections.AbstractImmutableDelegatedList<ITEM>
    implements IArrayItem<ITEM> {
  @NonNull
  private static final IEnhancedQName QNAME = IEnhancedQName.of("array");
  @NonNull
  private static final Set<FunctionProperty> PROPERTIES = ObjectUtils.notNull(
      EnumSet.of(FunctionProperty.DETERMINISTIC));
  @NonNull
  private static final List<IArgument> ARGUMENTS = ObjectUtils.notNull(List.of(
      IArgument.builder().name("position").type(IIntegerItem.type()).one().build()));
  @NonNull
  private static final ISequenceType RESULT = ISequenceType.of(
      IAnyAtomicItem.type(), Occurrence.ZERO_OR_ONE);

  @NonNull
  private static final IArrayItem<?> EMPTY = new ArrayItemN<>();

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
    return (IArrayItem<T>) EMPTY;
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
  public boolean isFocusDependent() {
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

    IIntegerItem position = arg.getFirstItem(true);
    if (position == null) {
      return ISequence.empty(); // NOPMD - readability
    }

    int index = position.asInteger().intValueExact() - 1;
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
