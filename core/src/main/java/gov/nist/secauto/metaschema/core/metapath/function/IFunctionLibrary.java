/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function;

import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

import java.util.stream.Stream;

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
  default boolean hasFunction(@NonNull IEnhancedQName name, int arity) {
    return getFunction(name, arity) != null;
  }

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
  IFunction getFunction(@NonNull IEnhancedQName name, int arity);
}
