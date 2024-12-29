/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.IItem;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.atomic.ITimeItem;
import gov.nist.secauto.metaschema.core.metapath.atomic.ITimeWithTimeZoneItem;
import gov.nist.secauto.metaschema.core.metapath.function.IFunction;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implements the XPath 3.1 <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#func-current-time">fn:current-time</a>
 * function.
 */
public final class FnCurrentTime {
  @NonNull
  static final IFunction SIGNATURE = IFunction.builder()
      .name("current-time")
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextDependent()
      .focusIndependent()
      .returnType(ITimeItem.type())
      .returnOne()
      .functionHandler(FnCurrentTime::execute)
      .build();

  private FnCurrentTime() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<ITimeItem> execute(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    return ISequence.of(fnCurrentTime(dynamicContext));
  }

  /**
   * Implements <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-current-time">fn:current-time</a>.
   *
   * @param dynamicContext
   *          the dynamic evaluation context
   * @return the current date
   */
  @NonNull
  public static ITimeItem fnCurrentTime(@NonNull DynamicContext dynamicContext) {
    return ITimeWithTimeZoneItem.valueOf(dynamicContext.getCurrentDateTime());
  }
}
