/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.type.impl;

import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.IMapTest;
import gov.nist.secauto.metaschema.core.metapath.type.ISequenceType;

import edu.umd.cs.findbugs.annotations.NonNull;

public class MapTestImpl implements IMapTest {
  // FIXME: share code with ArrayTest?
  @NonNull
  private final IAtomicOrUnionType keyType;
  @NonNull
  private final ISequenceType valueType;

  public MapTestImpl(
      @NonNull IAtomicOrUnionType keyType,
      @NonNull ISequenceType valueType) {
    this.keyType = keyType;
    this.valueType = valueType;
  }

  @Override
  public IAtomicOrUnionType getKeyType() {
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
