/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import dev.metaschema.core.metapath.ContextAbsentDynamicMetapathException;
import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.FocusContext;
import dev.metaschema.core.metapath.MetapathConstants;
import dev.metaschema.core.metapath.function.IFunction;
import dev.metaschema.core.metapath.item.IItem;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.metapath.item.atomic.IIntegerItem;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implements <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#func-position">fn:position</a>.
 * <p>
 * Returns the context position from the dynamic context. The context position
 * is the position of the context item within the sequence of items currently
 * being processed.
 */
public final class FnPosition {
  private static final String NAME = "position";
  @NonNull
  static final IFunction SIGNATURE = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextDependent()
      .focusDependent()
      .returnType(IIntegerItem.type())
      .returnOne()
      .functionHandler(FnPosition::execute)
      .build();

  private FnPosition() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IIntegerItem> execute(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    return ISequence.of(fnPosition(dynamicContext));
  }

  /**
   * Returns the context position from the dynamic context.
   * <p>
   * Based on the XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-position">fn:position</a>
   * function.
   *
   * @param dynamicContext
   *          the dynamic evaluation context
   * @return the context position as an integer
   * @throws ContextAbsentDynamicMetapathException
   *           if the focus context is absent (XPDY0002)
   */
  @NonNull
  public static IIntegerItem fnPosition(@NonNull DynamicContext dynamicContext) {
    FocusContext focusContext = dynamicContext.getFocusContext();
    if (focusContext == null) {
      throw new ContextAbsentDynamicMetapathException("The context position is absent");
    }
    return IIntegerItem.valueOf(focusContext.getPosition());
  }
}
