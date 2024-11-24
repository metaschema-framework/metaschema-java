/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.type;

import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.type.impl.AbstractItemType;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An abstract implementation of an atomic type.
 *
 * @param <I>
 *          the Java type of the item supported by the implementation
 */
public abstract class AbstractAtomicOrUnionType<I extends IAnyAtomicItem>
    extends AbstractItemType<I>
    implements IAtomicOrUnionType<I> {
  @NonNull
  private final ICastExecutor<I> castExecutor;

  /**
   * Construct a new atomic type.
   *
   * @param itemClass
   *          the item class this atomic type supports
   * @param castExecutor
   *          the executor used to cast an item to an item of this type
   */
  public AbstractAtomicOrUnionType(
      @NonNull Class<I> itemClass,
      @NonNull ICastExecutor<I> castExecutor) {
    super(itemClass);
    this.castExecutor = castExecutor;
  }

  @Override
  public I cast(IAnyAtomicItem item) {
    return castExecutor.cast(item);
  }
}
