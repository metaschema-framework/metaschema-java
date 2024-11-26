/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides a common implementation for all atomic types that have an underlying
 * value.
 *
 * @param <TYPE>
 *          the Java type associated with the atomic type.
 */
public abstract class AbstractAnyAtomicItem<TYPE>
    extends AbstractAtomicItemBase<TYPE> {
  @NonNull
  private final TYPE value;

  /**
   * Construct a new atomic item using the provided {@code value}.
   *
   * @param value
   *          the value to assign to this atomic item
   */
  protected AbstractAnyAtomicItem(@NonNull TYPE value) {
    this.value = ObjectUtils.requireNonNull(value, "value");
  }

  @Override
  @NonNull
  public TYPE getValue() {
    return value;
  }
}
