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
import gov.nist.secauto.metaschema.core.metapath.function.InvalidArgumentFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyUriItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBooleanItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.INumericItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IUntypedAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implements the XPath 3.1 <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#func-boolean">fn:boolean</a>
 * function.
 */
public final class FnBoolean {
  private static final String NAME = "boolean";
  @NonNull
  static final IFunction SIGNATURE = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextIndependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("arg")
          .type(IItem.class)
          .zeroOrMore()
          .build())
      .returnType(IBooleanItem.class)
      .returnOne()
      .functionHandler(FnBoolean::execute)
      .build();

  private FnBoolean() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IBooleanItem> execute(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {

    ISequence<?> items = ObjectUtils.requireNonNull(arguments.get(0));

    IBooleanItem result = fnBoolean(items);
    return ISequence.of(result);
  }

  /**
   * Get the effective boolean value of the provided sequence.
   * <p>
   * Based on the XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-boolean">fn:boolean</a>
   * function.
   *
   * @param sequence
   *          the sequence to evaluate
   * @return the effective boolean value of the sequence
   */
  @NonNull
  public static IBooleanItem fnBoolean(@NonNull ISequence<?> sequence) {
    return IBooleanItem.valueOf(fnBooleanAsPrimitive(sequence));
  }

  /**
   * A helper method that gets the effective boolean value of the provided
   * sequence based on <a href="https://www.w3.org/TR/xpath-31/#id-ebv">XPath
   * 3.1</a>.
   *
   * @param sequence
   *          the sequence to evaluate
   * @return the effective boolean value
   */
  public static boolean fnBooleanAsPrimitive(@NonNull ISequence<?> sequence) {
    boolean retval = false;
    IItem first = sequence.getFirstItem(false);
    if (first != null) {
      if (first instanceof INodeItem) {
        retval = true;
      } else if (sequence.size() == 1) {
        retval = fnBooleanAsPrimitive(first);
      }
    }
    return retval;
  }

  /**
   * A helper method that gets the effective boolean value of the provided item
   * based on <a href="https://www.w3.org/TR/xpath-31/#id-ebv">XPath 3.1</a>.
   *
   * @param item
   *          the item to evaluate
   * @return the effective boolean value
   */
  public static boolean fnBooleanAsPrimitive(@NonNull IItem item) {
    boolean retval;
    if (item instanceof IBooleanItem) {
      retval = ((IBooleanItem) item).toBoolean();
    } else if (item instanceof INumericItem) {
      retval = ((INumericItem) item).toEffectiveBoolean();
    } else if (item instanceof IStringItem
        || item instanceof IAnyUriItem
        || item instanceof IUntypedAtomicItem) {
      String string = ((IAnyAtomicItem) item).asString();
      retval = !string.isBlank();
    } else {
      throw new InvalidArgumentFunctionException(
          InvalidArgumentFunctionException.INVALID_ARGUMENT_TYPE,
          String.format("Invalid argument type '%s'", item.getClass().getName()));
    }
    return retval;
  }
}
