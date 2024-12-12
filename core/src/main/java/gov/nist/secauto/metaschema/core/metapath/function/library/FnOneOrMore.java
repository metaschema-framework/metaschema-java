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
 * "https://www.w3.org/TR/xpath-functions-31/#func-one-or-more">fn:one-or-more</a>
 * function.
 */
public final class FnOneOrMore {
  private static final String NAME = "one-or-more";
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
      .returnOneOrMore()
      .functionHandler(FnOneOrMore::execute)
      .build();

  private FnOneOrMore() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<?> execute(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    return fnOneOrMore(ObjectUtils.requireNonNull(arguments.get(0)));
  }

  /**
   * Check that the provided sequence has one or more items.
   * <p>
   * Based on the XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-one-or-more">fn:one-or-more</a>
   * function.
   *
   * @param sequence
   *          the sequence to evaluate
   * @return the sequence if it has zero or one items
   * @throws InvalidArgumentFunctionException
   *           with the code
   *           {@link InvalidArgumentFunctionException#INVALID_ARGUMENT_ONE_OR_MORE}
   *           if the sequence contains no items
   */
  @NonNull
  public static ISequence<?> fnOneOrMore(@NonNull ISequence<?> sequence) {
    if (sequence.isEmpty()) {
      throw new InvalidArgumentFunctionException(
          InvalidArgumentFunctionException.INVALID_ARGUMENT_ONE_OR_MORE,
          String.format("fn:one-or-more called with the sequence '%s' containing less than one item.",
              sequence.toSignature()));
    }
    return sequence;
  }
}
