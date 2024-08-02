/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.function.FunctionUtils;
import gov.nist.secauto.metaschema.core.metapath.function.IFunction;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.INumericItem;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class FunctionTestBase
    extends ExpressionTestBase {

  /**
   * Assert that the execution of the provided function and arguments produce the
   * desired results.
   *
   * @param function
   *          the function to test
   * @param expectedResult
   *          the expected result produced by the function
   * @param arguments
   *          the function arguments to use for evaluation
   */
  public static void assertFunctionResult(
      @NonNull IFunction function,
      @NonNull ISequence<?> expectedResult,
      List<ISequence<?>> arguments) {
    assertFunctionResult(function, null, expectedResult, arguments);
  }

  /**
   * Assert that the execution of the provided function and arguments produce the
   * desired results.
   *
   * @param function
   *          the function to test
   * @param focus
   *          the item focus to use for evaluation
   * @param expectedResult
   *          the expected result produced by the function
   * @param arguments
   *          the function arguments to use for evaluation
   */
  public static void assertFunctionResult(
      @NonNull IFunction function,
      @Nullable ISequence<?> focus,
      @NonNull ISequence<?> expectedResult,
      List<ISequence<?>> arguments) {

    List<ISequence<?>> usedArguments = arguments == null ? CollectionUtil.emptyList() : arguments;

    // QName functionName = function.getQName();
    //
    // IFunction resolvedFunction =
    // FunctionService.getInstance().getFunction(functionName,
    // usedArguments.size());
    //
    // assertNotNull(resolvedFunction, String.format("Function '%s' not found in
    // function service.", functionName));

    assertFunctionResultInternal(function, focus, expectedResult, usedArguments);
  }

  private static void assertFunctionResultInternal(
      @NonNull IFunction function,
      @Nullable ISequence<?> focus,
      @NonNull ISequence<?> expectedResult,
      List<ISequence<?>> arguments) {
    ISequence<INumericItem> result = FunctionTestBase.executeFunction(
        function,
        newDynamicContext(),
        focus,
        arguments);

    assertAll(
        () -> assertEquals(expectedResult, result),
        () -> assertEquals(
            FunctionUtils.getTypes(expectedResult.getValue()),
            FunctionUtils.getTypes(result.getValue())));

  }

  @SuppressWarnings("unchecked")
  private static <R extends IItem> ISequence<R> executeFunction(
      @NonNull IFunction function,
      @Nullable DynamicContext dynamicContext,
      @Nullable ISequence<?> focus,
      List<ISequence<?>> arguments) {

    DynamicContext context = dynamicContext == null ? new DynamicContext() : dynamicContext;
    ISequence<?> focusSeqence = function.isFocusDepenent()
        ? ObjectUtils.requireNonNull(focus, "Function call requires a focus")
        : ISequence.empty();
    return (ISequence<R>) function.execute(
        arguments == null ? CollectionUtil.emptyList() : arguments,
        context,
        focusSeqence);
  }
}
