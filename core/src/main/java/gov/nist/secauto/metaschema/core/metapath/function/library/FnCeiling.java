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
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.INumericItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implements the XPath 3.1 <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#func-ceiling">fn:ceiling</a>
 * function.
 */
public final class FnCeiling {
  private static final String NAME = "ceiling";

  @NonNull
  static final IFunction SIGNATURE = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextIndependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("arg")
          .type(INumericItem.class)
          .zeroOrOne()
          .build())
      .returnType(INumericItem.class)
      .returnZeroOrOne()
      .functionHandler(FnCeiling::execute)
      .build();

  private FnCeiling() {
    // disable construction
  }

  // CPD-OFF
  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<INumericItem> execute(
      @NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    ISequence<? extends INumericItem> sequence = FunctionUtils.asType(
        ObjectUtils.requireNonNull(arguments.get(0)));

    INumericItem item = sequence.getFirstItem(true);
    if (item == null) {
      return ISequence.empty(); // NOPMD - readability
    }

    return ISequence.of(item.castAsType(item.ceiling()));
    // CPD-ON
  }
}
