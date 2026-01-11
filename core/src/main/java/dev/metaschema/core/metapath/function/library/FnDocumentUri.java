/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import java.util.List;

import dev.metaschema.core.metapath.ContextAbsentDynamicMetapathException;
import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.MetapathConstants;
import dev.metaschema.core.metapath.function.FunctionUtils;
import dev.metaschema.core.metapath.function.IArgument;
import dev.metaschema.core.metapath.function.IFunction;
import dev.metaschema.core.metapath.item.IItem;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.metapath.item.atomic.IAnyUriItem;
import dev.metaschema.core.metapath.item.node.IDocumentNodeItem;
import dev.metaschema.core.metapath.item.node.INodeItem;
import dev.metaschema.core.metapath.type.InvalidTypeMetapathException;
import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Implements the XPath 3.1 <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#func-document-uri">fn:document-uri</a>
 * functions.
 */
public final class FnDocumentUri {
  private static final String NAME = "document-uri";
  @NonNull
  static final IFunction SIGNATURE_NO_ARG = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextDependent()
      .focusDependent()
      .returnType(IAnyUriItem.type())
      .returnOne()
      .functionHandler(FnDocumentUri::executeNoArg)
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
          .type(INodeItem.type())
          .zeroOrOne()
          .build())
      .returnType(IAnyUriItem.type())
      .returnOne()
      .functionHandler(FnDocumentUri::executeOneArg)
      .build();

  private FnDocumentUri() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IAnyUriItem> executeNoArg(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {

    ISequence<IAnyUriItem> retval;
    if (focus == null) {
      // Per XPath 3.1: If the context item is absent, dynamic error [err:XPDY0002]
      throw new ContextAbsentDynamicMetapathException(
          "The context item is absent for fn:document-uri()");
    } else if (focus instanceof IDocumentNodeItem) {
      retval = ISequence.of(fnDocumentUri((IDocumentNodeItem) focus));
    } else if (focus instanceof INodeItem) {
      // node item but not a document - return empty sequence per XPath spec
      retval = ISequence.empty();
    } else {
      // not a node at all - throw type error per XPath spec
      throw new InvalidTypeMetapathException(
          focus,
          String.format("Expected type '%s', but the context item was type '%s'.",
              INodeItem.class.getName(),
              focus.getClass().getName()));
    }
    return retval;
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IAnyUriItem> executeOneArg(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {

    ISequence<? extends INodeItem> arg = FunctionUtils.asType(ObjectUtils.requireNonNull(arguments.get(0)));

    INodeItem item = arg.getFirstItem(true);

    return item instanceof IDocumentNodeItem
        ? ISequence.of(fnDocumentUri((IDocumentNodeItem) item))
        : ISequence.empty();
  }

  /**
   * Get the URI of the document.
   * <p>
   * Based on the XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-document-uri">fn:document-uri</a>
   * function.
   *
   * @param document
   *          the document to get the URI for
   * @return the URI of the document or {@code null} if not available
   */
  @Nullable
  public static IAnyUriItem fnDocumentUri(@NonNull IDocumentNodeItem document) {
    return IAnyUriItem.valueOf(document.getDocumentUri());
  }
}
