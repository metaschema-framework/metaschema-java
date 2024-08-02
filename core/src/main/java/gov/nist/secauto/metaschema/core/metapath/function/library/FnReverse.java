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
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implements <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#func-reverse">fn:reverse</a>.
 */
public final class FnReverse {
  @NonNull
  static final IFunction SIGNATURE_ONE_ARG = IFunction.builder()
      .name("reverse")
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextIndependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("target")
          .type(IItem.class)
          .zeroOrMore()
          .build())
      .returnType(IItem.class)
      .returnZeroOrMore()
      .functionHandler(FnReverse::execute)
      .build();

  private FnReverse() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<?> execute(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    ISequence<?> target = FunctionUtils.asType(ObjectUtils.requireNonNull(arguments.get(0)));
    return ISequence.ofCollection(fnReverse(target));
  }

  /**
   * Reverse the order of items in the {@code sequence}.
   *
   * @param <T>
   *          the type for the given Metapath sequence
   * @param target
   *          the sequence to check for reversal
   * @return {@code sequence} the new sequence with items in reverse order.
   *
   */
  @SuppressWarnings("PMD.OnlyOneReturn")
  @NonNull
  public static <T extends IItem> List<T> fnReverse(@NonNull List<T> target) {
    if (target.size() <= 1) {
      return target;
    }
    List<T> newSequence = new ArrayList<>(target);
    Collections.reverse(newSequence);
    return newSequence;
  }
}
