/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.function.IFunction;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDateItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDateWithTimeZoneItem;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implements the XPath 3.1 <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#func-current-date">fn:current-date</a>
 * function.
 */
public final class FnCurrentDate {
  @NonNull
  private static final String NAME = "current-date";
  @NonNull
  static final IFunction SIGNATURE = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextDependent()
      .focusIndependent()
      .returnType(IDateItem.type())
      .returnOne()
      .functionHandler(FnCurrentDate::execute)
      .build();

  private FnCurrentDate() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IDateItem> execute(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    return ISequence.of(fnCurrentDate(dynamicContext));
  }

  /**
   * Implements <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-current-date">fn:current-date</a>.
   *
   * @param dynamicContext
   *          the dynamic evaluation context
   * @return the current date
   */
  @NonNull
  public static IDateItem fnCurrentDate(@NonNull DynamicContext dynamicContext) {
    // FIXME: support implicit timezone
    return IDateWithTimeZoneItem.valueOf(dynamicContext.getCurrentDateTime());
  }
}
