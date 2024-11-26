/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

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
  public String toSignature() {
    return ObjectUtils.notNull(new StringBuilder()
        .append(getType().toSignature())
        .append("(")
        .append(getValueSignature())
        .append(")")
        .toString());
  }

  @NonNull
  protected abstract String getValueSignature();

  @NonNull
  @Override
  public String toString() {
    return toSignature();
  }
}
