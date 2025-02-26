/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.impl;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.item.ICollectionValue;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * The base class for {@link ISequence} implementations, that provides an
 * implementation of common methods.
 *
 * @param <ITEM>
 *          the Java type of the items contained within the sequence
 */
public abstract class AbstractSequence<ITEM extends IItem>
    implements ISequence<ITEM> {

  @NonNull
  private static final ISequence<?> EMPTY = new SequenceN<>();

  /**
   * Get an immutable sequence that is empty.
   *
   * @param <T>
   *          the item Java type
   * @return the empty sequence
   */
  @SuppressWarnings("unchecked")
  public static <T extends IItem> ISequence<T> empty() {
    return (ISequence<T>) EMPTY;
  }

  @Override
  public List<ITEM> getValue() {
    return asList();
  }

  /**
   * Get the unmodifiable list backing this sequence.
   *
   * @return the underlying list
   */
  @NonNull
  protected abstract List<ITEM> asList();

  @Override
  public boolean isEmpty() {
    return asList().isEmpty();
  }

  @Override
  public boolean contains(Object obj) {
    return asList().contains(obj);
  }

  @Override
  public Object[] toArray() {
    return asList().toArray();
  }

  @Override
  public <T> T[] toArray(T[] array) {
    return asList().toArray(array);
  }

  @Override
  public boolean containsAll(Collection<?> collection) {
    return asList().containsAll(collection);
  }

  @Override
  public ITEM get(int index) {
    return asList().get(index);
  }

  @Override
  public int indexOf(Object obj) {
    return asList().indexOf(obj);
  }

  @Override
  public Iterator<ITEM> iterator() {
    return asList().iterator();
  }

  @Override
  public int lastIndexOf(Object obj) {
    return asList().lastIndexOf(obj);
  }

  @Override
  public ListIterator<ITEM> listIterator() {
    return asList().listIterator();
  }

  @Override
  public ListIterator<ITEM> listIterator(int index) {
    return asList().listIterator(index);
  }

  @Override
  public int size() {
    return asList().size();
  }

  @Override
  public List<ITEM> subList(int fromIndex, int toIndex) {
    return asList().subList(fromIndex, toIndex);
  }

  @Override
  public Stream<ITEM> stream() {
    return ObjectUtils.notNull(asList().stream());
  }

  @Override
  public final boolean add(ITEM item) {
    throw unsupported();
  }

  @Override
  public void add(int index, ITEM element) {
    throw unsupported();
  }

  @Override
  public final boolean addAll(Collection<? extends ITEM> collection) {
    throw unsupported();
  }

  @Override
  public boolean addAll(int index, Collection<? extends ITEM> collection) {
    throw unsupported();
  }

  @Override
  public final void clear() {
    throw unsupported();
  }

  @Override
  public final boolean remove(Object obj) {
    throw unsupported();
  }

  @Override
  public ITEM remove(int index) {
    throw unsupported();
  }

  @Override
  public final boolean removeAll(Collection<?> collection) {
    throw unsupported();
  }

  @Override
  public final boolean removeIf(Predicate<? super ITEM> filter) {
    throw unsupported();
  }

  @Override
  public final boolean retainAll(Collection<?> collection) {
    throw unsupported();
  }

  @Override
  public ITEM set(int index, ITEM element) {
    throw unsupported();
  }

  private static UnsupportedOperationException unsupported() {
    return new UnsupportedOperationException("sequences are immutable");
  }

  @SuppressWarnings("PMD.OnlyOneReturn")
  @Override
  public boolean equals(Object other) {
    // must either be the same instance or a sequence that has the same list
    // contents
    if (other == this) {
      return true;
    }
    if (!(other instanceof List)) {
      return false;
    }
    Iterator<?> iter = ((List<?>) other).iterator();
    for (Object element : asList()) {
      if (!iter.hasNext()) {
        return false;
      }
      Object otherElement = iter.next();
      if (element == null ? otherElement != null : !element.equals(otherElement)) {
        return false;
      }
    }
    return !iter.hasNext();
  }

  @Override
  public int hashCode() {
    return asList().hashCode();
  }

  @SuppressWarnings("PMD.OnlyOneReturn")
  @Override
  public boolean deepEquals(ICollectionValue other, DynamicContext dynamicContext) {
    if (!(other instanceof ISequence)) {
      return false;
    }

    ISequence<?> otherSequence = (ISequence<?>) other;
    if (size() != otherSequence.size()) {
      return false;
    }

    Iterator<? extends IItem> thisIterator = iterator();
    Iterator<? extends IItem> otherIterator = otherSequence.iterator();
    boolean retval = true;
    while (thisIterator.hasNext() && otherIterator.hasNext()) {
      IItem i1 = thisIterator.next();
      IItem i2 = otherIterator.next();
      if (!i1.deepEquals(i2, dynamicContext)) {
        retval = false;
        break;
      }
    }
    return retval;
  }

  @Override
  public String toSignature() {
    return ObjectUtils.notNull(safeStream()
        .map(IItem::toSignature)
        .collect(Collectors.joining(",", "(", ")")));
  }

  @Override
  public String toString() {
    return toSignature();
  }
}
