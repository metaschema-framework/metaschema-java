/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.MetapathException;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * This functional interface provides a dispatch method for executing a function
 * call.
 */
@FunctionalInterface
public interface IFunctionExecutor {
  /**
   * Execute the provided function using the provided arguments, dynamic context,
   * and focus.
   *
   * @param function
   *          the signature of the function
   * @param arguments
   *          the function arguments
   * @param dynamicContext
   *          the dynamic evaluation context
   * @param focus
   *          the current focus
   * @return a sequence containing the result of the execution
   * @throws MetapathException
   *           if an error occurred while executing the function
   */
  @NonNull
  ISequence<?> execute(
      @NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      @Nullable IItem focus);
}
