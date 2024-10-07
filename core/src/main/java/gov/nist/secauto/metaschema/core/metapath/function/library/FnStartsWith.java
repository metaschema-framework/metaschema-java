/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.function.FunctionUtils;
import gov.nist.secauto.metaschema.core.metapath.function.IArgument;
import gov.nist.secauto.metaschema.core.metapath.function.IFunction;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBooleanItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

public final class FnStartsWith {

  @NonNull
  static final IFunction SIGNATURE = IFunction.builder()
      .name("starts-with")
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextDependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("arg1").type(IStringItem.class)
          .zeroOrOne()
          .build())
      .argument(IArgument.builder()
          .name("arg2")
          .type(IStringItem.class)
          .zeroOrOne()
          .build())
      .returnType(IBooleanItem.class)
      .returnOne()
      .functionHandler(FnStartsWith::execute)
      .build();

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IBooleanItem> execute(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    IStringItem arg1 = FunctionUtils.asTypeOrNull(arguments.get(0).getFirstItem(true));

    IStringItem arg2 = FunctionUtils.asTypeOrNull(arguments.get(1).getFirstItem(true));

    return ISequence.of(IBooleanItem.valueOf(
        fnStartsWith(
            arg1 == null ? "" : arg1.asString(),
            arg2 == null ? "" : arg2.asString())));
  }

  /**
   * Determine if the string provided in the first argument contains the string in
   * the second argument as a leading substring.
   * <p>
   * Based on the XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-starts-with">fn:starts-with</a>
   * function.
   *
   * @param arg1
   *          the string to examine
   * @param arg2
   *          the string to check as the leading substring
   * @return {@code true} if {@code arg1} starts with {@code arg2}, or
   *         {@code false} otherwise
   */
  public static boolean fnStartsWith(@NonNull String arg1, @NonNull String arg2) {
    boolean retval;
    if (arg2.isEmpty()) {
      retval = true;
    } else if (arg1.isEmpty()) {
      retval = false;
    } else {
      retval = arg1.startsWith(arg2);
    }
    return retval;
  }

  private FnStartsWith() {
    // disable construction
  }
}
