/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import java.util.List;

import dev.metaschema.core.metapath.ContextAbsentDynamicMetapathException;
import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.FocusContext;
import dev.metaschema.core.metapath.MetapathConstants;
import dev.metaschema.core.metapath.function.IFunction;
import dev.metaschema.core.metapath.item.IItem;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.metapath.item.atomic.IIntegerItem;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implements
 * <a href="https://www.w3.org/TR/xpath-functions-31/#func-last">fn:last</a>.
 * <p>
 * Returns the context size from the dynamic context. This is the number of
 * items in the sequence being processed, and is used with predicates to test if
 * an item is the last in the sequence.
 */
public final class FnLast {
  private static final String NAME = "last";
  @NonNull
  static final IFunction SIGNATURE = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextDependent()
      .focusDependent()
      .returnType(IIntegerItem.type())
      .returnOne()
      .functionHandler(FnLast::execute)
      .build();

  private FnLast() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IIntegerItem> execute(
      @NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    return ISequence.of(fnLast(dynamicContext));
  }

  /**
   * Returns the context size from the dynamic context.
   * <p>
   * Based on the XPath 3.1
   * <a href= "https://www.w3.org/TR/xpath-functions-31/#func-last">fn:last</a>
   * function.
   *
   * @param dynamicContext
   *          the dynamic evaluation context
   * @return the context size as an integer
   * @throws ContextAbsentDynamicMetapathException
   *           if the focus context is absent (XPDY0002)
   */
  @NonNull
  public static IIntegerItem fnLast(@NonNull DynamicContext dynamicContext) {
    FocusContext focusContext = dynamicContext.getFocusContext();
    if (focusContext == null) {
      throw new ContextAbsentDynamicMetapathException("The context size is absent");
    }
    return IIntegerItem.valueOf(focusContext.getSize());
  }
}
