/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.function.FunctionUtils;
import gov.nist.secauto.metaschema.core.metapath.function.IArgument;
import gov.nist.secauto.metaschema.core.metapath.function.IFunction;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidTypeFunctionException;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * /** Implements <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#func-distinct-values">fn:distinct-values</a>
 * functions. This implementation does not implement the two-arg variant with
 * collation at this time.
 */
public final class FnDistinctValues {
  @NonNull
  private static final String NAME = "distinct-values";
  @NonNull
  static final IFunction SIGNATURE_ONE_ARG = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextDependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("arg")
          .type(IAnyAtomicItem.type())
          .zeroOrMore()
          .build())
      .returnType(IAnyAtomicItem.type())
      .returnZeroOrMore()
      .functionHandler(FnDistinctValues::executeOneArg)
      .build();

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IAnyAtomicItem> executeOneArg(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    ISequence<IAnyAtomicItem> seq = FunctionUtils.asType(ObjectUtils.requireNonNull(arguments.get(0)));

    return ISequence.of(fnDistinctValues(seq));
  }

  /**
   * Get the first occurrence of each distinct value in the provided list.
   * <p>
   * Based on the XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-distinct-values">fn:distinct-values</a>
   * function.
   *
   * @param values
   *          the items to get destinct values for
   * @return a the list of distinct values
   */
  @NonNull
  public static Stream<IAnyAtomicItem> fnDistinctValues(@NonNull List<IAnyAtomicItem> values) {
    Set<IAnyAtomicItem> distinctValues = new TreeSet<>(FnDistinctValues::compare);
    distinctValues.addAll(values);
    return ObjectUtils.notNull(distinctValues.stream());
  }

  private static int compare(@NonNull IAnyAtomicItem item1, @NonNull IAnyAtomicItem item2) {
    int retval;
    try {
      retval = item1.compareTo(item2);
    } catch (InvalidTypeFunctionException | InvalidValueForCastFunctionException ex) {
      retval = 1;
    }
    return retval;
  }

  private FnDistinctValues() {
    // disable construction
  }
}
