/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.type;

import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.IArrayItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IArrayTest extends IItemType {
  @SuppressWarnings({ "rawtypes" })
  @Override
  default Class<? extends IArrayItem> getItemClass() {
    return IArrayItem.class;
  }

  @NonNull
  ISequenceType getValueType();

  @Override
  default boolean isInstance(IItem item) {
    boolean retval = getItemClass().isInstance(item);
    if (retval) {
      // this is an array
      IArrayItem<?> array = (IArrayItem<?>) item;
      retval = getValueType().matches(array.contentsAsSequence());
    }
    return retval;
  }

  @Override
  default String toSignature() {
    return ObjectUtils.notNull(
        new StringBuilder()
            .append("array(")
            .append(getValueType().toSignature())
            .append(')')
            .toString());
  }
}
