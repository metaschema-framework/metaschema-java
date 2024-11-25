/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.type.impl;

import gov.nist.secauto.metaschema.core.metapath.item.function.IArrayItem;
import gov.nist.secauto.metaschema.core.metapath.type.IArrayTest;
import gov.nist.secauto.metaschema.core.metapath.type.ISequenceType;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An item type that applies to all {@link IArrayItem} with a specific value
 * type.
 */
public class ArrayTestImpl implements IArrayTest {
  @NonNull
  private final ISequenceType valueType;

  /**
   * Construct a new item type.
   *
   * @param valueType
   *          the sequence type to match array contents against
   */
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
