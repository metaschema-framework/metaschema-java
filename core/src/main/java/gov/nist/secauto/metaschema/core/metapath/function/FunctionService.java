/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function;

import gov.nist.secauto.metaschema.core.metapath.StaticMetapathException;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import nl.talsmasoftware.lazy4j.Lazy;

public final class FunctionService {
  private static final Lazy<FunctionService> INSTANCE = Lazy.lazy(() -> new FunctionService());
  @NonNull
  private final ServiceLoader<IFunctionLibrary> loader;
  @NonNull
  private final Lazy<IFunctionLibrary> library;
  @NonNull
  private final Lock instanceLock = new ReentrantLock();

  /**
   * Get the singleton instance of the function service.
   *
   * @return the service instance
   */
  public static FunctionService getInstance() {
    return INSTANCE.get();
  }

  /**
   * Construct a new function service.
   */
  @SuppressWarnings("null")
  public FunctionService() {
    this.loader = ServiceLoader.load(IFunctionLibrary.class);
    ServiceLoader<IFunctionLibrary> loader = getLoader();

    this.library = Lazy.lazy(() -> {
      FunctionLibrary functionLibrary = new FunctionLibrary();
      loader.stream()
          .map(Provider<IFunctionLibrary>::get)
          .flatMap(IFunctionLibrary::stream)
          .forEachOrdered(function -> functionLibrary.registerFunction(ObjectUtils.notNull(function)));
      return functionLibrary;
    });
  }

  /**
   * Get the function service loader instance.
   *
   * @return the service loader instance.
   */
  @NonNull
  private ServiceLoader<IFunctionLibrary> getLoader() {
    return loader;
  }

  @NonNull
  private IFunctionLibrary getLibrary() {
    return ObjectUtils.notNull(library.get());
  }

  /**
   * Retrieve the collection of function signatures in this library as a stream.
   *
   * @return a stream of function signatures
   */
  public Stream<IFunction> stream() {
    return getLibrary().stream();
  }

  /**
   * Retrieve the function with the provided name that supports the signature of
   * the provided methods, if such a function exists.
   *
   * @param name
   *          the name of a group of functions
   * @param arity
   *          the count of arguments for use in determining an argument signature
   *          match
   * @return the matching function or {@code null} if no match exists
   * @throws StaticMetapathException
   *           if a matching function was not found
   */
  public IFunction getFunction(@NonNull String name, int arity) {
    IFunction retval;
    try {
      instanceLock.lock();
      retval = getLibrary().getFunction(name, arity);
    } finally {
      instanceLock.unlock();
    }

    if (retval == null) {
      throw new StaticMetapathException(StaticMetapathException.NO_FUNCTION_MATCH,
          String.format("unable to find function with name '%s' having arity '%d'", name, arity));
    }
    return retval;
  }

  /**
   * Retrieve the function with the provided name that supports the signature of
   * the provided methods, if such a function exists.
   *
   * @param name
   *          the name of a group of functions
   * @param arity
   *          the count of arguments for use in determining an argument signature
   *          match
   * @return the matching function or {@code null} if no match exists
   * @throws StaticMetapathException
   *           if a matching function was not found
   */
  public IFunction getFunction(@NonNull QName name, int arity) {
    IFunction retval;
    try {
      instanceLock.lock();
      retval = getLibrary().getFunction(name, arity);
    } finally {
      instanceLock.unlock();
    }

    if (retval == null) {
      throw new StaticMetapathException(StaticMetapathException.NO_FUNCTION_MATCH,
          String.format("unable to find function with name '%s' having arity '%d'", name, arity));
    }
    return retval;
  }
}
