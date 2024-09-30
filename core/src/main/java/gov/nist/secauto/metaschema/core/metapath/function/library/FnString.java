/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.DynamicMetapathException;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.function.FunctionUtils;
import gov.nist.secauto.metaschema.core.metapath.function.IArgument;
import gov.nist.secauto.metaschema.core.metapath.function.IFunction;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidTypeFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implements
 * <a href= "https://www.w3.org/TR/xpath-functions-31/#func-string">fn:string</a>.
 */
public final class FnString {
  @NonNull
  static final IFunction SIGNATURE_NO_ARG = IFunction.builder()
      .name("string")
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextDependent()
      .focusDependent()
      .returnType(IStringItem.class)
      .returnOne()
      .functionHandler(FnString::executeNoArg)
      .build();

  @NonNull
  static final IFunction SIGNATURE_ONE_ARG = IFunction.builder()
      .name("string")
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextIndependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("arg1")
          .type(IItem.class)
          .zeroOrOne()
          .build())
      .returnType(IStringItem.class)
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

    ISequence<IAnyAtomicItem> retval;
    if (focus == null) {
      throw new DynamicMetapathException(DynamicMetapathException.DYNAMIC_CONTEXT_ABSENT, "The context is empty");
    }

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
      retval = ((IAnyAtomicItem) item).asStringItem();
    } else {
      throw new InvalidTypeFunctionException(InvalidTypeFunctionException.ARGUMENT_TO_STRING_IS_FUNCTION, item);
    }
    return retval;
  }
}
