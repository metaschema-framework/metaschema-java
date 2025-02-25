/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.function.FunctionUtils;
import gov.nist.secauto.metaschema.core.metapath.function.IArgument;
import gov.nist.secauto.metaschema.core.metapath.function.IFunction;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDayTimeDurationItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.ITimeItem;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implements the XPath 3.1 <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#func-dateTime">fn:dateTime</a>
 * function.
 */
public final class FnAdjustTimeToTimezone {
  private static final String NAME = "adjust-time-to-timezone";
  @NonNull
  static final IFunction ONE_ARG_SIGNATURE = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextDependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("arg")
          .type(ITimeItem.type())
          .zeroOrOne()
          .build())
      .returnType(ITimeItem.type())
      .returnZeroOrOne()
      .functionHandler(FnAdjustTimeToTimezone::executeOneArg)
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
          .type(ITimeItem.type())
          .zeroOrOne()
          .build())
      .argument(IArgument.builder()
          .name("timezone")
          .type(IDayTimeDurationItem.type())
          .zeroOrOne()
          .build())
      .returnType(ITimeItem.type())
      .returnZeroOrOne()
      .functionHandler(FnAdjustTimeToTimezone::executeTwoArg)
      .build();

  private FnAdjustTimeToTimezone() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<ITimeItem> executeOneArg(
      @NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    ITimeItem arg = FunctionUtils.asTypeOrNull(arguments.get(0).getFirstItem(true));
    // get the implicit timezone
    IDayTimeDurationItem timezone = dynamicContext.getImplicitTimeZoneAsDayTimeDuration();
    return arg == null ? ISequence.empty() : ISequence.of(arg.replaceTimezone(timezone));
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<ITimeItem> executeTwoArg(
      @NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    ITimeItem arg = FunctionUtils.asTypeOrNull(arguments.get(0).getFirstItem(true));
    IDayTimeDurationItem timezone = FunctionUtils.asTypeOrNull(arguments.get(1).getFirstItem(true));
    return arg == null ? ISequence.empty() : ISequence.of(arg.replaceTimezone(timezone));
  }
}
