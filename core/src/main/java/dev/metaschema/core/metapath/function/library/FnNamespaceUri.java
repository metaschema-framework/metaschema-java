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
import dev.metaschema.core.metapath.item.atomic.IStringItem;
import dev.metaschema.core.metapath.item.node.IDefinitionNodeItem;
import dev.metaschema.core.metapath.item.node.INodeItem;
import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * /** Implements <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#func-namespace-uri">fn:namespace-uri</a>
 * functions.
 */
public final class FnNamespaceUri {
  @NonNull
  private static final String NAME = "namespace-uri";
  @NonNull
  static final IFunction SIGNATURE_NO_ARG = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextDependent()
      .focusDependent()
      .returnType(IStringItem.type())
      .returnOne()
      .functionHandler(FnNamespaceUri::executeNoArg)
      .build();
  @NonNull
  static final IFunction SIGNATURE_ONE_ARG = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextIndependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("arg")
          .type(INodeItem.type())
          .zeroOrOne()
          .build())
      .returnType(IStringItem.type())
      .returnOne()
      .functionHandler(FnNamespaceUri::executeOneArg)
      .build();

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IStringItem> executeNoArg(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {

    INodeItem arg = FunctionUtils.asType(
        // test that the focus is an INodeItem
        INodeItem.type().test(ObjectUtils.requireNonNull(focus)));

    return ISequence.of(
        IStringItem.valueOf(fnNamespaceUri(arg)));
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IStringItem> executeOneArg(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    INodeItem arg = FunctionUtils.asTypeOrNull(ObjectUtils.requireNonNull(arguments.get(0)).getFirstItem(true));

    return ISequence.of(
        IStringItem.valueOf(arg == null ? "" : fnNamespaceUri(arg)));
  }

  /**
   * Get the namespace URI of the provided node item.
   * <p>
   * Based on the XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-namespace-uri">fn:namespace-uri</a>
   * function.
   *
   * @param arg
   *          the node item to get the namespace URI for
   * @return the namespace URI of the node if it has one, or an empty string
   *         otherwise
   */
  @NonNull
  public static String fnNamespaceUri(@NonNull INodeItem arg) {
    return arg instanceof IDefinitionNodeItem
        ? ((IDefinitionNodeItem<?, ?>) arg).getQName().getNamespace()
        : "";
  }

  private FnNamespaceUri() {
    // disable construction
  }
}
