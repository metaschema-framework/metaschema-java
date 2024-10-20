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
import gov.nist.secauto.metaschema.core.metapath.item.function.IArrayItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implements the XPath 3.1 <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#func-array-flatten">array:flatten</a>
 * function.
 */
public final class ArrayFlatten {
  private static final String NAME = "flatten";
  @NonNull
  static final IFunction SIGNATURE = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS_ARRAY)
      .deterministic()
      .contextIndependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("input")
          .type(IItem.class)
          .zeroOrMore()
          .build())
      .returnType(IItem.class)
      .returnZeroOrMore()
      .functionHandler(ArrayFlatten::execute)
      .build();

  private ArrayFlatten() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<?> execute(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    ISequence<?> input = ObjectUtils.requireNonNull(arguments.get(0));

    return ISequence.of(flatten(input));
  }

  /**
   * An implementation of XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-array-flatten">array:flatten</a>.
   *
   * @param items
   *          the items to flatten
   * @return the stream of flattened items
   */
  @SuppressWarnings("null")
  @NonNull
  public static Stream<IItem> flatten(@NonNull List<? extends IItem> items) {
    return items.stream()
        .flatMap(ArrayFlatten::flatten);
  }

  /**
   * An implementation of XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-array-flatten">array:flatten</a>.
   *
   * @param item
   *          the item to flatten
   * @return the stream of flattened items
   */
  @SuppressWarnings("null")
  @NonNull
  public static Stream<IItem> flatten(@NonNull IItem item) {
    return item instanceof IArrayItem
        // flatten the array members
        ? ((IArrayItem<?>) item).stream()
            .flatMap(member -> member.asSequence().stream()
                .flatMap(ArrayFlatten::flatten))
        // use the item
        : ObjectUtils.notNull(Stream.of(item));
  }
}
