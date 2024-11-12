/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides base functionality for atomic item implementations.
 *
 * @param <TYPE>
 *          the Java type of the underlying data value
 */
public abstract class AbstractAtomicItemBase<TYPE> implements IAnyAtomicItem {

  @Override
  @NonNull
  public abstract IDataTypeAdapter<TYPE> getJavaTypeAdapter();

  @Override
  public String asString() {
    return getJavaTypeAdapter().asString(getValue());
  }

  @Override
  public String toString() {
    return asString();
  }
}
