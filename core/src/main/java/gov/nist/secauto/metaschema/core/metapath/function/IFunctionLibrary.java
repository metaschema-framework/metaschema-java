/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function;

import java.util.stream.Stream;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IFunctionLibrary {

  /**
   * Retrieve the collection of function signatures in this library as a stream.
   *
   * @return a stream of function signatures
   */
  @NonNull
  Stream<IFunction> stream();

  /**
   * Determine if there is a function with the provided name that supports the
   * signature of the provided {@code arity}.
   *
   * @param name
   *          the name of a group of functions
   * @param arity
   *          the count of arguments for use in determining an argument signature
   *          match
   * @return {@code true} if a function signature matches or {@code false}
   *         otherwise
   */
  default boolean hasFunction(@NonNull String name, int arity) {
    return getFunction(name, arity) != null;
  }

  /**
   * Determine if there is a function with the provided namespace qualified name
   * that supports the signature of the provided {@code arity}.
   *
   * @param name
   *          the namespace qualified name of a group of functions
   * @param arity
   *          the count of arguments for use in determining an argument signature
   *          match
   * @return {@code true} if a function signature matches or {@code false}
   *         otherwise
   */
  default boolean hasFunction(@NonNull QName name, int arity) {
    return getFunction(name, arity) != null;
  }

  /**
   * Retrieve the function with the provided name that supports the signature of
   * the provided {@code arity}, if such a function exists.
   *
   * @param name
   *          the name of a group of functions
   * @param arity
   *          the count of arguments for use in determining an argument signature
   *          match
   * @return the matching function or {@code null} if no match exists
   */
  IFunction getFunction(@NonNull String name, int arity);

  /**
   * Retrieve the function with the provided namespace qualified name that
   * supports the signature of the provided {@code arity}, if such a function
   * exists.
   *
   * @param name
   *          the namespace qualified name of a group of functions
   * @param arity
   *          the count of arguments for use in determining an argument signature
   *          match
   * @return the matching function or {@code null} if no match exists
   */
  IFunction getFunction(@NonNull QName name, int arity);
}
