/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function;

import dev.metaschema.core.metapath.StaticMetapathException;
import dev.metaschema.core.qname.IEnhancedQName;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Resolves Metapath function signatures based on name and arity.
 */
@FunctionalInterface
public interface IFunctionResolver {
  /**
   * Retrieve the function with the provided name that supports the signature of
   * the provided methods.
   *
   * @param name
   *          the name of a group of functions
   * @param arity
   *          the count of arguments for use in determining an argument signature
   *          match
   * @return the matching function
   * @throws StaticMetapathException
   *           with the code {@link StaticMetapathException#NO_FUNCTION_MATCH} if
   *           a matching function was not found
   */
  @NonNull
  IFunction getFunction(@NonNull IEnhancedQName name, int arity);
}
