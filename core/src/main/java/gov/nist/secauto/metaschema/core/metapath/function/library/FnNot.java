/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.function.IArgument;
import gov.nist.secauto.metaschema.core.metapath.function.IFunction;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBooleanItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implements the XPath 3.1
 * <a href= "https://www.w3.org/TR/xpath-functions-31/#func-not">fn:not</a>
 * function.
 */
public final class FnNot {
  private static final String NAME = "not";
  @NonNull
  static final IFunction SIGNATURE = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextIndependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("arg")
          .type(IItem.class)
          .zeroOrMore()
          .build())
      .returnType(IBooleanItem.class)
      .returnOne()
      .functionHandler(FnNot::execute)
      .build();

  private FnNot() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IBooleanItem> execute(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {

    ISequence<?> items = ObjectUtils.requireNonNull(arguments.iterator().next());

    IBooleanItem result = fnNot(items);
    return ISequence.of(result);
  }

  /**
   * Get the negated, effective boolean value of the provided item.
   * <p>
   * Based on the XPath 3.1
   * <a href="https://www.w3.org/TR/xpath-functions-31/#func-not">fn:not</a>
   * function.
   *
   * @param item
   *          the item to get the negated, effective boolean value for
   * @return the negated boolean value
   */
  @NonNull
  public static IBooleanItem fnNot(@NonNull IItem item) {
    return IBooleanItem.valueOf(!FnBoolean.fnBooleanAsPrimitive(item));
  }

  /**
   * Get the negated, effective boolean value of the provided item.
   * <p>
   * Based on the XPath 3.1
   * <a href="https://www.w3.org/TR/xpath-functions-31/#func-not">fn:not</a>
   * function.
   *
   * @param sequence
   *          the sequence to get the negated, effective boolean value for
   * @return the negated boolean value
   */
  @NonNull
  public static IBooleanItem fnNot(@NonNull ISequence<?> sequence) {
    return FnBoolean.fnBoolean(sequence).negate();
  }
}
