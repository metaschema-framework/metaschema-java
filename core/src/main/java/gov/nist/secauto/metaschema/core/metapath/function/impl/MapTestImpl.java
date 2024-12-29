/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.impl;

import gov.nist.secauto.metaschema.core.metapath.ISequenceType;
import gov.nist.secauto.metaschema.core.metapath.atomic.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.function.IMapItem;
import gov.nist.secauto.metaschema.core.metapath.function.IMapTest;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An item type that applies to all {@link IMapItem} with a specific key and
 * value type.
 */
public class MapTestImpl implements IMapTest {
  @NonNull
  private final IAtomicOrUnionType<?> keyType;
  @NonNull
  private final ISequenceType valueType;

  /**
   * Construct a new item type.
   *
   * @param keyType
   *          the atomic type to match map keys against
   * @param valueType
   *          the sequence type to match map values against
   */
  public MapTestImpl(
      @NonNull IAtomicOrUnionType<?> keyType,
      @NonNull ISequenceType valueType) {
    this.keyType = keyType;
    this.valueType = valueType;
  }

  @Override
  public IAtomicOrUnionType<?> getKeyType() {
    return keyType;
  }

  @Override
  public ISequenceType getValueType() {
    return valueType;
  }

  @Override
  public String toString() {
    return toSignature();
  }
}
