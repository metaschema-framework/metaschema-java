/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class FunctionLibrary implements IFunctionLibrary {

  @NonNull
  private final Map<QName, NamedFunctionSet> libraryByQName = new HashMap<>(); // NOPMD - intentional
  @NonNull
  private final Map<String, NamedFunctionSet> libraryByName = new HashMap<>(); // NOPMD - intentional
  @NonNull
  private final ReadWriteLock instanceLock = new ReentrantReadWriteLock();

  /**
   * Register the provided function signature.
   *
   * @param function
   *          the function signature to register
   * @throws IllegalArgumentException
   *           if the provided function has the same arity as a previously
   *           registered function with the same name
   */
  public final void registerFunction(@NonNull IFunction function) {
    registerFunctionByQName(function);
    registerFunctionByName(function);
  }

  private void registerFunctionByQName(@NonNull IFunction function) {
    QName qname = function.getQName();
    IFunction duplicate;
    Lock writeLock = instanceLock.writeLock();
    writeLock.lock();
    try {
      NamedFunctionSet functions = libraryByQName.get(qname);
      if (functions == null) {
        functions = new NamedFunctionSet();
        libraryByQName.put(qname, functions);
      }
      duplicate = functions.addFunction(function);
    } finally {
      writeLock.unlock();
    }
    if (duplicate != null) {
      throw new IllegalArgumentException(String.format("Duplicate functions with same arity: %s shadows %s",
          duplicate.toSignature(), function.toSignature()));
    }
  }

  private void registerFunctionByName(@NonNull IFunction function) {
    String name = function.getName();
    Lock writeLock = instanceLock.writeLock();
    writeLock.lock();
    try {
      NamedFunctionSet functions = libraryByName.get(name);
      if (functions == null) {
        functions = new NamedFunctionSet();
        libraryByName.put(name, functions);
      }
      // replace duplicates
      functions.addFunction(function);
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public Stream<IFunction> stream() {
    Lock readLock = instanceLock.readLock();
    readLock.lock();
    try {
      return ObjectUtils.notNull(libraryByQName.values().stream().flatMap(NamedFunctionSet::getFunctionsAsStream));
    } finally {
      readLock.unlock();
    }
  }

  @Override
  public IFunction getFunction(@NonNull String name, int arity) {
    IFunction retval = null;
    Lock readLock = instanceLock.readLock();
    readLock.lock();
    try {
      NamedFunctionSet functions = libraryByName.get(name);
      if (functions != null) {
        retval = functions.getFunctionWithArity(arity);
      }
    } finally {
      readLock.unlock();
    }
    return retval;
  }

  @Override
  public IFunction getFunction(@NonNull QName name, int arity) {
    IFunction retval = null;
    Lock readLock = instanceLock.readLock();
    readLock.lock();
    try {
      NamedFunctionSet functions = libraryByQName.get(name);
      if (functions != null) {
        retval = functions.getFunctionWithArity(arity);
      }
    } finally {
      readLock.unlock();
    }
    return retval;
  }

  private static class NamedFunctionSet {
    private final Map<Integer, IFunction> arityToFunctionMap;
    private IFunction unboundedArity;

    public NamedFunctionSet() {
      this.arityToFunctionMap = new HashMap<>();
    }

    @SuppressWarnings("null")
    @NonNull
    public Stream<IFunction> getFunctionsAsStream() {
      return arityToFunctionMap.values().stream();
    }

    @Nullable
    public IFunction getFunctionWithArity(int arity) {
      IFunction retval = arityToFunctionMap.get(arity);
      if (retval == null && unboundedArity != null && unboundedArity.arity() < arity) {
        retval = unboundedArity;
      }
      return retval;
    }

    @Nullable
    public IFunction addFunction(@NonNull IFunction function) {
      IFunction old = arityToFunctionMap.put(function.arity(), function);
      if (function.isArityUnbounded()) {
        unboundedArity = function;
      }
      return old;
    }
  }
}
