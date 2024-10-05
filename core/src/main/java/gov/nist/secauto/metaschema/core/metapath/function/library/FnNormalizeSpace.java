/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.DynamicMetapathException;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.function.FunctionUtils;
import gov.nist.secauto.metaschema.core.metapath.function.IArgument;
import gov.nist.secauto.metaschema.core.metapath.function.IFunction;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implements <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#func-normalize-space">fn:normalize-space</a>.
 */
public final class FnNormalizeSpace {
  @NonNull
  static final IFunction SIGNATURE_NO_ARG = IFunction.builder()
      .name("normalize-space")
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextDependent()
      .focusDependent()
      .returnType(IStringItem.class)
      .returnOne()
      .functionHandler(FnNormalizeSpace::executeNoArg)
      .build();

  @NonNull
  static final IFunction SIGNATURE_ONE_ARG = IFunction.builder()
      .name("normalize-space")
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
      .functionHandler(FnNormalizeSpace::executeOneArg)
      .build();

  private FnNormalizeSpace() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IStringItem> executeNoArg(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {

    ISequence<IAnyAtomicItem> retval;
    if (focus == null) {
      // Per the specification:
      // If no argument is supplied and the context item is absent]then a dynamic error is raised: [[err:XPDY0002].
      throw new DynamicMetapathException(DynamicMetapathException.DYNAMIC_CONTEXT_ABSENT, "The context is empty");
    }

    return ISequence.of(FnString.fnStringItem(focus).normalizeSpace());
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IStringItem> executeOneArg(
      @NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {

    IStringItem value = FunctionUtils.asTypeOrNull(ObjectUtils.requireNonNull(arguments.get(0)).getFirstItem(true));

    return value == null ? ISequence.empty() : ISequence.of(value.normalizeSpace());
  }
}
