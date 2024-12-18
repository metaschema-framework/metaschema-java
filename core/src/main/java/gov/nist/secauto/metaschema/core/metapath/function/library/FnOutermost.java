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
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * /** Implements
 * <a href= "https://www.w3.org/TR/xpath-functions-31/#func-root">fn:root</a>
 * functions.
 */
public final class FnOutermost {
  @NonNull
  private static final String NAME = "outermost";
  @NonNull
  static final IFunction SIGNATURE = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextIndependent()
      .focusDependent()
      .argument(IArgument.builder()
          .name("nodes")
          .type(INodeItem.type())
          .zeroOrMore()
          .build())
      .returnType(INodeItem.type())
      .returnZeroOrMore()
      .functionHandler(FnOutermost::execute)
      .build();

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<? extends INodeItem> execute(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    ISequence<? extends INodeItem> nodes = FunctionUtils.asType(ObjectUtils.requireNonNull(arguments.get(0)));

    return ISequence.of(fnOutermost(nodes.getValue()));
  }

  /**
   * Get every node within the provided list that is not an ancestor of another
   * member of the provided list.
   * <p>
   * The nodes are returned in document order with duplicates eliminated.
   * <p>
   * Based on the XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-innermost">fn:innermost</a>
   * function.
   *
   * @param arg
   *          the node items check
   * @return the nodes that are not an ancestor of another member of the provided
   *         list
   */
  @NonNull
  public static Stream<? extends INodeItem> fnOutermost(@NonNull List<? extends INodeItem> arg) {
    Set<? extends INodeItem> values = new HashSet<>(arg);

    return ObjectUtils.notNull(arg.stream()
        .distinct()
        .filter(node -> !node.ancestor().anyMatch(values::contains)));
  }

  private FnOutermost() {
    // disable construction
  }
}