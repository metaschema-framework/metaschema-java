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
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDurationItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IIntegerItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IYearMonthDurationItem;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implements the XPath 3.1 <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#func-minutes-from-duration">fn:minutes-from-duration</a>.
 * function.
 */
public final class FnMinutesFromDuration {
  private static final String NAME = "minutes-from-duration";
  @NonNull
  static final IFunction SIGNATURE = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextIndependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("arg")
          .type(IDurationItem.type())
          .zeroOrOne()
          .build())
      .returnType(IIntegerItem.type())
      .returnZeroOrOne()
      .functionHandler(FnMinutesFromDuration::execute)
      .build();

  private FnMinutesFromDuration() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IIntegerItem> execute(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    IDurationItem arg = FunctionUtils.asTypeOrNull(arguments.get(0).getFirstItem(true));
    if (arg instanceof IYearMonthDurationItem) {
      return ISequence.of(IIntegerItem.valueOf(0));
    }
    return arg == null ? ISequence.empty() : ISequence.of(fnMinutesFromDuration((IDayTimeDurationItem) arg));
  }

  /**
   * Implements <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-minutes-from-duration">fn:minutes-from-duration</a>.
   *
   * @param arg
   *          the meta:duration item from which to extract the minutes component
   * @return the minutes component from the date as an integer
   */
  @NonNull
  public static IIntegerItem fnMinutesFromDuration(IDayTimeDurationItem arg) {
    long durationSeconds = arg.asDuration().toSeconds();
    return IIntegerItem.valueOf((durationSeconds % 3_600) / 60);
  }
}
