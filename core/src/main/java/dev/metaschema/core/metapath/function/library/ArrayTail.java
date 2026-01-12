/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import java.util.List;

import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.MetapathConstants;
import dev.metaschema.core.metapath.function.FunctionUtils;
import dev.metaschema.core.metapath.function.IArgument;
import dev.metaschema.core.metapath.function.IFunction;
import dev.metaschema.core.metapath.item.IItem;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.metapath.item.function.IArrayItem;
import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Implements the XPath 3.1 <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#func-array-tail">array:tail</a>
 * function.
 */
public final class ArrayTail {
  private static final String NAME = "tail";
  @NonNull
  static final IFunction SIGNATURE = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS_ARRAY)
      .deterministic()
      .contextIndependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("array")
          .type(IArrayItem.type())
          .one()
          .build())
      .returnType(IItem.type())
      .returnZeroOrOne()
      .functionHandler(ArrayTail::execute)
      .build();

  private ArrayTail() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static <T extends IItem> ISequence<IArrayItem<T>> execute(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    IArrayItem<T> array = FunctionUtils.asType(ObjectUtils.requireNonNull(arguments.get(0).getFirstItem(true)));

    return ISequence.of(tail(array));
  }

  /**
   * An implementation of XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-array-tail">array:tail</a>.
   *
   * @param <T>
   *          the type of items in the given Metapath array
   * @param array
   *          the array to get the tail items from
   * @return the tail items
   */
  @Nullable
  public static <T extends IItem> IArrayItem<T> tail(@NonNull IArrayItem<T> array) {
    return ArraySubarray.subarray(array, 2);
  }
}
