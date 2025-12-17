/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import gov.nist.secauto.metaschema.core.metapath.ContextAbsentDynamicMetapathException;
import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.FocusContext;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.function.IFunction;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IIntegerItem;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implements
 * <a href="https://www.w3.org/TR/xpath-functions-31/#func-last">fn:last</a>.
 * <p>
 * Returns the context size from the dynamic context. This is the number of
 * items in the sequence being processed, and is used with predicates to test if
 * an item is the last in the sequence.
 */
public final class FnLast {
  private static final String NAME = "last";
  @NonNull
  static final IFunction SIGNATURE = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextDependent()
      .focusDependent()
      .returnType(IIntegerItem.type())
      .returnOne()
      .functionHandler(FnLast::execute)
      .build();

  private FnLast() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IIntegerItem> execute(
      @NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    FocusContext focusContext = dynamicContext.getFocusContext();
    if (focusContext == null) {
      throw new ContextAbsentDynamicMetapathException("The context size is absent");
    }
    return ISequence.of(IIntegerItem.valueOf(focusContext.getSize()));
  }
}
