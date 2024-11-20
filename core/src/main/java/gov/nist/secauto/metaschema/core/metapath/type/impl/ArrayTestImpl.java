/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.type.impl;

import gov.nist.secauto.metaschema.core.metapath.type.IArrayTest;
import gov.nist.secauto.metaschema.core.metapath.type.ISequenceType;

import edu.umd.cs.findbugs.annotations.NonNull;

public class ArrayTestImpl implements IArrayTest {
  @NonNull
  private final ISequenceType valueType;

  public ArrayTestImpl(@NonNull ISequenceType valueType) {
    this.valueType = valueType;
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
