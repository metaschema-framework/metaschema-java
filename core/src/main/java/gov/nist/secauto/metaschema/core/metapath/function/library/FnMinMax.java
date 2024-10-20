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
import gov.nist.secauto.metaschema.core.metapath.function.InvalidArgumentFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyUriItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDecimalItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IUntypedAtomicItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Implements the XPath 3.1
 * <a href="https://www.w3.org/TR/xpath-functions-31/#func-min">fn:min</a> and
 * <a href="https://www.w3.org/TR/xpath-functions-31/#func-max">fn:max</a>
 * functions.
 */
public final class FnMinMax {
  private static final String NAME_MIN = "min";
  private static final String NAME_MAX = "max";

  @NonNull
  static final IFunction SIGNATURE_MIN = IFunction.builder()
      .name(NAME_MIN)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextIndependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("arg")
          .type(IAnyAtomicItem.class)
          .zeroOrMore()
          .build())
      .returnType(IAnyAtomicItem.class)
      .returnZeroOrOne()
      .functionHandler(FnMinMax::executeMin)
      .build();

  @NonNull
  static final IFunction SIGNATURE_MAX = IFunction.builder()
      .name(NAME_MAX)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextIndependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("arg")
          .type(IAnyAtomicItem.class)
          .zeroOrMore()
          .build())
      .returnType(IAnyAtomicItem.class)
      .returnZeroOrOne()
      .functionHandler(FnMinMax::executeMax)
      .build();

  private FnMinMax() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IAnyAtomicItem> executeMin(
      @NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    ISequence<? extends IAnyAtomicItem> sequence = FunctionUtils.asType(
        ObjectUtils.requireNonNull(arguments.get(0)));

    return ISequence.of(min(sequence.getValue()));
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IAnyAtomicItem> executeMax(
      @NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    ISequence<? extends IAnyAtomicItem> sequence = FunctionUtils.asType(
        ObjectUtils.requireNonNull(arguments.get(0)));

    return ISequence.of(max(sequence.getValue()));
  }

  /**
   * An implementation of XPath 3.1
   * <a href="https://www.w3.org/TR/xpath-functions-31/#func-min">fn:min</a>.
   *
   * @param items
   *          the items to find the minimum value for
   * @return the average
   */
  @Nullable
  public static IAnyAtomicItem min(@NonNull List<? extends IAnyAtomicItem> items) {
    return normalize(items)
        .min((item1, item2) -> {
          assert item2 != null;
          return item1.compareTo(item2);
        })
        .orElse(null);
  }

  /**
   * An implementation of XPath 3.1
   * <a href="https://www.w3.org/TR/xpath-functions-31/#func-max">fn:max</a>.
   *
   * @param items
   *          the items to find the maximum value for
   * @return the average
   */
  @Nullable
  public static IAnyAtomicItem max(@NonNull List<? extends IAnyAtomicItem> items) {
    return normalize(items)
        .max((item1, item2) -> {
          assert item2 != null;
          return item1.compareTo(item2);
        })
        .orElse(null);
  }

  @SuppressWarnings("PMD.OnlyOneReturn") // readability
  private static Stream<? extends IAnyAtomicItem> normalize(
      @NonNull List<? extends IAnyAtomicItem> items) {
    if (items.isEmpty()) {
      return Stream.empty();
    }

    if (items.size() == 1) {
      return Stream.of(items.get(0));
    }

    List<? extends IAnyAtomicItem> resultingItems = ObjectUtils.notNull(items.stream()
        .map(item -> item instanceof IUntypedAtomicItem ? IDecimalItem.cast(item) : item)
        .collect(Collectors.toList()));

    Map<Class<? extends IAnyAtomicItem>, Integer> counts = FunctionUtils.countTypes(
        IAnyAtomicItem.PRIMITIVE_ITEM_TYPES,
        resultingItems);

    Stream<? extends IAnyAtomicItem> stream = null;
    if (counts.size() == 1) {
      stream = resultingItems.stream();
    } else if (counts.size() > 1) {
      int size = resultingItems.size();
      if (counts.getOrDefault(IStringItem.class, 0) + counts.getOrDefault(IAnyUriItem.class, 0) == size) {
        stream = resultingItems.stream()
            .map(IAnyAtomicItem::asStringItem);
      } else if (counts.getOrDefault(IDecimalItem.class, 0) == size) {
        stream = resultingItems.stream()
            .map(item -> (IDecimalItem) item);
      }
    }

    if (stream == null) {
      throw new InvalidArgumentFunctionException(
          InvalidArgumentFunctionException.INVALID_ARGUMENT_TYPE,
          String.format("Values must all be of a single atomic type. Their types are '%s'.",
              FunctionUtils.getTypes(resultingItems).stream()
                  .map(Class::getName)
                  .collect(Collectors.joining(","))));
    }
    return stream;
  }
}
