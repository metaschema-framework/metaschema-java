/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import java.util.List;

import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.MetapathConstants;
import dev.metaschema.core.metapath.function.IFunction;
import dev.metaschema.core.metapath.item.IItem;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.metapath.item.atomic.IDateTimeItem;
import dev.metaschema.core.metapath.item.atomic.IDateTimeWithTimeZoneItem;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implements the XPath 3.1 <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#func-current-dateTime">fn:current-dateTime</a>
 * function.
 */
public final class FnCurrentDateTime {
  private static final String NAME = "current-dateTime";
  @NonNull
  static final IFunction SIGNATURE = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextDependent()
      .focusIndependent()
      .returnType(IDateTimeItem.type())
      .returnOne()
      .functionHandler(FnCurrentDateTime::execute)
      .build();

  private FnCurrentDateTime() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IDateTimeItem> execute(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    return ISequence.of(fnCurrentDateTime(dynamicContext));
  }

  /**
   * Implements <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-current-dateTime">fn:current-dateTime</a>.
   *
   * @param dynamicContext
   *          the dynamic evaluation context
   * @return the current date
   */
  @NonNull
  public static IDateTimeItem fnCurrentDateTime(@NonNull DynamicContext dynamicContext) {
    // FIXME: support implicit timezone
    return IDateTimeWithTimeZoneItem.valueOf(dynamicContext.getCurrentDateTime());
  }
}
