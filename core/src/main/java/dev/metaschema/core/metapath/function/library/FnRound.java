/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import java.util.List;

import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.MetapathConstants;
import dev.metaschema.core.metapath.function.FunctionUtils;
import dev.metaschema.core.metapath.function.IArgument;
import dev.metaschema.core.metapath.function.IFunction;
import dev.metaschema.core.metapath.item.IItem;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.metapath.item.atomic.IIntegerItem;
import dev.metaschema.core.metapath.item.atomic.INumericItem;
import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implements the XPath 3.1
 * <a href= "https://www.w3.org/TR/xpath-functions-31/#func-round">fn:round</a>
 * functions.
 */
public final class FnRound {
  private static final String NAME = "round";
  @NonNull
  static final IFunction SIGNATURE = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextIndependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("arg")
          .type(INumericItem.type())
          .zeroOrOne()
          .build())
      .returnType(INumericItem.type())
      .returnZeroOrOne()
      .functionHandler(FnRound::executeOneArg)
      .build();

  @NonNull
  static final IFunction SIGNATURE_WITH_PRECISION = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextIndependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("arg1")
          .type(INumericItem.type())
          .zeroOrOne()
          .build())
      .argument(IArgument.builder()
          .name("precision")
          .type(IIntegerItem.type())
          .one()
          .build())
      .returnType(INumericItem.type())
      .returnZeroOrOne()
      .functionHandler(FnRound::executeTwoArg)
      .build();

  private FnRound() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<INumericItem> executeOneArg(
      @NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    ISequence<? extends INumericItem> sequence = FunctionUtils.asType(
        ObjectUtils.requireNonNull(arguments.get(0)));

    INumericItem item = sequence.getFirstItem(true);
    if (item == null) {
      return ISequence.empty();
    }

    return ISequence.of(item.round());
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<INumericItem> executeTwoArg(
      @NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    ISequence<? extends INumericItem> sequence = FunctionUtils.asType(
        ObjectUtils.requireNonNull(arguments.get(0)));

    INumericItem item = sequence.getFirstItem(true);
    if (item == null) {
      return ISequence.empty();
    }

    IIntegerItem precision = FunctionUtils.asType(ObjectUtils.requireNonNull(arguments.get(1).getFirstItem(true)));

    return ISequence.of(item.round(precision));
  }
}
