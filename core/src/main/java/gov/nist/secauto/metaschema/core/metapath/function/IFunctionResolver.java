/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function;

import gov.nist.secauto.metaschema.core.metapath.StaticMetapathError;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

import edu.umd.cs.findbugs.annotations.NonNull;

@FunctionalInterface
public interface IFunctionResolver {
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
   * @throws StaticMetapathError
   *           with the code {@link StaticMetapathError#NO_FUNCTION_MATCH} if a
   *           matching function was not found
   */
  @NonNull
  IFunction getFunction(@NonNull IEnhancedQName name, int arity);
}
