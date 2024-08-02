/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.info;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.databind.io.BindingException;
import gov.nist.secauto.metaschema.databind.model.IValuedMutable;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public interface IFeatureScalarItemValueHandler
    extends IItemValueHandler<Object>, IValuedMutable {

  default void setValue(@NonNull Object parent, @NonNull String text) {
    Object item = getValueFromString(text);
    setValue(parent, item);
  }

  @Nullable
  default String toStringFromItem(@NonNull Object parent) {
    Object item = getValue(parent);
    return item == null ? null : getJavaTypeAdapter().asString(item);
  }

  default Object getValueFromString(@NonNull String text) {
    return getJavaTypeAdapter().parse(text);
  }

  /**
   * Get the data type adapter supporting the scalar value.
   *
   * @return the data type adapter
   */
  @NonNull
  IDataTypeAdapter<?> getJavaTypeAdapter();

  @Override
  default Object deepCopyItem(Object source, IBoundObject parentInstance) throws BindingException {
    return getJavaTypeAdapter().copy(source);
  }
}
