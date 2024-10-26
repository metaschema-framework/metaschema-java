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
 * "https://www.w3.org/TR/xpath-functions-31/#func-lower-case">fn:lower-case</a>
 * functions.
 */
public final class FnLowerCase {
  private static final String NAME = "lower-case";

  @NonNull
  static final IFunction SIGNATURE_ONE_ARG = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextIndependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("arg")
          .type(IStringItem.class)
          .zeroOrOne()
          .build())
      .returnType(IStringItem.class)
      .returnOne()
      .functionHandler(FnLowerCase::executeOneArg)
      .build();

  private FnLowerCase() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IStringItem> executeOneArg(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments, @NonNull DynamicContext dynamicContext, IItem focus) {

    // From the XPath 3.1 specification:
    // If the value of $arg is the empty sequence, the zero-length string is returned.
    if (arguments.get(0).size() == 0) {
      return ISequence.of(IStringItem.valueOf(""));
    }

    IStringItem arg = FunctionUtils.asTypeOrNull(arguments.get(0).getFirstItem(true));
    return ISequence.of(fnLowerCase(arg));
  }

  /**
   * An implementation of <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-lower-case">fn:lower-case</a>.
   *
   * @param arg
   *          the string to be converted to lower case
   * @return the resulting string in lower case
   */
  @NonNull
  public static IStringItem fnLowerCase(@NonNull IStringItem arg) {
    return IStringItem.valueOf(arg.toString().toLowerCase());
  }
}