/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.impl;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * This implementation is inspired by the similar implementation provided by the
 * JDK.
 */
@SuppressWarnings("PMD.MissingStaticMethodInNonInstantiatableClass")
public final class ImmutableCollections {

  private ImmutableCollections() {
    // disable construction
  }

  private static UnsupportedOperationException unsupported() {
    return new UnsupportedOperationException("method not supported");
  }

  /**
   * A base class for an immutable collection.
   *
   * @param <T>
   *          the item Java type
   */
  public abstract static class AbstractImmutableCollection<T>
      extends AbstractCollection<T> {
    @Override
    public final boolean add(T item) {
      throw unsupported();
    }

    @Override
    public final boolean addAll(Collection<? extends T> collection) {
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
    public final boolean removeAll(Collection<?> collection) {
      throw unsupported();
    }

    @Override
    public final boolean removeIf(Predicate<? super T> filter) {
      throw unsupported();
    }

    @Override
    public final boolean retainAll(Collection<?> collection) {
      throw unsupported();
    }
  }

  /**
   * A base class for an immutable list.
   *
   * @param <T>
   *          the item Java type
   */
  public abstract static class AbstractImmutableList<T>
      extends AbstractImmutableCollection<T>
      implements List<T> {

    @Override
    public boolean addAll(int index, Collection<? extends T> collection) {
      throw unsupported();
    }

    @Override
    public T set(int index, T element) {
      throw unsupported();
    }

    @Override
    public void add(int index, T element) {
      throw unsupported();
    }

    @Override
    public T remove(int index) {
      throw unsupported();
    }
  }

  /**
   * A base class for an immutable list that wraps a list.
   *
   * @param <T>
   *          the item Java type
   */
  public abstract static class AbstractImmutableDelegatedList<T>
      extends AbstractImmutableList<T> {

    /**
     * Get the wrapped list.
     *
     * @return the list
     */
    @NonNull
    protected abstract List<T> getValue();

    @Override
    public T get(int index) {
      return getValue().get(index);
    }

    @Override
    public int indexOf(Object obj) {
      return getValue().indexOf(obj);
    }

    @Override
    public Iterator<T> iterator() {
      return getValue().iterator();
    }

    @Override
    public int lastIndexOf(Object obj) {
      return getValue().lastIndexOf(obj);
    }

    @Override
    public ListIterator<T> listIterator() {
      return getValue().listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
      return getValue().listIterator(index);
    }

    @Override
    public int size() {
      return getValue().size();
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
      return getValue().subList(fromIndex, toIndex);
    }

    @Override
    public Stream<T> stream() {
      return ObjectUtils.notNull(getValue().stream());
    }

    @Override
    public String toString() {
      return getValue().toString();
    }
  }

  /**
   * A base class for an immutable map.
   *
   * @param <K>
   *          the map key Java type
   * @param <V>
   *          the map value Java type
   */
  public abstract static class AbstractImmutableMap<K, V>
      extends AbstractMap<K, V> {
    @Override
    public void clear() {
      throw unsupported();
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> rf) {
      throw unsupported();
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mf) {
      throw unsupported();
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> rf) {
      throw unsupported();
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> rf) {
      throw unsupported();
    }

    @Override
    public V put(K key, V value) {
      throw unsupported();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
      throw unsupported();
    }

    @Override
    public V putIfAbsent(K key, V value) {
      throw unsupported();
    }

    @Override
    public V remove(Object key) {
      throw unsupported();
    }

    @Override
    public boolean remove(Object key, Object value) {
      throw unsupported();
    }

    @Override
    public V replace(K key, V value) {
      throw unsupported();
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
      throw unsupported();
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
      throw unsupported();
    }
  }

  /**
   * A base class for an immutable map that wraps a map.
   *
   * @param <K>
   *          the map key Java type
   * @param <V>
   *          the map value Java type
   */
  public abstract static class AbstractImmutableDelegatedMap<K, V>
      extends AbstractImmutableMap<K, V> {

    /**
     * Get the wrapped map.
     *
     * @return the map
     */
    @NonNull
    protected abstract Map<K, V> getValue();

    @Override
    public Set<Entry<K, V>> entrySet() {
      return Collections.unmodifiableSet(getValue().entrySet());
    }
  }
}
