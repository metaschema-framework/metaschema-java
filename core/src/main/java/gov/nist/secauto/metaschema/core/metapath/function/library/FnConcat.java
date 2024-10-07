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
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implements <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#func-concat">fn:concat</a>.
 */
public final class FnConcat {
  private static final String NAME = "concat";
  @NonNull
  static final IFunction SIGNATURE = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextIndependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("arg1")
          .type(IAnyAtomicItem.class)
          .zeroOrOne()
          .build())
      .argument(IArgument.builder()
          .name("arg2")
          .type(IAnyAtomicItem.class)
          .zeroOrOne()
          .build())
      .allowUnboundedArity(true)
      .returnType(IStringItem.class)
      .returnOne()
      .functionHandler(FnConcat::execute)
      .build();

  private FnConcat() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IStringItem> execute(
      @NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {

    return ISequence.of(concat(ObjectUtils.notNull(arguments.stream()
        .map(arg -> {
          assert arg != null;
          return (IAnyAtomicItem) arg.getFirstItem(true);
        }))));
  }

  /**
   * An implementation of XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-concat">fn:concat</a>.
   *
   * @param items
   *          the items to concatenate
   * @return the atomized result
   */
  @NonNull
  public static IStringItem concat(IAnyAtomicItem... items) {
    return concat(ObjectUtils.notNull(Arrays.asList(items)));
  }

  /**
   * An implementation of XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-concat">fn:concat</a>.
   *
   * @param items
   *          the items to concatenate
   * @return the atomized result
   */
  @NonNull
  public static IStringItem concat(@NonNull List<? extends IAnyAtomicItem> items) {
    return concat(ObjectUtils.notNull(items.stream()));
  }

  /**
   * An implementation of XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-concat">fn:concat</a>.
   *
   * @param items
   *          the items to concatenate
   * @return the atomized result
   */
  @NonNull
  public static IStringItem concat(@NonNull Stream<? extends IAnyAtomicItem> items) {
    return IStringItem.valueOf(ObjectUtils.notNull(items
        .map(item -> (item == null ? "" : IStringItem.cast(item).asString()))
        .collect(Collectors.joining())));
  }
}
