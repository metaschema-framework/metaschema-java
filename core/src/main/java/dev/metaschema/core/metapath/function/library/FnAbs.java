/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.MetapathConstants;
import dev.metaschema.core.metapath.function.FunctionUtils;
import dev.metaschema.core.metapath.function.IArgument;
import dev.metaschema.core.metapath.function.IFunction;
import dev.metaschema.core.metapath.item.IItem;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.metapath.item.atomic.INumericItem;
import dev.metaschema.core.util.ObjectUtils;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implements the XPath 3.1
 * <a href= "https://www.w3.org/TR/xpath-functions-31/#func-abs">fn:abs</a>
 * function.
 */
public final class FnAbs {
  private static final String NAME = "abs";

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
      .functionHandler(FnAbs::execute)
      .build();

  private FnAbs() {
    // disable construction
  }

  // CPD-OFF
  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<INumericItem> execute(
      @NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    ISequence<? extends INumericItem> sequence = FunctionUtils.asType(ObjectUtils.requireNonNull(arguments.get(0)));

    INumericItem item = sequence.getFirstItem(true);
    if (item == null) {
      return ISequence.empty(); // NOPMD - readability
    }

    return ISequence.of(item.castAsType(item.abs()));
    // CPD-ON
  }
}
