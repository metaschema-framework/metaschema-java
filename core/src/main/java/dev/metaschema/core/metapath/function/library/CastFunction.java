/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.function.FunctionUtils;
import dev.metaschema.core.metapath.function.IArgument;
import dev.metaschema.core.metapath.function.IFunction;
import dev.metaschema.core.metapath.function.IFunctionExecutor;
import dev.metaschema.core.metapath.item.IItem;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import dev.metaschema.core.metapath.type.AbstractAtomicOrUnionType;
import dev.metaschema.core.metapath.type.IAtomicOrUnionType;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.ObjectUtils;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implements the XPath 3.1
 * <a href= "https://www.w3.org/TR/xpath-functions-31/#casting">casting
 * functions</a>.
 *
 * @param <ITEM>
 *          the Metapath atomic item's Java type
 */
public final class CastFunction<ITEM extends IAnyAtomicItem> implements IFunctionExecutor {
  @NonNull
  private final IAtomicOrUnionType.ICastExecutor<ITEM> castExecutor;

  @NonNull
  static <ITEM extends IAnyAtomicItem> IFunction signature(
      @NonNull IEnhancedQName name,
      @NonNull IAtomicOrUnionType<?> resultingAtomicType,
      @NonNull IAtomicOrUnionType.ICastExecutor<ITEM> executor) {
    return signature(name.getNamespace(), name.getLocalName(), resultingAtomicType, executor);
  }

  @NonNull
  static <ITEM extends IAnyAtomicItem> IFunction signature(
      @NonNull String namespace,
      @NonNull String name,
      @NonNull IAtomicOrUnionType<?> resultingAtomicType,
      @NonNull IAtomicOrUnionType.ICastExecutor<ITEM> executor) {
    return IFunction.builder()
        .name(name)
        .namespace(namespace)
        .deterministic()
        .contextIndependent()
        .focusIndependent()
        .argument(IArgument.builder()
            .name("arg1")
            .type(IAnyAtomicItem.type())
            .zeroOrOne()
            .build())
        .returnType(resultingAtomicType)
        .returnZeroOrOne()
        .functionHandler(newCastExecutor(executor))
        .build();
  }

  @NonNull
  private static <ITEM extends IAnyAtomicItem> CastFunction<ITEM>
      newCastExecutor(@NonNull IAtomicOrUnionType.ICastExecutor<ITEM> executor) {
    return new CastFunction<>(executor);
  }

  private CastFunction(@NonNull AbstractAtomicOrUnionType.ICastExecutor<ITEM> castExecutor) {
    this.castExecutor = castExecutor;
  }

  @Override
  public ISequence<ITEM> execute(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {

    ISequence<? extends IAnyAtomicItem> arg = FunctionUtils.asType(
        ObjectUtils.notNull(arguments.get(0)));

    IAnyAtomicItem item = arg.getFirstItem(true);
    if (item == null) {
      return ISequence.empty(); // NOPMD - readability
    }

    ITEM castItem = castExecutor.cast(item);
    return ISequence.of(castItem);
  }
}
