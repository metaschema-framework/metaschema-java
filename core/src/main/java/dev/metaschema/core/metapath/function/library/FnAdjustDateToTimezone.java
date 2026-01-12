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
import dev.metaschema.core.metapath.item.atomic.IDateItem;
import dev.metaschema.core.metapath.item.atomic.IDayTimeDurationItem;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implements the XPath 3.1 <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#func-adjust-date-to-timezone">fn:adjust-date-to-timezone</a>
 * function.
 */
public final class FnAdjustDateToTimezone {
  private static final String NAME = "adjust-date-to-timezone";
  @NonNull
  static final IFunction ONE_ARG_SIGNATURE = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextDependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("arg")
          .type(IDateItem.type())
          .zeroOrOne()
          .build())
      .returnType(IDateItem.type())
      .returnZeroOrOne()
      .functionHandler(FnAdjustDateToTimezone::executeOneArg)
      .build();
  @NonNull
  static final IFunction TWO_ARG_SIGNATURE = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextIndependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("arg")
          .type(IDateItem.type())
          .zeroOrOne()
          .build())
      .argument(IArgument.builder()
          .name("timezone")
          .type(IDayTimeDurationItem.type())
          .zeroOrOne()
          .build())
      .returnType(IDateItem.type())
      .returnZeroOrOne()
      .functionHandler(FnAdjustDateToTimezone::executeTwoArg)
      .build();

  private FnAdjustDateToTimezone() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IDateItem> executeOneArg(
      @NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    IDateItem arg = FunctionUtils.asTypeOrNull(arguments.get(0).getFirstItem(true));
    return arg == null
        // Per spec, return empty sequence if the arg is null
        ? ISequence.empty()
        // use the implicit timezone
        : ISequence.of(arg.replaceTimezone(dynamicContext.getImplicitTimeZoneAsDayTimeDuration()));
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IDateItem> executeTwoArg(
      @NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    IDateItem arg = FunctionUtils.asTypeOrNull(arguments.get(0).getFirstItem(true));
    IDayTimeDurationItem timezone = FunctionUtils.asTypeOrNull(arguments.get(1).getFirstItem(true));
    return arg == null
        // Per spec, return empty sequence if the arg is null
        ? ISequence.empty()
        // use the provided timezone
        : ISequence.of(arg.replaceTimezone(timezone));
  }
}
