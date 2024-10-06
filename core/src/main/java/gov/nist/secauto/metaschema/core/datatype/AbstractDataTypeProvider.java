/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
  protected final Lock instanceLock = new ReentrantLock();

  @Override
  public List<? extends IDataTypeAdapter<?>> getJavaTypeAdapters() {
    try {
      instanceLock.lock();
      return CollectionUtil.unmodifiableList(library);
    } finally {
      instanceLock.unlock();
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
    try {
      instanceLock.lock();
      library.add(adapter);
    } finally {
      instanceLock.unlock();
    }
  }
}
