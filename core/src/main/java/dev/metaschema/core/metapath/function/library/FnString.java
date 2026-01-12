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
import dev.metaschema.core.metapath.function.InvalidTypeFunctionException;
import dev.metaschema.core.metapath.item.IItem;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import dev.metaschema.core.metapath.item.atomic.IStringItem;
import dev.metaschema.core.metapath.item.node.INodeItem;
import dev.metaschema.core.metapath.type.InvalidTypeMetapathException;
import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implements the XPath 3.1 <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#func-string">fn:string</a>
 * function.
 */
public final class FnString {
  private static final String NAME = "string";
  @NonNull
  static final IFunction SIGNATURE_NO_ARG = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextDependent()
      .focusDependent()
      .returnType(IStringItem.type())
      .returnOne()
      .functionHandler(FnString::executeNoArg)
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
          .type(IItem.type())
          .zeroOrOne()
          .build())
      .returnType(IStringItem.type())
      .returnOne()
      .functionHandler(FnString::executeOneArg)
      .build();

  private FnString() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IStringItem> executeNoArg(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    // the Focus should always be non-null, since the function if focus-dependent
    assert focus != null;

    return ISequence.of(fnStringItem(focus));
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IStringItem> executeOneArg(
      @NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {

    ISequence<?> sequence = FunctionUtils.asType(ObjectUtils.requireNonNull(arguments.get(0)));

    IItem first = sequence.getFirstItem(true);

    return first == null ? ISequence.empty() : ISequence.of(fnStringItem(first));
  }

  /**
   * An implementation of <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-string">fn::string</a>.
   *
   * @param item
   *          the item to get the string value for
   * @return the string value
   */
  @NonNull
  public static IStringItem fnStringItem(@NonNull IItem item) {
    IStringItem retval;
    if (item instanceof INodeItem) {
      retval = IStringItem.valueOf(((INodeItem) item).stringValue());
    } else if (item instanceof IAnyAtomicItem) {
      try {
        retval = ((IAnyAtomicItem) item).asStringItem();
      } catch (IllegalStateException ex) {
        throw new InvalidTypeMetapathException(item, ex.getMessage(), ex);
      }
    } else {
      throw new InvalidTypeFunctionException(InvalidTypeFunctionException.ARGUMENT_TO_STRING_IS_FUNCTION, item);
    }
    return retval;
  }
}
