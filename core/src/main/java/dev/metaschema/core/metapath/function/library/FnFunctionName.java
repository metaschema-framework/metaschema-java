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
import dev.metaschema.core.metapath.item.atomic.IQNameItem;
import dev.metaschema.core.util.ObjectUtils;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * /** Implements <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#func-function-name">fn:function-name</a>
 * functions.
 */
public final class FnFunctionName {
  @NonNull
  private static final String NAME = "function-name";

  @NonNull
  static final IFunction SIGNATURE = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextIndependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("func")
          .type(IFunction.type())
          .one()
          .build())
      .returnType(IQNameItem.type())
      .returnZeroOrOne()
      .functionHandler(FnFunctionName::execute)
      .build();

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IQNameItem> execute(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    IFunction fn = FunctionUtils.asType(ObjectUtils.requireNonNull(arguments.get(0).getFirstItem(true)));
    return fn.isNamedFunction()
        ? ISequence.of(IQNameItem.valueOf(fn.getQName()))
        : ISequence.empty();
  }

  private FnFunctionName() {
    // disable construction
  }
}
