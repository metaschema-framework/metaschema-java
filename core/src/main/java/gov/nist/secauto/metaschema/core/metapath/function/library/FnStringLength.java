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
import gov.nist.secauto.metaschema.core.metapath.function.InvalidTypeFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IIntegerItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Based on the XPath 3.1 <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#func-string-length">fn:string-length</a>
 * functions.
 */
public final class FnStringLength {
  private static final String NAME = "string-length";
  @NonNull
  static final IFunction SIGNATURE_NO_ARG = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextDependent()
      .focusDependent()
      .returnType(IIntegerItem.class)
      .returnOne()
      .functionHandler(FnStringLength::executeNoArg)
      .build();

  @NonNull
  static final IFunction SIGNATURE_ONE_ARG = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextIndependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("arg1")
          .type(IStringItem.class)
          .zeroOrOne()
          .build())
      .returnType(IIntegerItem.class)
      .returnOne()
      .functionHandler(FnStringLength::executeOneArg)
      .build();

  private FnStringLength() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IIntegerItem> executeNoArg(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments, @NonNull DynamicContext dynamicContext, IItem focus) {
    // the focus should always be non-null, since the function if focus-dependent
    assert focus != null;
    return ISequence.of(fnStringLength(focus));
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IIntegerItem> executeOneArg(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments, @NonNull DynamicContext dynamicContext, IItem focus) {

    // From the XPath 3.1 specification:
    // If the value of $arg is the empty sequence, the function returns the
    // xs:integer value zero (0).
    if (arguments.get(0).size() == 0) {
      return ISequence.of(IIntegerItem.valueOf(0));
    }

    IStringItem arg = FunctionUtils.asTypeOrNull(arguments.get(0).getFirstItem(true));
    return ISequence.of(fnStringLength(arg));
  }

  /**
   * An implementation of <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-string-length">fn:string-length</a>.
   *
   * @param item
   *          the item the string for which to compute the length
   * @return the length of the string
   */
  @NonNull
  public static IIntegerItem fnStringLength(@NonNull IItem item) {
    return IIntegerItem.valueOf(FnString.fnStringItem(item).asString().length());
  }
}
