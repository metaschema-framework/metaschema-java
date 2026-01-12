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
import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implements the XPath 3.1
 * <a href= "https://www.w3.org/TR/xpath-functions-31/#func-head">fn:head</a>
 * function.
 */
public final class FnHead {
  private static final String NAME = "head";
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
      .returnZeroOrOne()
      .functionHandler(FnHead::execute)
      .build();

  private FnHead() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<?> execute(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    ISequence<?> items = FunctionUtils.asType(ObjectUtils.requireNonNull(arguments.get(0)));
    return ISequence.of(fnHead(items));
  }

  /**
   * Return the first item in the {@code sequence}.
   *
   * @param sequence
   *          the sequence to check
   * @return {@code IItem} first item in the sequence for a sequence {@code null}
   *         otherwise no item is returned
   */
  public static IItem fnHead(List<? extends IItem> sequence) {
    return sequence.isEmpty() ? null : sequence.get(0);
  }
}
