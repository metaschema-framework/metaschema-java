/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A base implementation of an {@link IDataTypeProvider}, supporting dynamic
 * loading of Metaschema data type extensions at runtime.
 * <p>
 * The {@link MetaschemaDataTypeProvider} class provides an example of how to
 * use this class to provide new data types.
 */
public abstract class AbstractDataTypeProvider implements IDataTypeProvider {
  @NonNull
  private final List<IDataTypeAdapter<?>> library = new LinkedList<>();
  @NonNull
  private final ReadWriteLock libraryLock = new ReentrantReadWriteLock();

  @Override
  public List<? extends IDataTypeAdapter<?>> getJavaTypeAdapters() {
    Lock readLock = libraryLock.readLock();
    readLock.lock();
    try {
      // make a defensive copy to protect callers from potential modifications
      return CollectionUtil.unmodifiableList(new ArrayList<>(library));
    } finally {
      readLock.unlock();
    }
  }

  /**
   * Register the provided {@code adapter} with the type system.
   *
   * @param adapter
   *          the adapter to register
   * @throws IllegalArgumentException
   *           if another type adapter has no name
   */
  protected void registerDatatype(@NonNull IDataTypeAdapter<?> adapter) {
    if (adapter.getNames().isEmpty()) {
      throw new IllegalArgumentException("The adapter has no name: " + adapter.getClass().getName());
    }
    Lock writeLock = libraryLock.writeLock();
    writeLock.lock();
    try {
      library.add(adapter);
    } finally {
      writeLock.unlock();
    }
  }
}
