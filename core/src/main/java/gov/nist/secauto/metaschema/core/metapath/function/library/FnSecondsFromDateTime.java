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
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDateTimeItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDecimalItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IIntegerItem;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implements the XPath 3.1 <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#func-seconds-from-dateTime">fn:seconds-from-dateTime</a>.
 * function.
 */
public final class FnSecondsFromDateTime {
  private static final String NAME = "seconds-from-dateTime";
  @NonNull
  static final IFunction SIGNATURE = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextIndependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("arg")
          .type(IDateTimeItem.type())
          .zeroOrOne()
          .build())
      .returnType(IDecimalItem.type())
      .returnZeroOrOne()
      .functionHandler(FnSecondsFromDateTime::execute)
      .build();

  private FnSecondsFromDateTime() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IDecimalItem> execute(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    IDateTimeItem arg = FunctionUtils.asTypeOrNull(arguments.get(0).getFirstItem(true));
    return arg == null ? ISequence.empty() : ISequence.of(fnSecondsFromDateTime(arg));
  }

  /**
   * Implements <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-seconds-from-dateTime">fn:seconds-from-dateTime</a>.
   *
   * @param arg
   *          the meta:date-time item from which to extract the second component
   * @return the second component from the date as a decimal item
   */
  @NonNull
  public static IDecimalItem fnSecondsFromDateTime(IDateTimeItem arg) {
    return IDecimalItem.valueOf(arg.getSecond() + (arg.getNano() / 1_000_000_000.0));
  }
}
