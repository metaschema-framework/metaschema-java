/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.function.IArgument;
import gov.nist.secauto.metaschema.core.metapath.function.IFunction;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidArgumentFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implements the XPath 3.1 <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#func-zero-or-one">fn:zero-or-one</a>
 * function.
 */
public final class FnZeroOrOne {
  private static final String NAME = "zero-or-one";
  @NonNull
  static final IFunction SIGNATURE = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextIndependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("arg")
          .type(IItem.type())
          .zeroOrMore()
          .build())
      .returnType(IItem.type())
      .returnZeroOrOne()
      .functionHandler(FnZeroOrOne::execute)
      .build();

  private FnZeroOrOne() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<?> execute(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    return fnZeroOrOne(ObjectUtils.requireNonNull(arguments.get(0)));
  }

  /**
   * Check that the provided sequence has zero or one items.
   * <p>
   * Based on the XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-zero-or-one">fn:zero-or-one</a>
   * function.
   *
   * @param sequence
   *          the sequence to evaluate
   * @return the sequence if it has zero or one items
   * @throws InvalidArgumentFunctionException
   *           with the code
   *           {@link InvalidArgumentFunctionException#INVALID_ARGUMENT_ZERO_OR_ONE}
   *           if the sequence contains more than one item
   */
  @NonNull
  public static ISequence<?> fnZeroOrOne(@NonNull ISequence<?> sequence) {
    if (sequence.size() > 1) {
      throw new InvalidArgumentFunctionException(
          InvalidArgumentFunctionException.INVALID_ARGUMENT_ZERO_OR_ONE,
          String.format("fn:zero-or-one called with the sequence '%s' containing more than one item.",
              sequence.toSignature()));
    }
    return sequence;
  }
}
