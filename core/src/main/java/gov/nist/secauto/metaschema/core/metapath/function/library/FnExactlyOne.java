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
 * "https://www.w3.org/TR/xpath-functions-31/#func-exactly-one">fn:exactly-one</a>
 * function.
 */
public final class FnExactlyOne {
  private static final String NAME = "exactly-one";
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
      .returnOne()
      .functionHandler(FnExactlyOne::execute)
      .build();

  private FnExactlyOne() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<?> execute(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    return fnExactlyOne(ObjectUtils.requireNonNull(arguments.get(0)));
  }

  /**
   * Check that the provided sequence has exactly one item.
   * <p>
   * Based on the XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-exactly-one">fn:exactly-one</a>
   * function.
   *
   * @param sequence
   *          the sequence to evaluate
   * @return the sequence if it has zero or one items
   * @throws InvalidArgumentFunctionException
   *           with the code
   *           {@link InvalidArgumentFunctionException#INVALID_ARGUMENT_EXACTLY_ONE}
   *           if the sequence contains less or more than one item
   */
  @NonNull
  public static ISequence<?> fnExactlyOne(@NonNull ISequence<?> sequence) {
    if (sequence.size() != 1) {
      throw new InvalidArgumentFunctionException(
          InvalidArgumentFunctionException.INVALID_ARGUMENT_EXACTLY_ONE,
          String.format("fn:exactly-one called with the sequence '%s' containing a number of items other than one.",
              sequence.toSignature()));
    }
    return sequence;
  }
}
