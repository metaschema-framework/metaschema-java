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
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDecimalItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDurationItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IIntegerItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IYearMonthDurationItem;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implements the XPath 3.1 <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#func-seconds-from-duration">fn:seconds-from-duration</a>.
 * function.
 */
public final class FnSecondsFromDuration {
  private static final String NAME = "seconds-from-duration";
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
      .functionHandler(FnSecondsFromDuration::execute)
      .build();

  private FnSecondsFromDuration() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IDecimalItem> execute(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    IDurationItem arg = FunctionUtils.asTypeOrNull(arguments.get(0).getFirstItem(true));
    if (arg instanceof IYearMonthDurationItem) {
      return ISequence.of(IIntegerItem.valueOf(0));
    }
    return arg == null ? ISequence.empty() : ISequence.of(fnSecondsFromDuration((IDayTimeDurationItem) arg));
  }

  /**
   * Implements <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-seconds-from-duration">fn:seconds-from-duration</a>.
   *
   * @param arg
   *          the meta:duration item from which to extract the second component
   * @return the second component from the duration as a decimal item
   */
  @NonNull
  public static IDecimalItem fnSecondsFromDuration(IDayTimeDurationItem arg) {
    Duration duration = arg.asDuration();
    return IDecimalItem.valueOf(BigDecimal.valueOf(duration.getSeconds() % 60).add(BigDecimal
        .valueOf(duration.getNano()).divide(BigDecimal.valueOf(1_000_000_000.0), FunctionUtils.MATH_CONTEXT)));
  }
}
