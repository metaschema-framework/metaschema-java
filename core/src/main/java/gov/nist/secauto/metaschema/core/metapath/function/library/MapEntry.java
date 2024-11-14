/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ICollectionValue;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.function.FunctionUtils;
import gov.nist.secauto.metaschema.core.metapath.function.IArgument;
import gov.nist.secauto.metaschema.core.metapath.function.IFunction;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.IMapItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implements the XPath 3.1 <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#func-map-entry">map:entry</a>
 * function.
 */
public final class MapEntry {
  private static final String NAME = "entry";
  @NonNull
  static final IFunction SIGNATURE = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS_MAP)
      .deterministic()
      .contextIndependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("key")
          .type(IAnyAtomicItem.class)
          .one()
          .build())
      .argument(IArgument.builder()
          .name("item")
          .type(IItem.class)
          .zeroOrMore()
          .build())
      .returnType(IItem.class)
      .returnOne()
      .functionHandler(MapEntry::execute)
      .build();

  private MapEntry() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static <T extends ICollectionValue> ISequence<IMapItem<T>> execute(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    IAnyAtomicItem key = FunctionUtils.asType(ObjectUtils.requireNonNull(arguments.get(0).getFirstItem(true)));
    @SuppressWarnings("unchecked")
    T value = (T) arguments.get(1).toCollectionValue();

    return entry(key, value).asSequence();
  }

  /**
   * An implementation of XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-map-entry">map:entry</a>.
   *
   * @param <T>
   *          the type of items in the given Metapath map
   * @param key
   *          the Metapath map entry key
   * @param value
   *          the Metapath map entry value
   * @return a new array containing the modification
   */
  @NonNull
  public static <T extends ICollectionValue> IMapItem<T> entry(
      @NonNull IAnyAtomicItem key,
      @NonNull T value) {
    return IMapItem.ofEntries(IMapItem.entry(key, value));
  }
}
