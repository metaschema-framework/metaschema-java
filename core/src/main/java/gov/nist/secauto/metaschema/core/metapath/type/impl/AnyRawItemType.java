/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.type.impl;

import gov.nist.secauto.metaschema.core.metapath.function.IFunction;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.IArrayItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.IMapItem;
import gov.nist.secauto.metaschema.core.metapath.type.IItemType;

import edu.umd.cs.findbugs.annotations.NonNull;

public final class AnyRawItemType<I extends IItem>
    extends AbstractItemType<I> {
  @NonNull
  public static final IItemType ANY_FUNCTION = new AnyRawItemType<>(
      IFunction.class,
      "function(*)");

  @NonNull
  public static final IItemType ANY_MAP = new AnyRawItemType<>(
      IMapItem.class,
      "map(*)");
  @NonNull
  public static final IItemType ANY_ARRAY = new AnyRawItemType<>(
      IArrayItem.class,
      "array(*)");

  @NonNull
  private final String signature;

  private AnyRawItemType(
      @NonNull Class<? extends I> itemClass,
      @NonNull String signature) {
    super(itemClass);
    this.signature = signature;
  }

  @Override
  public String toSignature() {
    return signature;
  }
}
