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
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implements the XPath 3.1 <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#func-string-join">fn:string-join</a>
 * function.
 */
public final class FnStringJoin {
  private static final String NAME = "string-join";
  @NonNull
  static final IFunction SIGNATURE_ONE_ARG = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextIndependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("arg1")
          .type(IAnyAtomicItem.type())
          .oneOrMore()
          .build())
      .returnType(IStringItem.type())
      .returnOne()
      .functionHandler(FnStringJoin::execute)
      .build();

  static final IFunction SIGNATURE_TWO_ARG = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextIndependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("arg1")
          .type(IAnyAtomicItem.type())
          .zeroOrMore()
          .build())
      .argument(IArgument.builder()
          .name("arg2")
          .type(IStringItem.type())
          .zeroOrOne()
          .build())
      .returnType(IStringItem.type())
      .returnOne()
      .functionHandler(FnStringJoin::execute)
      .build();

  private FnStringJoin() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IStringItem> execute(
      @NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {

    ISequence<?> arg1 = arguments.get(0);
    IStringItem arg2 = arguments.size() == 1 ? IStringItem.valueOf("")
        : FunctionUtils.asTypeOrNull(arguments.get(1).getFirstItem(true));
    String separator = arg2 == null ? "" : arg2.asString();

    if (arg1.isEmpty()) {
      return ISequence.of(IStringItem.valueOf(""));
    }

    return ISequence.of(stringJoin(ObjectUtils.notNull(arg1.stream()
        .map(arg -> {
          assert arg != null;
          return (IAnyAtomicItem) arg;
        })), separator));
  }

  /**
   * An implementation of XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-string-join">fn:string-join</a>.
   *
   * @param items
   *          the items to concatenate
   * @return the atomized result
   */
  @NonNull
  public static IStringItem stringJoinstringJoin(IAnyAtomicItem items, String separator) {
    return stringJoin(ObjectUtils.notNull(Arrays.asList(items)), separator);
  }

  /**
   * An implementation of XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-string-join">fn:string-join</a>.
   *
   * @param items
   *          the items to concatenate
   * @return the atomized result
   */
  @NonNull
  public static IStringItem stringJoin(@NonNull List<? extends IAnyAtomicItem> items, String separator) {
    return stringJoin(ObjectUtils.notNull(items.stream()), separator);
  }

  /**
   * An implementation of XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-string-join">fn:string-join</a>.
   *
   * @param items
   *          the items to concatenate
   * @return the atomized result
   */
  @NonNull
  public static IStringItem stringJoin(@NonNull Stream<? extends IAnyAtomicItem> items, String separator) {
    return IStringItem.valueOf(ObjectUtils.notNull(items
        .map(item -> item == null ? "" : IStringItem.cast(item).asString())
        .collect(Collectors.joining(separator))));
  }
}