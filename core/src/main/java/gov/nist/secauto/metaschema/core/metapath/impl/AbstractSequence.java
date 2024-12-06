/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.impl;

import gov.nist.secauto.metaschema.core.metapath.item.ICollectionValue;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.Iterator;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * The base class for {@link ISequence} implementations, that provides an
 * implementation of common methods.
 *
 * @param <ITEM>
 *          the Java type of the items contained within the sequence
 */
public abstract class AbstractSequence<ITEM extends IItem>
    extends ImmutableCollections.AbstractImmutableDelegatedList<ITEM>
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
  public boolean equals(Object other) {
    // must either be the same instance or a sequence that has the same list
    // contents
    return other == this
        || other instanceof ISequence && getValue().equals(((ISequence<?>) other).getValue());
  }

  @SuppressWarnings("PMD.OnlyOneReturn")
  @Override
  public boolean deepEquals(ICollectionValue other) {
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
      if (!i1.deepEquals(i2)) {
        retval = false;
        break;
      }
    }
    return retval;
  }

  @Override
  public int hashCode() {
    return getValue().hashCode();
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
