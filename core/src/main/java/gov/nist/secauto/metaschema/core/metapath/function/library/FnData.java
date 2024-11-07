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
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAtomicValuedItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.IArrayItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implements the XPath 3.1
 * <a href= "https://www.w3.org/TR/xpath-functions-31/#func-data">fn:data</a>
 * functions.
 */
public final class FnData {
  private static final String NAME = "data";
  @NonNull
  static final IFunction SIGNATURE_NO_ARG = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextDependent()
      .focusDependent()
      .returnType(IAnyAtomicItem.class)
      .returnOne()
      .functionHandler(FnData::executeNoArg)
      .build();

  @NonNull
  static final IFunction SIGNATURE_ONE_ARG = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextIndependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("arg1")
          .type(IItem.class)
          .zeroOrMore()
          .build())
      .returnType(IAnyAtomicItem.class)
      .returnOne()
      .functionHandler(FnData::executeOneArg)
      .build();

  private FnData() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IAnyAtomicItem> executeNoArg(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {

    INodeItem item = FunctionUtils.requireTypeOrNull(INodeItem.class, focus);

    ISequence<IAnyAtomicItem> retval;
    if (item == null) {
      retval = ISequence.empty();
    } else {
      IAnyAtomicItem data = fnDataItem(item);
      retval = ISequence.of(data);
    }
    return retval;
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IAnyAtomicItem> executeOneArg(
      @NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {

    ISequence<?> sequence = FunctionUtils.asType(ObjectUtils.requireNonNull(arguments.get(0)));
    return fnData(sequence);
  }

  /**
   * An implementation of XPath 3.1
   * <a href="https://www.w3.org/TR/xpath-functions-31/#func-data">fn:data</a>
   * supporting <a href="https://www.w3.org/TR/xpath-31/#id-atomization">item
   * atomization</a>.
   *
   * @param sequence
   *          the sequence of items to atomize
   * @return the atomized result
   */
  @SuppressWarnings("null")
  @NonNull
  public static ISequence<IAnyAtomicItem> fnData(@NonNull ISequence<? extends IItem> sequence) {
    return ISequence.of(sequence.stream()
        .flatMap(FnData::atomize));
  }

  /**
   * An implementation of
   * <a href="https://www.w3.org/TR/xpath-31/#id-atomization">item
   * atomization</a>.
   *
   * @param item
   *          the item to atomize
   * @return the atomized result
   * @throws InvalidTypeFunctionException
   *           if the item cannot be cast to an atomic value, most likely because
   *           it doesn't have a typed value
   */
  @NonNull
  public static IAnyAtomicItem fnDataItem(@NonNull IItem item) {
    IAnyAtomicItem retval = null;
    if (item instanceof IAtomicValuedItem) {
      retval = ((IAtomicValuedItem) item).toAtomicItem();
    }

    if (retval != null) {
      return retval;
    }
    throw new InvalidTypeFunctionException(InvalidTypeFunctionException.NODE_HAS_NO_TYPED_VALUE, item);
  }

  /**
   * An implementation of
   * <a href="https://www.w3.org/TR/xpath-31/#id-atomization">item
   * atomization</a>.
   *
   * @param item
   *          the item to atomize
   * @return the atomized result
   */
  @NonNull
  public static Stream<IAnyAtomicItem> fnDataItem(@NonNull IArrayItem<?> item) {
    return ObjectUtils.notNull(item.stream().flatMap(member -> {
      Stream<IAnyAtomicItem> result;
      if (member instanceof IItem) {
        result = atomize((IItem) member);
      } else if (member instanceof ISequence) {
        result = ((ISequence<?>) member).stream()
            .flatMap(FnData::atomize);
      } else {
        throw new UnsupportedOperationException("array member not an item or sequence.");
      }
      return result;
    }));
  }

  /**
   * An implementation of
   * <a href="https://www.w3.org/TR/xpath-31/#id-atomization">item
   * atomization</a>.
   *
   * @param item
   *          the item to atomize
   * @return the atomized result
   */
  @NonNull
  public static Stream<IAnyAtomicItem> atomize(@NonNull IItem item) {
    Stream<IAnyAtomicItem> retval;
    if (item instanceof IAnyAtomicItem) {
      retval = ObjectUtils.notNull(Stream.of((IAnyAtomicItem) item));
    } else if (item instanceof IAtomicValuedItem) {
      retval = ObjectUtils.notNull(Stream.of(((IAtomicValuedItem) item).toAtomicItem()));
    } else if (item instanceof IArrayItem) {
      retval = fnDataItem((IArrayItem<?>) item);
    } else {
      throw new InvalidTypeFunctionException(InvalidTypeFunctionException.NODE_HAS_NO_TYPED_VALUE, item);
    }
    return retval;
  }
}
