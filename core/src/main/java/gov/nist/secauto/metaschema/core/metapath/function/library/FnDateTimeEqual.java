/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;
import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.function.FunctionUtils;
import gov.nist.secauto.metaschema.core.metapath.function.IArgument;
import gov.nist.secauto.metaschema.core.metapath.function.IFunction;
import gov.nist.secauto.metaschema.core.metapath.function.impl.OperationFunctions;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBooleanItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDateTimeItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDateTimeWithTimeZoneItem;

/**
 * Implements the XPath 3.1 <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#func-current-date">fn:current-date</a>
 * function.
 */
public final class FnDateTimeEqual {
  @NonNull
  static final IFunction SIGNATURE = IFunction.builder()
      .name("dateTime-equal")
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextDependent()
      .focusIndependent()
      .argument(IArgument.builder()
              .name("arg1")
              .type(IDateTimeItem.type())
              .one()
              .build())
      .argument(IArgument.builder()
          .name("arg2")
          .type(IDateTimeItem.type())
          .one()
          .build())      
      .returnType(IBooleanItem.type())
      .returnOne()
      .functionHandler(FnDateTimeEqual::execute)
      .build();

  private FnDateTimeEqual() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IBooleanItem> execute(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    IDateTimeItem arg1 = FunctionUtils.asTypeOrNull(arguments.get(0).getFirstItem(true));
    IDateTimeItem arg2 = FunctionUtils.asTypeOrNull(arguments.get(1).getFirstItem(true));
    return ISequence.of(fnDateTimeEqual(dynamicContext, arg1, arg2));
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
  public static IBooleanItem fnDateTimeEqual(@NonNull DynamicContext dynamicContext, IDateTimeItem arg1, IDateTimeItem arg2) {
    return OperationFunctions.opDateTimeEqual(arg1, arg2);
  }
}
