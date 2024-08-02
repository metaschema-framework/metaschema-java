/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ICollectionValue;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.function.FunctionUtils;
import gov.nist.secauto.metaschema.core.metapath.function.IArgument;
import gov.nist.secauto.metaschema.core.metapath.function.IFunction;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IIntegerItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.ArrayException;
import gov.nist.secauto.metaschema.core.metapath.item.function.IArrayItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

public final class ArrayGet {
  @NonNull
  public static final IFunction SIGNATURE = IFunction.builder()
      .name("get")
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS_ARRAY)
      .deterministic()
      .contextIndependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("array")
          .type(IArrayItem.class)
          .one()
          .build())
      .argument(IArgument.builder()
          .name("position")
          .type(IIntegerItem.class)
          .one()
          .build())
      .returnType(IItem.class)
      .returnZeroOrOne()
      .functionHandler(ArrayGet::execute)
      .build();

  private ArrayGet() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<?> execute(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    IArrayItem<?> array = FunctionUtils.asType(ObjectUtils.requireNonNull(arguments.get(0).getFirstItem(true)));
    IIntegerItem position = FunctionUtils.asType(ObjectUtils.requireNonNull(arguments.get(1).getFirstItem(true)));

    return get(array, position).asSequence();
  }

  /**
   * An implementation of XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-array-get">array:get</a>.
   *
   * @param <T>
   *          the type of items in the given Metapath array
   * @param target
   *          the array of Metapath items that is the target of retrieval
   * @param positionItem
   *          the integer position of the item to retrieve
   * @return the retrieved item
   * @throws ArrayException
   *           if the position is not in the range of 1 to array:size
   */
  @NonNull
  public static <T extends ICollectionValue> T get(
      @NonNull List<T> target,
      @NonNull IIntegerItem positionItem) {
    return get(target, positionItem.asInteger().intValue());
  }

  /**
   * An implementation of XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-array-get">array:get</a>.
   *
   * @param <T>
   *          the type of items in the given Metapath array
   * @param target
   *          the array of Metapath items that is the target of retrieval
   * @param position
   *          the integer position of the item to retrieve
   * @return the retrieved item
   * @throws ArrayException
   *           if the position is not in the range of 1 to array:size
   */
  @NonNull
  public static <T extends ICollectionValue> T get(
      @NonNull List<T> target,
      int position) {
    try {
      return ObjectUtils.requireNonNull(target.get(position - 1));
    } catch (IndexOutOfBoundsException ex) {
      throw new ArrayException(
          ArrayException.INDEX_OUT_OF_BOUNDS,
          String.format("The index %d is outside the range of values for the array size '%d'.",
              position,
              target.size()),
          ex);
    }
  }
}
