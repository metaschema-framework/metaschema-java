/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.type.impl;

import gov.nist.secauto.metaschema.core.metapath.item.function.IArrayItem;
import gov.nist.secauto.metaschema.core.metapath.type.IArrayType;
import gov.nist.secauto.metaschema.core.metapath.type.ISequenceType;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.NonNull;

public class ArrayTypeImpl implements IArrayType {
  @NonNull
  private final ISequenceType valueType;

  public ArrayTypeImpl(@NonNull ISequenceType valueType) {
    this.valueType = valueType;
  }

  public ISequenceType getValueType() {
    return valueType;
  }

  // @Override
  // public boolean matches(IItem item) {
  // return item instanceof IArrayItem
  // ? ((IArrayItem<?>) item).stream().allMatch(valueType::matches)
  // : false;
  // }

  @SuppressWarnings({ "rawtypes" })
  @Override
  public Class<? extends IArrayItem> getItemClass() {
    return IArrayItem.class;
  }

  @Override
  public String toSignature() {
    return ObjectUtils.notNull(
        new StringBuilder()
            .append("array(")
            .append(getValueType().toSignature())
            .append(')')
            .toString());
  }
}
