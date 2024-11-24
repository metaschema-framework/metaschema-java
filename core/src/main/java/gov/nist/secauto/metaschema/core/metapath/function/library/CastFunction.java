/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.function.FunctionUtils;
import gov.nist.secauto.metaschema.core.metapath.function.IArgument;
import gov.nist.secauto.metaschema.core.metapath.function.IFunction;
import gov.nist.secauto.metaschema.core.metapath.function.IFunctionExecutor;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.type.AbstractAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

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
      @NonNull String namespace,
      @NonNull String name,
      @NonNull IAtomicOrUnionType<?> resulingAtomicType,
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
        .returnType(resulingAtomicType)
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
